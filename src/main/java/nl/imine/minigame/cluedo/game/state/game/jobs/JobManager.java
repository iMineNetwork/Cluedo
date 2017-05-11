package nl.imine.minigame.cluedo.game.state.game.jobs;

import static nl.imine.minigame.cluedo.settings.Setting.GAME_JOB_REQUIRED_AMOUNT;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import nl.imine.minigame.cluedo.settings.JobService;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.Log;
import nl.imine.minigame.timer.Timer;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;


public class JobManager {

    private static JobManager jobManager;

    private List<AvailableJob> availableJobs;
    private List<AvailableJob> jobPool;
    private Random random = new Random();
    private Timer timer;

    public static void init() {
        // Load jobs from file
        JobService jobService = new JobService();
        jobService.init();
        jobManager = new JobManager(jobService);
    }

    public JobManager(JobService jobService) {
        this.availableJobs = jobService.getAvailableJobs();
        this.jobPool = availableJobs; //The available jobs is always the initial job pool
    }

    public void assignJob(CluedoPlayer player){
        if(!jobPool.isEmpty()) {
            AvailableJob job = jobPool.get(random.nextInt(jobPool.size()));

            //Spawn the Item
            Item item = job.getLocation().getWorld().dropItem(job.getLocation(), job.getDisplayItem());

            //Set meta to prevent stacking with other player's items.
            ItemStack itemStack = item.getItemStack();
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(player.getPlayer().getDisplayName());
            itemStack.setItemMeta(meta);
            item.setItemStack(itemStack);

            player.setActiveJob(new Job(job, item));
            player.getPlayer().sendMessage(job.getDescription());
        }
    }

    public void handleJobItemPickup(CluedoPlayer player){
        player.setCompletedJobs(player.getCompletedJobs() + 1);
        player.setActiveJob(null);
        player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

        if(player.getCompletedJobs() < CluedoPlugin.getSettings().getInt(GAME_JOB_REQUIRED_AMOUNT)) {
            Log.info(player.getPlayer().getDisplayName() + " has completed a Job. " + player.getCompletedJobs() + "/" + CluedoPlugin.getSettings().getInt(GAME_JOB_REQUIRED_AMOUNT));
            //Assign a new job
        } else {
            Log.info(player.getPlayer().getDisplayName() + " has finished all of their Jobs. " + player.getCompletedJobs() + "/" + CluedoPlugin.getSettings().getInt(GAME_JOB_REQUIRED_AMOUNT));
            //Give the player a bonus for completing the jobs.
            //TODO Upgrade System for roles?

            //Set inventory
            ItemStack bow = new ItemStack(Material.BOW);
            ItemMeta bowMeta = bow.getItemMeta();
            bowMeta.setUnbreakable(true);
            bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            bow.setItemMeta(bowMeta);

            switch(player.getRole().getRoleType()){
                case BYSTANDER:
                    player.getPlayer().getInventory().setHeldItemSlot(0);
                    player.getPlayer().getInventory().setItem(1, bow);
                    player.getPlayer().getInventory().setItem(9, new ItemStack(Material.ARROW));                    break;
                case DETECTIVE:
                    // As players can shuffle their inventory, check if the off hand is occupied before setting the item.
                    if(player.getPlayer().getInventory().getItemInOffHand() == null) {
                        player.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.TOTEM));
                    } else {
                        player.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.TOTEM));
                    }
                    break;
                case MURDERER:
                    //Set inventory
                    player.getPlayer().getInventory().addItem(bow);
                    player.getPlayer().getInventory().setItem(9, new ItemStack(Material.ARROW));
                    break;
            }
        }
    }

    public void startJobSystem(){
        CluedoPlugin.getGame().getCluedoPlayers().forEach(this::assignJob);

        timer = CluedoPlugin.getTimerManager().createTimer("Job timer", CluedoPlugin.getSettings().getInt(Setting.GAME_JOB_REFRESH_RATE), () -> {
            CluedoPlugin.getGame().getCluedoPlayers().stream()
                    .filter(cluedoPlayer -> cluedoPlayer.getActiveJob() == null)
                    .filter(cluedoPlayer -> cluedoPlayer.getRole().getRoleType() != RoleType.SPECTATOR)
                    .filter(cluedoPlayer -> cluedoPlayer.getRole().getRoleType() != RoleType.LOBBY)
                    .filter(cluedoPlayer -> cluedoPlayer.getCompletedJobs() < CluedoPlugin.getSettings().getInt(GAME_JOB_REQUIRED_AMOUNT))
                    .forEach(this::assignJob);
                    timer.resetTimer(CluedoPlugin.getSettings().getInt(Setting.GAME_JOB_REFRESH_RATE));
                    timer.setStopped(false);
                }
        );
    }

    public void resetJobs(){
        jobPool = availableJobs;
        CluedoPlugin.getGame().getCluedoPlayers().forEach(cluedoPlayer -> cluedoPlayer.setActiveJob(null));
        timer.setStopped(true);
        timer.resetTimer(CluedoPlugin.getSettings().getInt(Setting.GAME_JOB_REFRESH_RATE));
    }

    public static JobManager getInstance(){
        return jobManager;
    }

}
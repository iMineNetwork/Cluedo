package nl.imine.minigame.cluedo.game.state.game.jobs;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import nl.imine.minigame.cluedo.settings.JobService;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.Log;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

public class JobManager {

    private static JobManager jobManager;

    private static JobService jobService = new JobService();

    private List<AvailableJob> availableJobs;
    private List<AvailableJob> jobPool;
    private Random random = new Random();

    public static void init() {
        // Load jobs from file
        jobService.init();
        jobManager = new JobManager(jobService.getAvailableJobs());
    }

    public JobManager(List<AvailableJob> availableJobs) {
        this.availableJobs = availableJobs;
        this.jobPool = availableJobs; //The available jobs is always the initial job pool
    }

    public void assignJob(CluedoPlayer player){
        AvailableJob job = jobPool.get(random.nextInt(jobPool.size()));
        jobPool.remove(job);

        //Spawn the Item
        Item item = job.getLocation().getWorld().spawn(job.getLocation(), Item.class, itemEntity -> {
            itemEntity.setItemStack(job.getDisplayItem());
        });

        player.setActiveJob(new Job(job, item));
        player.getPlayer().sendMessage(job.getDescription());
    }

    public void handleJobItemPickup(CluedoPlayer player){
        player.setCompletedJobs(player.getCompletedJobs() + 1);
        Item item = player.getActiveJob().getJobItem();
        item.remove();

        if(player.getCompletedJobs() < CluedoPlugin.getSettings().getInt(Setting.GAME_JOB_REQUIRED_AMOUNT)) {
            //Assign a new job
            Log.info(player.getPlayer().getDisplayName() + " has completed a Job. " + player.getCompletedJobs() + "/" + Setting.GAME_JOB_REQUIRED_AMOUNT);
            assignJob(player);
        } else {
            Log.info(player.getPlayer().getDisplayName() + " has finished all of their Jobs. " + player.getCompletedJobs() + "/" + Setting.GAME_JOB_REQUIRED_AMOUNT);
            //Give the player a bonus for completing the jobs.

            //TODO Upgrade System for roles?
            switch(player.getRole().getRoleType()){
                case BYSTANDER:
                    player.setRole(RoleType.DETECTIVE);
                    break;
                case DETECTIVE:
                    //TODO What kind of reward should a detective get?
                    break;
                case MURDERER:
                    //Set inventory
                    ItemStack bow = new ItemStack(Material.BOW);
                    ItemMeta bowMeta = bow.getItemMeta();
                    bowMeta.setUnbreakable(true);
                    bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                    bow.setItemMeta(bowMeta);

                    player.getPlayer().getInventory().setItem(1, bow);
                    player.getPlayer().getInventory().setItem(9, new ItemStack(Material.ARROW));
                    break;
            }
        }
    }

    public void resetJobs(){
        jobPool = availableJobs;
    }

    public static JobManager getInstance(){
        return jobManager;
    }

}

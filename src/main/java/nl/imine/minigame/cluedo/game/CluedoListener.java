package nl.imine.minigame.cluedo.game;

import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.player.role.RoleInteractPermission;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.game.state.game.CluedoGame;
import nl.imine.minigame.cluedo.game.state.game.jobs.JobManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CluedoListener implements Listener {

    private List<CluedoPlayer> detectiveTimeout = new ArrayList<>();

    public static void init() {
        Bukkit.getServer().getPluginManager().registerEvents(new CluedoListener(), JavaPlugin.getPlugin(CluedoPlugin.class));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDamage(EntityDamageEvent evt) {
        //Check if entity is a player
        if (!(evt.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) evt.getEntity();

        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(player)) {
            return;
        }

        //Turn off falling damage
        if (evt.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            evt.setCancelled(true);
        }

        if (CluedoPlugin.getGame().getCluedoPlayer(player).getRole().getRoleType() == RoleType.LOBBY
                || CluedoPlugin.getGame().getCluedoPlayer(player).getRole().getRoleType() == RoleType.SPECTATOR) {
            evt.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent evt) {
        Player player = evt.getPlayer();

        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(player)) {
            return;
        }

        //Get Cluedo player object
        CluedoPlugin.getGame().getCluedoPlayers().stream()
                .filter(cPlayer -> cPlayer.getPlayer().equals(player))
                .findFirst().ifPresent(CluedoPlugin.getGame()::onLeave);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {
        Player player = evt.getEntity();

        //Get Cluedo player object
        Optional<CluedoPlayer> optionalCluedoPlayer = CluedoPlugin.getGame().getCluedoPlayers().stream()
                .filter(cPlayer -> cPlayer.getPlayer().equals(player))
                .findFirst();

        if (!optionalCluedoPlayer.isPresent()) {
            return;
        }

        CluedoPlayer cluedoPlayer = optionalCluedoPlayer.get();

        //Handle item drops
        if (cluedoPlayer.getRole().getRoleType().equals(RoleType.DETECTIVE)) {
            Item item = evt.getEntity().getLocation().getWorld()
                    .dropItem(evt.getEntity().getLocation(), new ItemStack(Material.BOW));
            item.setInvulnerable(true);
        }

        //If a detective killed an innocent player, take away their weapons and put them in time-out
        if (cluedoPlayer.getRole().isInnocent()) {
            if (evt.getEntity().getKiller() != null) {
                //Find the killer's object
                CluedoPlayer killerPlayer = CluedoPlugin.getGame().getCluedoPlayers().stream()
                        .filter(cPlayer -> cPlayer.getPlayer().equals(evt.getEntity().getKiller()))
                        .findFirst().orElse(null);
                if (killerPlayer.getRole().getRoleType().isInnocent()) {
                    if (killerPlayer.getRole().getRoleType().equals(RoleType.DETECTIVE)) {

                        //killerPlayer.getPlayer().setExp((killerPlayer.getPlayer().getExpToLevel() / (1 - killerPlayer.getPlayer().getExp())) - 15);
                        killerPlayer.removeXpFromReward(15);
                        //Demote the detective
                        killerPlayer.setRole(RoleType.BYSTANDER);
                        //Drop the bow
                        Item item = killerPlayer.getPlayer().getLocation().getWorld()
                                .dropItem(killerPlayer.getPlayer().getLocation(), new ItemStack(Material.BOW));
                        item.setInvulnerable(true);
                    }
                    killerPlayer.getPlayer().getInventory().clear();
                    //Put the detective in time-out
                    killerPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1, false, false), true);
                    detectiveTimeout.add(killerPlayer);
                    //Remove him from timeout after 30 seconds (20 ticks == 1 second)
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(CluedoPlugin.class), () -> detectiveTimeout.remove(killerPlayer), 30 * 20);
                }
            }
        }

        //Don't drop the inventory
        evt.getDrops().clear();
        CluedoPlugin.getGame().getGameState().handlePlayerDeath(cluedoPlayer);
        player.spigot().respawn();
//        player.teleport(CluedoPlugin.getGameId().getGameState().getRespawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent evt) {
        Player player = evt.getPlayer();

        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(player)) {
            return;
        }

        evt.setRespawnLocation(CluedoPlugin.getGame().getGameState().getRespawnLocation());
    }

    @EventHandler
    public void onPlayerItemPickup(EntityPickupItemEvent evt) {
        if (!(evt.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) evt.getEntity();

        //Get Cluedo player object
        Optional<CluedoPlayer> optionalCluedoPlayer = CluedoPlugin.getGame().getCluedoPlayers().stream()
                .filter(cPlayer -> cPlayer.getPlayer().equals(player))
                .findFirst();

        //Make sure the player is actually participating in this minigame
        if (!optionalCluedoPlayer.isPresent()) {
            return;
        }

        CluedoPlayer cluedoPlayer = optionalCluedoPlayer.get();
        if (evt.getItem().getItemStack().getType().equals(Material.BOW)) {
            //Check if the player is a bystander without a weapon
            if (cluedoPlayer.getRole().getRoleType().equals(RoleType.BYSTANDER) && !detectiveTimeout.contains(cluedoPlayer)) {
                cluedoPlayer.setRole(RoleType.DETECTIVE);
                evt.getItem().remove();
            }
        }

        if (cluedoPlayer.getActiveJob() != null && evt.getItem().equals(cluedoPlayer.getActiveJob().getJobItem())) {
            JobManager.getInstance().handleJobItemPickup(cluedoPlayer);
        }

        //Don't allow pickups as we handle that ourselves
        evt.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPvPDamage(EntityDamageByEntityEvent evt) {
        //Check if entity is a player
        if (!(evt.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) evt.getEntity();

        //Check if the attacker was a player
        Player damager = null;
        boolean wasArrow = false;
        if (!(evt.getDamager() instanceof Player)) {
            if (evt.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) evt.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    damager = (Player) arrow.getShooter();
                    wasArrow = true;
                }
            } else {
                return;
            }
        }
        if (damager == null) {
            damager = (Player) evt.getDamager();
        }

        //Make sure the players are actually participating in this minigame
        if (!(CluedoPlugin.getGame().getPlayers().contains(damager) && CluedoPlugin.getGame().getPlayers().contains(player))) {
            return;
        }

        if (CluedoPlugin.getGame().getGameState().getState().equals(CluedoStateType.IN_GAME)) {
            if (wasArrow) {
                handleArrowDamage(evt);
            } else {
                handleMeleeDamage(evt);
            }
        } else {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerItemDrop(PlayerDropItemEvent pdie) {

        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(pdie.getPlayer())) {
            return;
        }

        if (pdie.getPlayer().getGameMode() != GameMode.CREATIVE) {
            pdie.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent evt) {

        //Make sure the player is actually participating in this minigame
        Optional<CluedoPlayer> optionalCluedoPlayer = CluedoPlugin.getGame().getCluedoPlayers().stream()
                .filter(cPlayer -> cPlayer.getPlayer().equals(evt.getPlayer()))
                .findFirst();
        if (!optionalCluedoPlayer.isPresent()) {
            return;
        }
        CluedoPlayer cluedoPlayer = optionalCluedoPlayer.get();

        //testing to see if the clicked block exists to prevent NullPointerExceptions
        if (evt.getClickedBlock() == null) {
            return;
        }

        //people in Creative get full access to edit the map
        if (evt.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        //Disallow players from taking items from flower pots
        if (isFlowerPot(evt.getClickedBlock().getType())) {
            evt.setCancelled(true);
            return;
        }

        if (CluedoPlugin.getGame().getGameState().getState() == CluedoStateType.PRE_GAME
                && ((evt.getPlayer().getInventory().getItemInMainHand() != null && evt.getPlayer().getInventory().getItemInMainHand().getType() == Material.SPLASH_POTION)
                || (evt.getPlayer().getInventory().getItemInOffHand() != null && evt.getPlayer().getInventory().getItemInOffHand().getType() == Material.SPLASH_POTION))) {
            evt.setCancelled(true);
        }

        //Check if the player can interact with this block
        CluedoPlugin.getGame().getRoleInteractionPermissions().stream()
                .filter(permission -> permission.getType().equals(evt.getClickedBlock().getType()))
                .filter(permission -> !permission.canInteract(cluedoPlayer.getRole().getRoleType()))
                .forEachOrdered(_item -> {
                    evt.setCancelled(true);
                });
    }

    @EventHandler
    private void onEntityBlockInteract(EntityInteractEvent evt) {
        if (!evt.getEntity().getWorld().equals(CluedoPlugin.getGame().getCluedoWorld())) {
            return;
        }

        if (evt.getEntity() instanceof Arrow) {
            if (((Projectile) evt.getEntity()).getShooter() instanceof Player) {
                Player player = (Player) ((Projectile) evt.getEntity()).getShooter();

                //Get Cluedo player object
                CluedoPlayer cluedoPlayer = CluedoPlugin.getGame().getCluedoPlayers().stream()
                        .filter(cPlayer -> cPlayer.getPlayer().equals(player))
                        .findFirst().orElse(null);

                for (RoleInteractPermission permission : CluedoPlugin.getGame().getRoleInteractionPermissions()) {
                    if (permission.getType().equals(evt.getBlock().getType())) {
                        if (!permission.canInteract(cluedoPlayer.getRole().getRoleType())) {
                            evt.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void onPlayerEntityInteract(PlayerInteractEntityEvent evt) {

        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(evt.getPlayer())) {
            return;
        }

        if (evt.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        if (evt.getRightClicked() instanceof ItemFrame) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    private void onPotionConsume(PlayerItemConsumeEvent pice) {

        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(pice.getPlayer())) {
            return;
        }

        //when a player in Creative drinks a potion he doesn't get a bottle, so no need to do anything else
        if (pice.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (pice.getItem().getType() == Material.POTION) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(CluedoPlugin.class), () -> { //delay to allow the bottle to be placed into the inventory
                ItemStack bottle = new ItemStack(Material.GLASS_BOTTLE);
                bottle.setAmount(1);
                pice.getPlayer().getInventory().remove(bottle);
            }, 1L);
        }
    }

    private void handleMeleeDamage(EntityDamageByEntityEvent evt) {
        double woodenSwordMaxDamage = 4;
        if (evt.getDamage() >= woodenSwordMaxDamage) {
            evt.setDamage(100);
            if (evt.getDamager() instanceof Player
                    && CluedoPlugin.getGame().getCluedoPlayer((Player) evt.getDamager()).getRole().getRoleType() == RoleType.MURDERER) {
                CluedoPlugin.getGame().getCluedoPlayer((Player) evt.getDamager()).addXpToReward(8);

            }
        } else {
            evt.setCancelled(true);
        }
    }

    private void handleArrowDamage(EntityDamageByEntityEvent evt) {
        Arrow arrow = (Arrow) evt.getDamager();
        if (!evt.getEntity().equals(arrow.getShooter())) {
            if (arrow.isCritical()) {
                evt.setDamage(100);
            } else {
                evt.setCancelled(true);
            }
        } else {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent flce) {

        //this event can be fired for an NPC, but since we're not interested in them we'll filter them out
        if (!(flce.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) flce.getEntity();

        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(player)) {
            return;
        }

        flce.setCancelled(true);
        player.setSaturation(Float.MAX_VALUE);

    }

    @EventHandler
    public void onInventoryMove(InventoryClickEvent evt) {
        if (!(evt.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) evt.getWhoClicked();
        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(player)) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE) {
            return; //players in GM1 can do anything they want
        }

        if (evt.getCurrentItem() != null
                && (evt.getCurrentItem().getType() == null
                || evt.getCurrentItem().getType() == Material.AIR
                || evt.getCurrentItem().getType() == Material.POTION
                || evt.getCurrentItem().getType() == Material.WHEAT)) {
            return;
        }

        evt.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent evt) {
        //Only care about teleports FROM the cluedo world
        if (!evt.getFrom().getWorld().equals(CluedoPlugin.getGame().getCluedoWorld())) {
            return;
        }

        //Teleports TO the cluedo world are fine
        if (evt.getTo().getWorld().equals(CluedoPlugin.getGame().getCluedoWorld())) {
            return;
        }

        CluedoPlugin.getGame().getCluedoPlayers().stream()
                .filter(cPlayer -> cPlayer.getPlayer().equals(evt.getPlayer()))
                .findFirst()
                .ifPresent(CluedoPlugin.getGame()::onLeave);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent projectileLaunchEvent){
        if(!(projectileLaunchEvent.getEntity() instanceof Arrow)){
            return;
        }
        Arrow arrow = (Arrow) projectileLaunchEvent.getEntity();

        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }
        Player shooter = (Player) arrow.getShooter();

        if (!CluedoPlugin.getGame().getPlayers().contains(shooter)) {
            return;
        }

        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);

    }

    private boolean isFlowerPot(Material material){
        return material.equals(Material.FLOWER_POT)
        || material.equals(Material.POTTED_ACACIA_SAPLING)
        || material.equals(Material.POTTED_ALLIUM)
        || material.equals(Material.POTTED_AZURE_BLUET)
        || material.equals(Material.POTTED_BIRCH_SAPLING)
        || material.equals(Material.POTTED_BLUE_ORCHID)
        || material.equals(Material.POTTED_BROWN_MUSHROOM)
        || material.equals(Material.POTTED_CACTUS)
        || material.equals(Material.POTTED_DANDELION)
        || material.equals(Material.POTTED_DARK_OAK_SAPLING)
        || material.equals(Material.POTTED_DEAD_BUSH)
        || material.equals(Material.POTTED_FERN)
        || material.equals(Material.POTTED_JUNGLE_SAPLING)
        || material.equals(Material.POTTED_OAK_SAPLING)
        || material.equals(Material.POTTED_ORANGE_TULIP)
        || material.equals(Material.POTTED_OXEYE_DAISY)
        || material.equals(Material.POTTED_PINK_TULIP)
        || material.equals(Material.POTTED_POPPY)
        || material.equals(Material.POTTED_RED_MUSHROOM)
        || material.equals(Material.POTTED_RED_TULIP)
        || material.equals(Material.POTTED_SPRUCE_SAPLING)
        || material.equals(Material.POTTED_WHITE_TULIP);
    }

}

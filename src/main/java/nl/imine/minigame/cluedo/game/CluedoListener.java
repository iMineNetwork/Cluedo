package nl.imine.minigame.cluedo.game;

import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CluedoListener implements Listener {

    private List<CluedoPlayer> detectiveTimeout = new ArrayList<>();

    public static void init() {
        Bukkit.getServer().getPluginManager().registerEvents(new CluedoListener(), CluedoPlugin.getInstance());
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
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent evt) {
        Player player = evt.getEntity();

        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(player)) {
            return;
        }

        //Get Cluedo player object
        CluedoPlayer cluedoPlayer = CluedoPlugin.getGame().getCluedoPlayers().stream()
                .filter(cPlayer -> cPlayer.getPlayer().equals(player))
                .findFirst().orElse(null);

        //Handle item drops
        if (cluedoPlayer.getRole().getRoleType().equals(RoleType.DETECTIVE)) {
            evt.getEntity().getLocation().getWorld()
                    .dropItem(evt.getEntity().getLocation(), new ItemStack(Material.BOW));
        }

        //If a detective killed an innocent player, take away their weapons and put them in time-out
        if(cluedoPlayer.getRole().isInnocent()){
            if(evt.getEntity().getKiller() != null){
                //Find the killer's object
                CluedoPlayer killerPlayer = CluedoPlugin.getGame().getCluedoPlayers().stream()
                        .filter(cPlayer -> cPlayer.getPlayer().equals(evt.getEntity().getKiller()))
                        .findFirst().orElse(null);
                if(killerPlayer.getRole().getRoleType().equals(RoleType.DETECTIVE)){
                    //Demote the detective
                    killerPlayer.setRole(RoleType.BYSTANDER);
                    //Drop the bow
                    killerPlayer.getPlayer().getLocation().getWorld()
                            .dropItem(killerPlayer.getPlayer().getLocation(), new ItemStack(Material.BOW));
                    //Put the detective in time-out
                    detectiveTimeout.add(killerPlayer);
                    //Remove him from timeout after 30 seconds (20 ticks == 1 second)
                    Bukkit.getScheduler().runTaskLater(CluedoPlugin.getInstance(), () -> detectiveTimeout.remove(killerPlayer), 30 * 20);
                }
            }
        }

        //Don't drop the inventory
        evt.getDrops().clear();
        CluedoPlugin.getGame().getGameState().handlePlayerDeath(player);
        player.spigot().respawn();
//        player.teleport(CluedoPlugin.getGame().getGameState().getRespawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
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
    public void onPlayerItemPickup(PlayerPickupItemEvent evt) {
        Player player = evt.getPlayer();

        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(player)) {
            return;
        }

        //Get Cluedo player object
        CluedoPlayer cluedoPlayer = CluedoPlugin.getGame().getCluedoPlayers().stream()
                .filter(cPlayer -> cPlayer.getPlayer().equals(player))
                .findFirst().orElse(null);

        //Bystanders on timeout should not be able to pick up a weapon
        if(detectiveTimeout.contains(cluedoPlayer)){
            return;
        }

        //Check if the player is a bystander without a weapon
        if (cluedoPlayer.getRole().getRoleType().equals(RoleType.BYSTANDER)) {
            cluedoPlayer.setRole(RoleType.DETECTIVE);
            evt.getItem().remove();
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
    private void onPlayerInteract(PlayerInteractEvent pie) {

        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(pie.getPlayer())) {
            return;
        }

        //people in Creative get full access to edit the map
        if (pie.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        //disallow every interaction except stone buttons and levers
        if (pie.getClickedBlock() == null || pie.getClickedBlock().getType() == Material.STONE_BUTTON || pie.getClickedBlock().getType() == Material.WOOD_BUTTON || pie.getClickedBlock().getType() == Material.LEVER) {
            return;
        }

        pie.setCancelled(true);
    }


    @EventHandler
    private void onPotionConsume(PlayerItemConsumeEvent pice) {

        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(pice.getPlayer())) {
            return;
        }

        //when a player in Creative dirnks a potion he doesn't get a bottle, so no need to do anything else
        if (pice.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (pice.getItem().getType() == Material.POTION) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(CluedoPlugin.getInstance(), () -> { //delay to allow the bottle to be placed into the inventory
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
        } else {
            evt.setCancelled(true);
        }
    }

    private void handleArrowDamage(EntityDamageByEntityEvent evt) {
        Arrow arrow = (Arrow) evt.getDamager();
        if (arrow.isCritical()) {
            evt.setDamage(100);
        } else {
            evt.setCancelled(true);
        }
    }

}

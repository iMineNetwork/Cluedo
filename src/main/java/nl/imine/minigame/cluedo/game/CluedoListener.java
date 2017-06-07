package nl.imine.minigame.cluedo.game;

import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.player.role.RoleInteractPermission;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import nl.imine.minigame.cluedo.game.meeseeks.MeeseeksManager;

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
    public void onPlayerDisconnect(PlayerQuitEvent evt) {
        Player player = evt.getPlayer();

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
            Item item = evt.getPlayer().getLocation().getWorld()
                    .dropItem(evt.getPlayer().getLocation(), new ItemStack(Material.BOW));
            item.setInvulnerable(true);
        }

        //Don't drop the inventory
        CluedoPlugin.getGame().getGameState().handlePlayerDeath(player);

        CluedoPlugin.getGame().getCluedoPlayers().remove(cluedoPlayer);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {
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
                        //Demote the detective
                        killerPlayer.setRole(RoleType.BYSTANDER);
                        //Drop the bow
                        Item item = killerPlayer.getPlayer().getLocation().getWorld()
                                .dropItem(killerPlayer.getPlayer().getLocation(), new ItemStack(Material.BOW));
                        item.setInvulnerable(true);
                    }
                    killerPlayer.getPlayer().getInventory().clear();
                    //Put the detective in time-out
                    killerPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0, false, false), true);
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
        if (!CluedoPlugin.getGame().getPlayers().contains(evt.getPlayer())) {
            return;
        }

        //testing to see if the clicked block exists to prevent NullPointerExceptions
        if (evt.getClickedBlock() == null) {
            return;
        }

        //people in Creative get full access to edit the map
        if (evt.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        //Disallow players from taking items from flower pots
        if (evt.getClickedBlock().getType().equals(Material.FLOWER_POT)) {
            evt.setCancelled(true);
            return;
        }

        //Get Cluedo player object
        CluedoPlayer cluedoPlayer = CluedoPlugin.getGame().getCluedoPlayers().stream()
                .filter(cPlayer -> cPlayer.getPlayer().equals(evt.getPlayer()))
                .findFirst().orElse(null);

        //Check if the player can interact with this block
        CluedoPlugin.getGame().getRoleInteractionPermissions().stream()
                .filter(permission -> permission.getType().equals(evt.getClickedBlock().getType()))
                .filter(permission -> !permission.canInteract(cluedoPlayer.getRole().getRoleType()))
                .forEachOrdered(_item -> {
                    evt.setCancelled(true);
                });
    }

    @EventHandler
    private void onCarrotOnAStickInteract(PlayerInteractEvent evt) {
        //Make sure the player is actually participating in this minigame
        if (!CluedoPlugin.getGame().getPlayers().contains(evt.getPlayer())) {
            return;
        }

        ItemStack mainHandItem = evt.getPlayer().getInventory().getItemInMainHand();
        ItemStack offhandItem = evt.getPlayer().getInventory().getItemInOffHand();
        if ((mainHandItem.getType().equals(Material.CARROT_STICK) && mainHandItem.getDurability() == 1)) {

            mainHandItem.setAmount(mainHandItem.getAmount() - 1);
            evt.getPlayer().getInventory().setItemInMainHand(mainHandItem);
            evt.setCancelled(true);
            MeeseeksManager.getInstance().createMeeseeks(evt.getPlayer(), evt.getPlayer().getLocation());

        } else if ((offhandItem.getType().equals(Material.CARROT_STICK) && offhandItem.getDurability() == 1)) {
            offhandItem.setAmount(offhandItem.getAmount() - 1);
            evt.getPlayer().getInventory().setItemInOffHand(offhandItem);
            evt.setCancelled(true);
            MeeseeksManager.getInstance().createMeeseeks(evt.getPlayer(), evt.getPlayer().getLocation());
        }

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
    public void onEntityTarget(EntityTargetEvent ete) {

        if (!(ete.getEntityType() == EntityType.ZOMBIE)) {
            return;
        }

        Zombie zombie = (Zombie) ete.getEntity();

        if (!MeeseeksManager.getInstance().isMeeseeksZombie(zombie)) {
            return;
        }

        if (!(ete.getTarget() instanceof Player) || MeeseeksManager.getInstance().getMeeseeksOwner(zombie) != (Player) ete.getTarget()) {
            ete.setCancelled(true);
        }
    }

    @EventHandler
    public void onZombieDamage(EntityDamageByEntityEvent evt) {
        //a player damaged by a zombie
        if (!(evt.getDamager() instanceof Zombie)) {
            return;
        }
        if (!(evt.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) evt.getEntity();
        Zombie zombie = (Zombie) evt.getDamager();

        if (!MeeseeksManager.getInstance().isMeeseeksZombie(zombie)) {
            return;
        }

        if (!(player.getLocation().distance(zombie.getLocation()) >= 10)) {
            evt.setCancelled(true);
            zombie.setTarget(null);
        }

    }

    @EventHandler
    public void onMeeseekDeath(EntityDeathEvent evt) {
        if (!(evt.getEntity() instanceof Zombie)) {
            return;
        }

        Zombie zombie = (Zombie) evt.getEntity();

        if (!MeeseeksManager.getInstance().isMeeseeksZombie(zombie)) {
            return;
        }
        evt.setDroppedExp(0);
        evt.getDrops().clear();

        MeeseeksManager.getInstance().remove(zombie);
    }

}

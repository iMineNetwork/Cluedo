/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.minigame.cluedo.game.meeseeks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.imine.minigame.cluedo.CluedoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

/**
 *
 * @author Dennis
 */
public class MeeseeksManager {

    HashMap<Player, List<Zombie>> meeseekses = new HashMap<>();
    private static MeeseeksManager instance;
    private static final int TARGET_DISTANCE = 7;
    private static final int TELEPORT_DISTANCE = 15;

    public static MeeseeksManager getInstance() {
        if (instance == null) {
            instance = new MeeseeksManager();
        }
        return instance;
    }

    private MeeseeksManager() {

    }

    /**
     * spawns a meeseeks zombie
     *
     * @param player the owner of the meeseeks zombie (the one the zombie should
     * target)
     * @param loc the location where the zombie should be spawned
     */
    public void createMeeseeks(Player player, Location loc) {
        if (!meeseekses.containsKey(player)) {
            meeseekses.put(player, new ArrayList<>());
        }
        Zombie zombie = loc.getWorld().spawn(loc, Zombie.class);

        int meeseeksID = Bukkit.getScheduler().scheduleSyncRepeatingTask(CluedoPlugin.getPlugin(), () -> {
            if (player.getLocation().distance(zombie.getLocation()) > TARGET_DISTANCE && player.getLocation().distance(zombie.getLocation()) < TELEPORT_DISTANCE) {
                if (zombie.getTarget() != player) {
                    zombie.setTarget(player);
                }
            } else if (player.getLocation().distance(zombie.getLocation()) >= TELEPORT_DISTANCE) {
                zombie.teleport(player);
            }
        }, 1l, 10l);

        zombie.setCanPickupItems(false);
        zombie.setCustomName("Mr Meeseeks");
        zombie.setCustomNameVisible(false);
        zombie.setInvulnerable(true);
        zombie.setSilent(false);
        zombie.setBaby(false);
        zombie.addScoreboardTag("MeeseeksID: " + meeseeksID);

        meeseekses.get(player).add(zombie);
    }

    /**
     * kills a given zombie and removes it from the players meeseeks list
     *
     * @param zombie the zombie to remove
     */
    public void remove(Zombie zombie) {
        meeseekses.keySet().forEach(player -> {
            meeseekses.get(player)
                    .stream()
                    .filter(meeseeksZombie -> meeseeksZombie.equals(zombie))
                    .forEachOrdered(targetZombie -> {
                        remove(player, zombie);
                    });
        });
    }

    public void remove(Player player, Zombie zombie) {
        meeseekses.get(player).remove(zombie);
        zombie.getScoreboardTags()
                .stream()
                .filter(tag -> tag.startsWith("MeeseeksID: "))
                .forEach(tag -> {
                    Bukkit.getScheduler().cancelTask(Integer.parseInt(tag.replace("MeeseeksID: ", "")));
                });
        zombie.remove();

    }

    /**
     * gives the owner of a meeseeks zombie
     *
     * @param zombie zombie who's owner should be returned
     * @return the owner of the zombie
     */
    public Player getMeeseeksOwner(Zombie zombie) {
        for (Player player : meeseekses.keySet()) {
            for (Zombie meeseeksZombie : meeseekses.get(player)) {
                if (meeseeksZombie.equals(zombie)) {
                    return player;
                }
            }

        }
        return null;
    }

    /**
     * check if a zombie is a meeseek zombie
     *
     * @param zombie the zombie that should be checked
     * @return if the zombie is a meeseeks zombie
     */
    public boolean isMeeseeksZombie(Zombie zombie) {
        return meeseekses.keySet().stream()
                .anyMatch(p -> meeseekses.get(p).stream()
                .anyMatch(z -> z.equals(zombie)));
    }

    /**
     * remove all meeseekses from a specific player
     *
     * @param player the players who's meeseeks should be removed
     */
    public void removeMeeseekses(Player player) {
        if (!meeseekses.containsKey(player)) {
            return;
        }
        new HashMap<>(meeseekses)
                .get(player)
                .stream()
                .forEach(zombie -> {
                    remove(player, zombie);
                });
    }

    /**
     * remove all meeseekses
     */
    public void removeAllMeeseekses() {
        //using new HashMap<>(meeseekses) to prevent concurrentModificationException
        new HashMap<>(meeseekses)
                .keySet()
                .forEach(player -> new HashMap<>(meeseekses)
                .get(player)
                .stream()
                .forEach(zombie -> {
                    remove(player, zombie);
                }));
    }

}
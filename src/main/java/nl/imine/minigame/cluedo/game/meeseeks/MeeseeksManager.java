/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.minigame.cluedo.game.meeseeks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static MeeseeksManager getInstance() {
        if (instance == null) {
            instance = new MeeseeksManager();
        }
        return instance;
    }

    private MeeseeksManager() {

    }

    public void createMeeseeks(Player player, Location loc) {
        if (!meeseekses.containsKey(player)) {
            meeseekses.put(player, new ArrayList<>());
        }
        Zombie zombie = loc.getWorld().spawn(loc, Zombie.class);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(CluedoPlugin.getPlugin(), () -> {
            if (player.getLocation().distance(zombie.getLocation()) > 7 && player.getLocation().distance(zombie.getLocation()) < 15) {
                if (zombie.getTarget() != player) {
                    zombie.setTarget(player);
                }
            }else if(player.getLocation().distance(zombie.getLocation()) >= 15){
                zombie.teleport(player);
            }
        }, 1l, 10l);

        zombie.setCanPickupItems(false);
        zombie.setCustomName("Mr Meeseeks");
        zombie.setCustomNameVisible(false);
        zombie.setInvulnerable(true);
        zombie.setSilent(false);
        zombie.setBaby(false);

        meeseekses.get(player).add(zombie);
    }

    public void remove(Zombie zombie) {
        meeseekses.keySet().forEach(p -> {
            meeseekses.get(p)
                    .stream()
                    .filter(z -> z.equals(zombie))
                    .forEachOrdered(z -> {
                        meeseekses.get(p).remove(z);
                        z.remove();
                    });
        });
    }

    public Player getMeeseeksOwner(Zombie zombie) {
        for (Player p : meeseekses.keySet()) {
            for (Zombie z : meeseekses.get(p)) {
                if (z.equals(zombie)) {
                    return p;
                }
            }

        }
        return null;
    }

    public boolean isMeeseeksZombie(Zombie zombie) {
        return meeseekses.keySet().stream()
                .anyMatch(p -> meeseekses.get(p).stream()
                .anyMatch(z -> z.equals(zombie)));
    }

}

package nl.imine.minigame.cluedo.util;

import org.bukkit.entity.Player;

public class PlayerUtil {

    public static void cleanPlayer(Player player) {
        player.getInventory().clear();
        player.setFoodLevel(20);
        player.setSaturation(Float.MAX_VALUE);
        player.setWalkSpeed(0.2f);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }
}

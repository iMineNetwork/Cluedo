package nl.imine.minigame.cluedo.util;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class PlayerUtil {

    public static void cleanPlayer(Player player, boolean clearInv) {
        if(clearInv){
            player.getInventory().clear();
            player.getEnderChest().clear();
        }
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(Float.MAX_VALUE);
        player.setWalkSpeed(0.2f);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setFireTicks(0);
        player.setGlowing(false);
    }
}

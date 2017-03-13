package nl.imine.minigame.cluedo.game.state;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import nl.imine.minigame.cluedo.CluedoPlugin;

public class CluedoListener implements Listener {

	public static void init() {
		Bukkit.getServer().getPluginManager().registerEvents(new CluedoListener(), CluedoPlugin.getInstance());
	}

	@EventHandler
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
}

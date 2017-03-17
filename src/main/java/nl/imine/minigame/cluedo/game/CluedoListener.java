package nl.imine.minigame.cluedo.game;

import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.player.role.RoleType;

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

	@EventHandler
	public void onPlayerDead(PlayerDeathEvent evt) {
		Player player = evt.getEntity();

		//Make sure the player is actually participating in this minigame
		if (!CluedoPlugin.getGame().getPlayers().contains(player)) {
			return;
		}

		CluedoPlugin.getGame().getCluedoPlayers().stream()
				.filter(cluedoPlayer -> cluedoPlayer.getPlayer().equals(player))
				.findFirst()
				.ifPresent(cludeoPlayer -> cludeoPlayer.setRole(RoleType.LOBBY));
	}

	@EventHandler
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

		if(CluedoPlugin.getGame().getGameState().getState().equals(CluedoStateType.IN_GAME)) {
			if (wasArrow) {
				handleArrowDamage(evt);
			} else {
				handleMeleeDamage(evt);
			}
		} else {
			evt.setCancelled(true);
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

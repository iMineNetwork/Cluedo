package nl.imine.minigame.cluedo.game.state.pregame;

import java.util.Optional;
import java.util.logging.Logger;

import nl.imine.minigame.cluedo.game.player.role.RoleType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.PlayerUtil;
import nl.imine.minigame.timer.Timer;
import nl.imine.minigame.timer.TimerHandler;

import javax.swing.text.html.Option;

public class CluedoPreGame extends CluedoState implements TimerHandler {

	private Logger logger = JavaPlugin.getPlugin(CluedoPlugin.class).getLogger();

	private CluedoMinigame cluedoMinigame;
	private int gameTimer = CluedoPlugin.getSettings().getInt(Setting.PRE_GAME_TIME);
	private Location spawnLocation = CluedoPlugin.getSettings().getLocation(Setting.PRE_GAME_SPAWN);
	private Timer timer;

	public CluedoPreGame(CluedoMinigame cluedoMinigame) {
		super(CluedoStateType.PRE_GAME);
		this.cluedoMinigame = cluedoMinigame;
	}

	@Override
	public void handleStateChange() {
		logger.finer("Handling state change for: " + this.getClass().getSimpleName());
		this.timer = CluedoPlugin.getTimerManager().createTimer("Preparation", gameTimer, this);
		cluedoMinigame.getCluedoPlayers().forEach(this::handlePlayer);
	}

	@Override
	public void handleStateEnd() {
		logger.finer("Handling state end for: " + this.getClass().getSimpleName());
		this.timer.setStopped(true);
		cluedoMinigame.getPlayers().forEach(this.timer::hideTimer);
	}

	@Override
	public void onTimerEnd() {
		logger.finest("Handling timer end for: " + this.getClass().getSimpleName());

		//Reveal all players to each other
		for (CluedoPlayer subjectPlayer : cluedoMinigame.getCluedoPlayers()) {
			for (CluedoPlayer targetPlayer : cluedoMinigame.getCluedoPlayers()) {
				if(subjectPlayer != targetPlayer){
					subjectPlayer.getPlayer().showPlayer(JavaPlugin.getPlugin(CluedoPlugin.class), targetPlayer.getPlayer());
				}
			}
		}

		cluedoMinigame.changeGameState(CluedoStateType.IN_GAME);
	}

	@Override
	public CluedoStateType getState() {
		return cluedoStateType;
	}

	@Override
	public void handlePlayer(CluedoPlayer cluedoPlayer) {
		PlayerUtil.cleanPlayer(cluedoPlayer.getPlayer(), false);
		cluedoPlayer.getPlayer().teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);

		//Hide all other players
		for (CluedoPlayer targetPlayer : cluedoMinigame.getCluedoPlayers()) {
			if(cluedoPlayer != targetPlayer){
				cluedoPlayer.getPlayer().hidePlayer(JavaPlugin.getPlugin(CluedoPlugin.class), targetPlayer.getPlayer());
			}
		}

		//Set the introductory title texts
		String titleText;
		String subtitleText;
		switch (cluedoPlayer.getRole().getRoleType()) {
			case MURDERER:
				titleText = String.format("%sYou are the murderer", ChatColor.DARK_RED);
				subtitleText = String.format("%sFind and kill all the innocents", ChatColor.RED);
				break;
			case DETECTIVE:
				titleText = String.format("%sYou are a detective", ChatColor.BLUE);
				subtitleText = String.format("%sThere is a murderer on the loose. Find and kill him", ChatColor.AQUA);
				break;
			case BYSTANDER:
				titleText = String.format("%sYou are a bystander", ChatColor.BLUE);
				subtitleText = String.format("%sThere is a murderer on the loose. Don't get caught", ChatColor.AQUA);
				break;
			case SPECTATOR:
			case LOBBY:
			default:
				titleText = String.format("%sYou are... not assigned correctly", ChatColor.DARK_PURPLE);
				subtitleText = String.format("%sPlease report this to one of the members of staff", ChatColor.LIGHT_PURPLE);
				break;
		}
		cluedoPlayer.getPlayer().sendTitle(titleText, subtitleText,10 ,(timer.getTimer()*20),10);

		//Blind the players for the remainder of the preparation
		cluedoPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ((timer.getTimer()+1) * 20), 0, true, false), true);
	}

	@Override
	public void handlePlayerDeath(CluedoPlayer cluedoPlayer) {
		cluedoPlayer.getPlayer().teleport(spawnLocation);
	}

	@Override
	public Location getRespawnLocation() {
		return spawnLocation;
	}

	@Override
	public void handlePlayerLeave(CluedoPlayer cluedoPlayer) {
		timer.hideTimer(cluedoPlayer.getPlayer());
		if(cluedoPlayer.getRole().getRoleType().equals(RoleType.DETECTIVE) || cluedoPlayer.getRole().getRoleType().equals(RoleType.MURDERER)) {
			cluedoMinigame.getPlayers().forEach(player -> player.sendMessage("Game aborted due to the " + cluedoPlayer.getRole().getRoleType().name().toLowerCase() + " leaving the game."));
			cluedoMinigame.changeGameState(CluedoStateType.LOBBY);
		}
	}
}

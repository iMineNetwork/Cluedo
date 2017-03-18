package nl.imine.minigame.cluedo.game.state.pregame;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.Log;
import nl.imine.minigame.timer.Timer;
import nl.imine.minigame.timer.TimerHandler;

public class CluedoPreGame implements CluedoState, TimerHandler {

	public static final CluedoStateType cluedoStateType = CluedoStateType.PRE_GAME;

	private CluedoMinigame cluedoMinigame;
	private int gameTimer = CluedoPlugin.getSettings().getInt(Setting.PRE_GAME_TIME);
	private Location spawnLocation = CluedoPlugin.getSettings().getLocation(Setting.LOBBY_SPAWN);
	private Timer timer;

	public CluedoPreGame(CluedoMinigame cluedoMinigame) {
		this.cluedoMinigame = cluedoMinigame;
	}

	@Override
	public void handleStateChange() {
		Log.finer("Handling state change for: " + this.getClass().getSimpleName());
		this.timer = CluedoPlugin.getTimerManager().createTimer("Preperation", gameTimer, this);
		cluedoMinigame.getPlayers().forEach(this::handlePlayer);
	}

	@Override
	public void onTimerEnd() {
		Log.finest("Handling timer end for: " + this.getClass().getSimpleName());
		cluedoMinigame.changeGameState(CluedoStateType.IN_GAME);
	}

	@Override
	public CluedoStateType getState() {
		return cluedoStateType;
	}

	@Override
	public void handlePlayer(Player player) {
		player.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);

		//Find the player's game object.
		CluedoPlayer cluedoPlayer = cluedoMinigame.getCluedoPlayers().stream()
				.filter(registeredPlayer -> registeredPlayer.getPlayer().equals(player))
				.findAny()
				.orElse(null);

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
		player.sendTitle(titleText, subtitleText,10 ,(timer.getTimer()*20),10);

		//Blind the players for the remainder of the preparation
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ((timer.getTimer()+1) * 20), 0, true, false), true);
	}

	@Override
	public void handlePlayerDeath(Player player) {
	}

	@Override
	public Location getRespawnLocation() {
		return spawnLocation;
	}
}

package nl.imine.minigame.cluedo.game.state.game;

import java.util.List;
import java.util.Random;

import com.sun.scenario.Settings;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.Log;
import nl.imine.minigame.cluedo.util.PlayerUtil;
import nl.imine.minigame.timer.Timer;
import nl.imine.minigame.timer.TimerHandler;

public class CluedoGame implements CluedoState, TimerHandler {

	//Dependencies
	private CluedoMinigame cluedoMinigame;

	//Settings
	private Location respawnLocation = CluedoPlugin.getSettings().getLocation(Setting.LOBBY_SPAWN);
	private List<CluedoSpawn> spawns = CluedoPlugin.getSpawnLocationService().getSpawns();
	private int gameTimer = CluedoPlugin.getSettings().getInt(Setting.IN_GAME_TIME);
	public static final CluedoStateType cluedoStateType = CluedoStateType.IN_GAME;

	//Game variables
	private Timer timer;
	private BukkitTask footprintHandler;
	private boolean started = false;

	public CluedoGame(CluedoMinigame cluedoMinigame) {
		this.cluedoMinigame = cluedoMinigame;
	}

	@Override
	public void handleStateChange() {
		Log.finer("Handling state change for: " + this.getClass().getSimpleName());
		this.timer = CluedoPlugin.getTimerManager().createTimer(CluedoPlugin.getInstance().getName(), gameTimer, this);
		cluedoMinigame.getPlayers().forEach(this::handlePlayer);
		footprintHandler = Bukkit.getScheduler().runTaskTimer(CluedoPlugin.getInstance(), new FootprintHandler(), 0, 20);
		started = true;
	}

	@Override
	public void onTimerEnd() {
		Log.finest("Handling timer end for: " + this.getClass().getSimpleName());
		endGame(GameResult.STALEMATE);
	}

	@Override
	public CluedoStateType getState() {
		return cluedoStateType;
	}

	@Override
	public void handlePlayer(Player player) {
		if (!started) {
			timer.showTimer(player);
			spawns = CluedoPlugin.getSpawnLocationService().getSpawns();
			player.teleport(spawns.get(new Random().nextInt(spawns.size())).getLocation());
		} else {
			player.teleport(respawnLocation);
		}
	}

	@Override
	public void handlePlayerDeath(Player player) {
		//Clear the player of his items and put him back in the lobby.
		PlayerUtil.cleanPlayer(player);

		//Find the player's game object.
		CluedoPlayer cluedoPlayer = cluedoMinigame.getCluedoPlayers().stream()
				.filter(registeredPlayer -> registeredPlayer.getPlayer().equals(player))
				.findAny()
				.orElse(null);

		//Make the player a spectator
		cluedoPlayer.setRole(RoleType.SPECTATOR);

		//Check if the game has to end
		GameResult result = checkGameEnd();
		if (result != null) {
			endGame(result);
		}

	}

	@Override
	public Location getRespawnLocation() {
		return respawnLocation;
	}

	private GameResult checkGameEnd() {
		boolean murdererAlive = false;
		boolean innocentsAlive = false;

		for (CluedoPlayer cluedoPlayer : cluedoMinigame.getCluedoPlayers()) {
			//Check if there is still a murderer
			if (!murdererAlive) {
				if (cluedoPlayer.getRole().getRoleType().equals(RoleType.MURDERER)) {
					murdererAlive = true;
				}
			}

			//Check if there are any bystanders left
			if (!innocentsAlive) {
				if (cluedoPlayer.getRole().isInnocent()) {
					innocentsAlive = true;
				}
			}
		}

		if (!murdererAlive) {
			return GameResult.BYSTANDER_WIN;
		}

		if (!innocentsAlive) {
			return GameResult.MURDERER_WIN;
		}

		return null;
	}

	private void endGame(GameResult result) {

		//Setup end game notifications
		String titleText = null;
		String subtitleText = null;
		switch (result) {
			case BYSTANDER_WIN:
				titleText = ChatColor.BLUE + "Bystanders Win";
				break;
			case MURDERER_WIN:
				titleText = ChatColor.RED + "Murderer Wins";
				break;
			//Will run when the timer has finished or the game stops for any other reason
			case STALEMATE:
			default:
				titleText = ChatColor.DARK_PURPLE+ "Time limit reached";
				break;
		}

		for (CluedoPlayer player : cluedoMinigame.getCluedoPlayers()) {
			//Show the set-up title to all players in the minigame instance
			player.getPlayer().sendTitle(titleText, subtitleText, 10 ,100,10);

			//Hide Timer
			timer.hideTimer(player.getPlayer());

			//Remove footprints
			player.clearFootprints();
		}

		//Hide timer and prevent it from ticking
		CluedoPlugin.getTimerManager().removeTimer(timer);

		//Clean map
		cluedoMinigame.getCluedoWorld().getEntitiesByClasses(Arrow.class, Item.class)
				.forEach(Entity::remove);

		//Remove footprint runner
		footprintHandler.cancel();
		footprintHandler = null;

		//Change state
		cluedoMinigame.changeGameState(CluedoStateType.END_GAME);
	}
}

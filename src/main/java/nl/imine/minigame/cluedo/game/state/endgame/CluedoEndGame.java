package nl.imine.minigame.cluedo.game.state.endgame;

import java.util.logging.Logger;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.meeseeks.MeeseeksManager;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.game.state.game.jobs.JobManager;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.PlayerUtil;
import nl.imine.minigame.timer.Timer;
import nl.imine.minigame.timer.TimerHandler;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CluedoEndGame extends CluedoState implements TimerHandler {

    private Logger logger = JavaPlugin.getPlugin(CluedoPlugin.class).getLogger();

    private Location respawnLocation = CluedoPlugin.getSettings().getLocation(Setting.LOBBY_SPAWN);

    private CluedoMinigame cluedoMinigame;
    private int gameTimer = CluedoPlugin.getSettings().getInt(Setting.END_GAME_TIME);
    private Timer timer;

    public CluedoEndGame(CluedoMinigame cluedoMinigame) {
        super(CluedoStateType.END_GAME);
        this.cluedoMinigame = cluedoMinigame;
    }

    @Override
    public void handleStateChange() {
        logger.finer("Handling state change for: " + this.getClass().getSimpleName());
        this.timer = CluedoPlugin.getTimerManager().createTimer(CluedoPlugin.getSettings().getString(Setting.GAME_NAME), gameTimer, this);
        cluedoMinigame.getPlayers().forEach(this::handlePlayer);
        JobManager.getInstance().resetJobs();
    }

    @Override
    public void onTimerEnd() {
        logger.finest("Handling timer end for: " + this.getClass().getSimpleName());
        cluedoMinigame.getPlayers().forEach(timer::hideTimer);
        MeeseeksManager.getInstance().removeAllMeeseekses();
        cluedoMinigame.changeGameState(CluedoStateType.LOBBY);
    }

    @Override
    public CluedoStateType getState() {
        return cluedoStateType;
    }

    @Override
    public void handlePlayer(Player player) {
        PlayerUtil.cleanPlayer(player, false);
        timer.showTimer(player);

        //Find the player's game object.
        CluedoPlayer cluedoPlayer = cluedoMinigame.getCluedoPlayers().stream()
                .filter(registeredPlayer -> registeredPlayer.getPlayer().equals(player))
                .findAny()
                .orElse(null);

        //Clear the job data of this player
        if (cluedoPlayer.getActiveJob() != null) {
            cluedoPlayer.getActiveJob().getJobItem().remove();
        }
        cluedoPlayer.setActiveJob(null);
        cluedoPlayer.setCompletedJobs(0);
    }

    @Override
    public void handlePlayerDeath(Player player) {
        //Clear the player of his items and put him back in the lobby.
        PlayerUtil.cleanPlayer(player, true);

        //Find the player's game object.
        CluedoPlayer cluedoPlayer = cluedoMinigame.getCluedoPlayers().stream()
                .filter(registeredPlayer -> registeredPlayer.getPlayer().equals(player))
                .findAny()
                .orElse(null);

        cluedoPlayer.setRole(RoleType.SPECTATOR);

        player.teleport(respawnLocation);
    }

    @Override
    public Location getRespawnLocation() {
        return respawnLocation;
    }
}

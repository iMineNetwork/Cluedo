package nl.imine.minigame.cluedo.game.state.endgame;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
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
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

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
        cluedoMinigame.getCluedoPlayers().forEach(this::handlePlayer);
        JobManager.getInstance().resetJobs();
    }

    @Override
    public void handleStateEnd() {
        logger.finer("Handling end change for: " + this.getClass().getSimpleName());
        this.timer.setStopped(true);
        cluedoMinigame.getPlayers().forEach(this.timer::hideTimer);

        //Clear all arrows from the world
        cluedoMinigame.getCluedoWorld().getEntitiesByClasses(Arrow.class, Item.class)
                .forEach(Entity::remove);
    }

    @Override
    public void onTimerEnd() {
        logger.finest("Handling timer end for: " + this.getClass().getSimpleName());
        cluedoMinigame.changeGameState(CluedoStateType.LOBBY);
    }

    @Override
    public CluedoStateType getState() {
        return cluedoStateType;
    }

    @Override
    public void handlePlayer(CluedoPlayer cluedoPlayer) {
        PlayerUtil.cleanPlayer(cluedoPlayer.getPlayer(), false);
        timer.showTimer(cluedoPlayer.getPlayer());

        //Clear the job data of this player
        if (cluedoPlayer.getActiveJob() != null) {
            cluedoPlayer.getActiveJob().getJobItem().remove();
        }
        cluedoPlayer.setActiveJob(null);
        cluedoPlayer.setCompletedJobs(0);
    }

    @Override
    public void handlePlayerDeath(CluedoPlayer cluedoPlayer) {
        //Clear the player of his items and put him back in the lobby.
        PlayerUtil.cleanPlayer(cluedoPlayer.getPlayer(), true);

        cluedoPlayer.setRole(RoleType.SPECTATOR);
        cluedoPlayer.getPlayer().teleport(respawnLocation);
    }

    @Override
    public Location getRespawnLocation() {
        return respawnLocation;
    }

    @Override
    public void handlePlayerLeave(CluedoPlayer cluedoPlayer) {
        timer.hideTimer(cluedoPlayer.getPlayer());

        //Clear the player of their items
        PlayerUtil.cleanPlayer(cluedoPlayer.getPlayer(), true);
    }
}

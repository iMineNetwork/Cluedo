package nl.imine.minigame.cluedo.game.state.lobby;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.Log;
import nl.imine.minigame.timer.Timer;
import nl.imine.minigame.timer.TimerHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CluedoLobby implements CluedoState, TimerHandler{

    public static final CluedoStateType cluedoStateType = CluedoStateType.LOBBY;

    private CluedoMinigame cluedoMinigame;
    private int gameTimer = CluedoPlugin.getSettings().getInt(Setting.LOBBY_TIME);
    private Location spawnLocation = CluedoPlugin.getSettings().getLocation(Setting.LOBBY_SPAWN);
    private Timer timer;

    public CluedoLobby(CluedoMinigame cluedoMinigame){
        this.cluedoMinigame = cluedoMinigame;
    }

    @Override
    public void handleStateChange() {
        Log.info("Handling state change for: " + this.getClass().getSimpleName());
        this.timer = CluedoPlugin.getTimerManager().createTimer("Lobby", gameTimer, this);
        cluedoMinigame.getPlayers().forEach(this::handlePlayer);
    }

    @Override
    public void onTimerEnd() {
        Log.info("Handling timer end for: " + this.getClass().getSimpleName());
        cluedoMinigame.getPlayers().forEach(timer::hideTimer);
        cluedoMinigame.changeGameState(CluedoStateType.PRE_GAME);
    }

    @Override
    public CluedoStateType getState() {
        return cluedoStateType;
    }

    @Override
    public void handlePlayer(Player player) {
        timer.showTimer(player);
        player.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
}

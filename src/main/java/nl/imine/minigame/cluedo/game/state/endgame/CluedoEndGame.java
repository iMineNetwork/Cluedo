package nl.imine.minigame.cluedo.game.state.endgame;

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

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CluedoEndGame implements CluedoState, TimerHandler{

    public static final CluedoStateType cluedoStateType = CluedoStateType.END_GAME;
    private Location respawnLocation = CluedoPlugin.getSettings().getLocation(Setting.LOBBY_SPAWN);


    private CluedoMinigame cluedoMinigame;
    private int gameTimer = CluedoPlugin.getSettings().getInt(Setting.END_GAME_TIME);
    private Timer timer;

    public CluedoEndGame(CluedoMinigame cluedoMinigame){
        this.cluedoMinigame = cluedoMinigame;
    }

    @Override
    public void handleStateChange() {
        Log.finer("Handling state change for: " + this.getClass().getSimpleName());
        this.timer = CluedoPlugin.getTimerManager().createTimer(CluedoPlugin.getInstance().getName(), gameTimer, this);
        cluedoMinigame.getPlayers().forEach(this::handlePlayer);
    }

    @Override
    public void onTimerEnd() {
        Log.finest("Handling timer end for: " + this.getClass().getSimpleName());
        cluedoMinigame.getPlayers().forEach(timer::hideTimer);
        cluedoMinigame.changeGameState(CluedoStateType.LOBBY);
    }

    @Override
    public CluedoStateType getState() {
        return cluedoStateType;
    }

    @Override
    public void handlePlayer(Player player) {
        timer.showTimer(player);
    }

    @Override
    public void handlePlayerDeath(Player player) {
        //Clear the player of his items and put him back in the lobby.
        PlayerUtil.cleanPlayer(player);
        player.teleport(respawnLocation);

        //Find the player's game object.
        CluedoPlayer cluedoPlayer = cluedoMinigame.getCluedoPlayers().stream()
                .filter(registeredPlayer -> registeredPlayer.getPlayer().equals(player))
                .findAny()
                .orElse(null);

        cluedoPlayer.setRole(RoleType.SPECTATOR);
    }
}

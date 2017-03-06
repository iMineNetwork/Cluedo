package nl.imine.minigame.cluedo.game.state.pregame;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.util.Log;
import nl.imine.minigame.timer.Timer;
import nl.imine.minigame.timer.TimerHandler;
import org.bukkit.entity.Player;

public class CluedoPreGame implements CluedoState, TimerHandler{

    public static final int SETTINGS_PRE_GAME_TIME = 10;
    public static final CluedoStateType cluedoStateType = CluedoStateType.PRE_GAME;

    private CluedoMinigame cluedoMinigame;

    private Timer timer;

    public CluedoPreGame(CluedoMinigame cluedoMinigame){
        this.cluedoMinigame = cluedoMinigame;
    }

    @Override
    public void handleStateChange() {
        Log.info("Handling state change for: " + this.getClass().getSimpleName());
        this.timer = CluedoPlugin.getTimerManager().createTimer("Preperation", SETTINGS_PRE_GAME_TIME, this);
        cluedoMinigame.getPlayers().forEach(timer::showTimer);
    }

    @Override
    public void onTimerEnd() {
        Log.info("Handling timer end for: " + this.getClass().getSimpleName());
        cluedoMinigame.getPlayers().forEach(timer::hideTimer);
        cluedoMinigame.changeGameState(CluedoStateType.IN_GAME);
    }

    @Override
    public CluedoStateType getState() {
        return cluedoStateType;
    }

    @Override
    public void handlePlayer(Player player) {
        timer.showTimer(player);
    }
}

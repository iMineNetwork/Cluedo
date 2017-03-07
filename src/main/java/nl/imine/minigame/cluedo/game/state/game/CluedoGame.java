package nl.imine.minigame.cluedo.game.state.game;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.Log;
import nl.imine.minigame.timer.Timer;
import nl.imine.minigame.timer.TimerHandler;
import org.bukkit.entity.Player;

public class CluedoGame implements CluedoState, TimerHandler{

    public static final CluedoStateType cluedoStateType = CluedoStateType.IN_GAME;

    private CluedoMinigame cluedoMinigame;
    private int gameTimer = CluedoPlugin.getSettings().getInt(Setting.IN_GAME_TIME);
    private Timer timer;

    public CluedoGame(CluedoMinigame cluedoMinigame){
        this.cluedoMinigame = cluedoMinigame;
    }

    @Override
    public void handleStateChange() {
        Log.info("Handling state change for: " + this.getClass().getSimpleName());
        this.timer = CluedoPlugin.getTimerManager().createTimer(CluedoPlugin.getInstance().getName(), gameTimer, this);
        cluedoMinigame.getPlayers().forEach(timer::showTimer);
    }

    @Override
    public void onTimerEnd() {
        Log.info("Handling timer end for: " + this.getClass().getSimpleName());
        cluedoMinigame.getPlayers().forEach(timer::hideTimer);
        cluedoMinigame.changeGameState(CluedoStateType.END_GAME);
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

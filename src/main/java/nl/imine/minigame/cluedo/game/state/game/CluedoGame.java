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

import java.util.List;
import java.util.Random;

public class CluedoGame implements CluedoState, TimerHandler{

    public static final CluedoStateType cluedoStateType = CluedoStateType.IN_GAME;

    private CluedoMinigame cluedoMinigame;
    private List<CluedoSpawn> spawns = CluedoPlugin.getSpawnLocationService().getSpawns();
    private int gameTimer = CluedoPlugin.getSettings().getInt(Setting.IN_GAME_TIME);
    private Timer timer;
    boolean started = false;

    public CluedoGame(CluedoMinigame cluedoMinigame){
        this.cluedoMinigame = cluedoMinigame;
    }

    @Override
    public void handleStateChange() {
        Log.finer("Handling state change for: " + this.getClass().getSimpleName());
        this.timer = CluedoPlugin.getTimerManager().createTimer(CluedoPlugin.getInstance().getName(), gameTimer, this);
        cluedoMinigame.getPlayers().forEach(this::handlePlayer);
        started = true;
    }

    @Override
    public void onTimerEnd() {
        Log.finest("Handling timer end for: " + this.getClass().getSimpleName());
        cluedoMinigame.getPlayers().forEach(timer::hideTimer);
        cluedoMinigame.changeGameState(CluedoStateType.END_GAME);
    }

    @Override
    public CluedoStateType getState() {
        return cluedoStateType;
    }

    @Override
    public void handlePlayer(Player player) {
        if(!started) {
            timer.showTimer(player);
            spawns = CluedoPlugin.getSpawnLocationService().getSpawns();
            player.teleport(spawns.get(new Random().nextInt(spawns.size())).getLocation());
        } else {
            player.teleport(spawns.get(new Random().nextInt(spawns.size())).getLocation());
        }
    }
}

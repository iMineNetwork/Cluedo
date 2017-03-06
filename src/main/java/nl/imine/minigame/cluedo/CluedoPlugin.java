package nl.imine.minigame.cluedo;

import nl.imine.minigame.MinigameManager;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.timer.TimerManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CluedoPlugin extends JavaPlugin {

    private static Plugin plugin;
    private static TimerManager timerManager;

    @Override
    public void onEnable() {
        CluedoPlugin.plugin = this;
        CluedoPlugin.timerManager = new TimerManager();
        CluedoPlugin.timerManager.init(this);
        CluedoMinigame game = new CluedoMinigame();
        game.changeGameState(CluedoStateType.LOBBY);
        MinigameManager.registerMinigame(game);
    }

    @Override
    public void onDisable() {
        CluedoPlugin.plugin = null;
    }

    public static Plugin getInstance(){
        return plugin;
    }

    public static TimerManager getTimerManager() { return timerManager; }
}

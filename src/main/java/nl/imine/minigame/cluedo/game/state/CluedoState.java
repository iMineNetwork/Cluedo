package nl.imine.minigame.cluedo.game.state;

import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class CluedoState {

    protected CluedoStateType cluedoStateType;

    public CluedoState(CluedoStateType cluedoStateType) {
        this.cluedoStateType = cluedoStateType;
    }

    public abstract void handleStateChange();
    public abstract void handleStateEnd();
    public abstract CluedoStateType getState();
    public abstract void handlePlayer(CluedoPlayer cluedoPlayer);
    public abstract void handlePlayerDeath(CluedoPlayer cluedoPlayer);
    public abstract void handlePlayerLeave(CluedoPlayer cluedoPlayer);
    public abstract Location getRespawnLocation();
}

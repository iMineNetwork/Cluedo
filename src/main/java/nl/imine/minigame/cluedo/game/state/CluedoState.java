package nl.imine.minigame.cluedo.game.state;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class CluedoState {

    protected CluedoStateType cluedoStateType;

    public CluedoState(CluedoStateType cluedoStateType) {
        this.cluedoStateType = cluedoStateType;
    }

    public abstract void handleStateChange();
    public abstract CluedoStateType getState();
    public abstract void handlePlayer(Player player);
    public abstract void handlePlayerDeath(Player player);
    public abstract Location getRespawnLocation();
}

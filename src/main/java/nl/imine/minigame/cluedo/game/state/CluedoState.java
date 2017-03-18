package nl.imine.minigame.cluedo.game.state;

import org.bukkit.entity.Player;

public interface CluedoState {

    void handleStateChange();
    CluedoStateType getState();
    void handlePlayer(Player player);
    void handlePlayerDeath(Player player);
}

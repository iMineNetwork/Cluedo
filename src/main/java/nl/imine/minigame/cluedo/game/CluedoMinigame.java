package nl.imine.minigame.cluedo.game;

import nl.imine.minigame.Minigame;
import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.game.state.endgame.CluedoEndGame;
import nl.imine.minigame.cluedo.game.state.game.CluedoGame;
import nl.imine.minigame.cluedo.game.state.lobby.CluedoLobby;
import nl.imine.minigame.cluedo.game.state.pregame.CluedoPreGame;
import nl.imine.minigame.cluedo.util.Log;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CluedoMinigame extends Minigame{

    public static final int SETTINGS_MAX_PLAYERCOUNT = 10;
    public static final String SETTINGS_MINIGAME_NAME = "Cluedo";

    private ArrayList<Player> players = new ArrayList<>();
    private World cluedoWorld;

    private CluedoState gameState;

    public String getName() {
        return SETTINGS_MINIGAME_NAME;
    }

    public boolean isJoinable() {
        return (getPlayerCount() < getMaxPlayers());
    }

    public int getMaxPlayers() {
        return SETTINGS_MAX_PLAYERCOUNT;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public String getMOTD() {
        return this.getName();
    }

    public void onJoin(Player player) {
        players.add(player);
        getGameState().handlePlayer(player);
    }

    public void onLeave(Player player) {
        players.remove(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public World getCluedoWorld() {
        return cluedoWorld;
    }

    public CluedoState getGameState() {
        return gameState;
    }

    public void changeGameState(CluedoStateType type){
        Log.info("Changing game state to: " + type.name());
        switch (type){
            case PRE_GAME:
                gameState = new CluedoPreGame(this);
                gameState.handleStateChange();
                break;
            case IN_GAME:
                gameState = new CluedoGame(this);
                gameState.handleStateChange();
                break;
            case END_GAME:
                gameState = new CluedoEndGame(this);
                gameState.handleStateChange();
                break;
            case LOBBY:
                gameState = new CluedoLobby(this);
                gameState.handleStateChange();
                break;
        }
    }
}

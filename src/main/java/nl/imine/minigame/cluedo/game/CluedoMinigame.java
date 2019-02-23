package nl.imine.minigame.cluedo.game;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.player.role.RoleInteractPermission;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.game.state.endgame.CluedoEndGame;
import nl.imine.minigame.cluedo.game.state.game.CluedoGame;
import nl.imine.minigame.cluedo.game.state.lobby.CluedoLobby;
import nl.imine.minigame.cluedo.game.state.pregame.CluedoPreGame;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CluedoMinigame {

    private Logger logger = JavaPlugin.getPlugin(CluedoPlugin.class).getLogger();
    private List<CluedoPlayer> players = new ArrayList<>();
    private CluedoState gameState;

    private final int maxPlayers = CluedoPlugin.getSettings().getInt(Setting.GAME_MAX_PLAYERS);
    private final String worldName = CluedoPlugin.getSettings().getString(Setting.GAME_WORLD_NAME);
    private final List<RoleInteractPermission> roleInteractionPermissions = CluedoPlugin.getSettings().getRoleInteractPermissions();

    public boolean isJoinable() {
        return (getPlayerCount() < getMaxPlayers());
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public void joinPlayer(Player player) {
        if (isJoinable() && !isPlayerInGame(player)) {
            onJoin(player);
        }
    }

    private void onJoin(Player player) {
        RoleType role = gameState.getState().equals(CluedoStateType.LOBBY) ? RoleType.LOBBY : RoleType.SPECTATOR;
        CluedoPlayer cluedoPlayer = new CluedoPlayer(player, role);
        players.add(cluedoPlayer);
        getGameState().handlePlayer(cluedoPlayer);
        PlayerUtil.cleanPlayer(player, true);
    }

    public void leavePlayer(Player player) {
        CluedoPlayer cluedoPlayer = getCluedoPlayer(player);
        if(cluedoPlayer != null) {
            onLeave(cluedoPlayer);
        }
    }

    public void onLeave(CluedoPlayer cluedoPlayer) {
        players.remove(cluedoPlayer);
        gameState.handlePlayerLeave(cluedoPlayer);
        cluedoPlayer.getPlayer().getInventory().clear();
        cluedoPlayer.getPreviousState().restoreState(cluedoPlayer.getPlayer());
    }

    private boolean isPlayerInGame(Player player) {
        return players.stream().map(CluedoPlayer::getPlayer).anyMatch(player::equals);
    }

    public List<Player> getPlayers() {
        return players.stream().map(CluedoPlayer::getPlayer).collect(Collectors.toList());
    }

    public List<CluedoPlayer> getCluedoPlayers() {
        return players;
    }

    public World getCluedoWorld() {
        return Bukkit.getWorld(worldName);
    }

    public List<RoleInteractPermission> getRoleInteractionPermissions() {
        return roleInteractionPermissions;
    }

    public CluedoState getGameState() {
        return gameState;
    }

    public void changeGameState(CluedoStateType type) {
        gameState.handleStateEnd();
        logger.fine("Changing game state to: " + type.name());
        switch (type) {
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

    public CluedoPlayer getCluedoPlayer(Player player) {
        return getCluedoPlayers().stream()
                .filter(cPlayer -> cPlayer.getPlayer().equals(player))
                .findFirst().orElse(null);
    }

    public void shutdown() {
        new ArrayList<>(players).forEach(this::onLeave);
    }
}

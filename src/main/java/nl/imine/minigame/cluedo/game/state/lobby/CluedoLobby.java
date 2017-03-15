package nl.imine.minigame.cluedo.game.state.lobby;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.Log;
import nl.imine.minigame.timer.Timer;
import nl.imine.minigame.timer.TimerHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CluedoLobby implements CluedoState, TimerHandler{

    public static final CluedoStateType cluedoStateType = CluedoStateType.LOBBY;

    private CluedoMinigame cluedoMinigame;
    private int gameTimer = CluedoPlugin.getSettings().getInt(Setting.LOBBY_TIME);
    private Location spawnLocation = CluedoPlugin.getSettings().getLocation(Setting.LOBBY_SPAWN);
    private Timer timer;

    public CluedoLobby(CluedoMinigame cluedoMinigame){
        this.cluedoMinigame = cluedoMinigame;
    }

    @Override
    public void handleStateChange() {
        Log.finer("Handling state change for: " + this.getClass().getSimpleName());
        this.timer = CluedoPlugin.getTimerManager().createTimer("Lobby", gameTimer, this);
        cluedoMinigame.getPlayers().forEach(this::handlePlayer);
    }

    @Override
    public void onTimerEnd() {
        Log.finest("Handling timer end for: " + this.getClass().getSimpleName());
		cluedoMinigame.getPlayers().forEach(timer::hideTimer);

		//A game should always contain at least 3 players
		if(cluedoMinigame.getCluedoPlayers().size() > 2) {
            cluedoMinigame.changeGameState(CluedoStateType.PRE_GAME);

        /* As we don't accept players anymore when the game has already started. We assign roles
            at the end of the lobby rather then when a player joins preparation. */
            Random random = new Random();

            List<CluedoPlayer> assignablePlayers = cluedoMinigame.getCluedoPlayers().stream()
                    .filter(player -> player.getRole().equals(RoleType.LOBBY))
                    .collect(Collectors.toList());

            //Select a random player and make him the murderer, then remove him from the assignable list
            int assignIndex = random.nextInt(assignablePlayers.size());
            assignablePlayers.get(assignIndex).setRole(RoleType.MURDERER);
            assignablePlayers.remove(assignIndex);

            //Select a random player and make him the detective, then remove him from the assignable list
            assignIndex = random.nextInt(assignablePlayers.size());
            assignablePlayers.get(assignIndex).setRole(RoleType.MURDERER);
            assignablePlayers.remove(assignIndex);

            //Assign all the remaining players to Bystander
            assignablePlayers.forEach(assignablePlayer -> assignablePlayer.setRole(RoleType.BYSTANDER));
        } else {
            //Restart lobby
            cluedoMinigame.changeGameState(CluedoStateType.LOBBY);
        }
    }

    @Override
    public CluedoStateType getState() {
        return cluedoStateType;
    }

    @Override
    public void handlePlayer(Player player) {
        timer.showTimer(player);
        cluedoMinigame.getCluedoPlayers().stream()
                .filter(cluedoPlayer -> cluedoPlayer.getPlayer().equals(player))
                .findAny()
                .ifPresent(cluedoPlayer -> cluedoPlayer.setRole(RoleType.LOBBY));
        player.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
}

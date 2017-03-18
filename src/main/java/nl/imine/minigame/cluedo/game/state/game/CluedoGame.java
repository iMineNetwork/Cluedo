package nl.imine.minigame.cluedo.game.state.game;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.Log;
import nl.imine.minigame.cluedo.util.PlayerUtil;
import nl.imine.minigame.timer.Timer;
import nl.imine.minigame.timer.TimerHandler;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class CluedoGame implements CluedoState, TimerHandler{

    public static final CluedoStateType cluedoStateType = CluedoStateType.IN_GAME;
    private Location respawnLocation = CluedoPlugin.getSettings().getLocation(Setting.LOBBY_SPAWN);


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
        endGame(GameResult.STALEMATE);
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
            player.teleport(respawnLocation);
        }
    }

    @Override
    public void handlePlayerDeath(Player player) {
        //Clear the player of his items and put him back in the lobby.
        PlayerUtil.cleanPlayer(player);
        player.teleport(respawnLocation);


        //Find the player's game object.
        CluedoPlayer cluedoPlayer = cluedoMinigame.getCluedoPlayers().stream()
                .filter(registeredPlayer -> registeredPlayer.getPlayer().equals(player))
                .findAny()
                .orElse(null);

        //Make the player a spectator
        cluedoPlayer.setRole(RoleType.SPECTATOR);

        //Check if the game has to end
        GameResult result = checkGameEnd();
        if(result != null){
            endGame(result);
        }

    }

    private GameResult checkGameEnd(){
        boolean murdererAlive = false;
        boolean innocentsAlive = false;

        for(CluedoPlayer cluedoPlayer : cluedoMinigame.getCluedoPlayers()) {
            //Check if there is still a murderer
            if (!murdererAlive) {
                if (cluedoPlayer.getRole().getRoleType().equals(RoleType.MURDERER)) {
                    murdererAlive = true;
                }
            }

            //Check if there are any bystanders left
            if (!innocentsAlive) {
                if (cluedoPlayer.getRole().isInnocent()){
                    innocentsAlive = true;
                }
            }
        }

        if(!murdererAlive){
            return GameResult.BYSTANDER_WIN;
        }

        if(!innocentsAlive){
            return GameResult.MURDERER_WIN;
        }

        return null;
    }

    private void endGame(GameResult result){
        cluedoMinigame.getPlayers().forEach(timer::hideTimer);
        cluedoMinigame.changeGameState(CluedoStateType.END_GAME);
    }
}

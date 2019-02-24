package nl.imine.minigame.cluedo.game.state.game;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.game.state.game.jobs.JobManager;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.PlayerUtil;
import nl.imine.minigame.timer.Timer;
import nl.imine.minigame.timer.TimerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

public class CluedoGame extends CluedoState implements TimerHandler {

    private Logger logger = JavaPlugin.getPlugin(CluedoPlugin.class).getLogger();

    //Dependencies
    private CluedoMinigame cluedoMinigame;

    //Settings
    private Location respawnLocation = CluedoPlugin.getSettings().getLocation(Setting.LOBBY_SPAWN);
    private List<CluedoSpawn> spawns = CluedoPlugin.getSpawnLocationService().getSpawns();
    private int gameTimer = CluedoPlugin.getSettings().getInt(Setting.IN_GAME_TIME);

    //Game variables
    //Scoreboard
    private Scoreboard gameScoreboard;
    private Team invisibleNameTagTeam;
    private Timer timer;
    private BukkitTask footprintHandler;
    private boolean started = false;

    private PlayerTracker playerTracker;

    public CluedoGame(CluedoMinigame cluedoMinigame) {
        super(CluedoStateType.IN_GAME);
        this.cluedoMinigame = cluedoMinigame;
    }

    @Override
    public void handleStateChange() {
        logger.finer("Handling state change for: " + this.getClass().getSimpleName());

        this.timer = CluedoPlugin.getTimerManager().createTimer(CluedoPlugin.getSettings().getString(Setting.GAME_NAME), gameTimer, this);

        // Initialize a clean scoreboard
        gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        //Add a team to remove the players' nametags
        invisibleNameTagTeam = gameScoreboard.registerNewTeam("InvisibleNametag");
        invisibleNameTagTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        invisibleNameTagTeam.setCanSeeFriendlyInvisibles(false);

        footprintHandler = Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(CluedoPlugin.class), new FootprintHandler(), 0, 5);
        cluedoMinigame.getCluedoPlayers().forEach(this::handlePlayer);

        JobManager.getInstance().startJobSystem();

        started = true;
    }

    @Override
    public void handleStateEnd() {
        logger.finer("Handling state end for: " + this.getClass().getSimpleName());
        this.timer.setStopped(true);
        cluedoMinigame.getPlayers().forEach(this.timer::hideTimer);
    }

    @Override
    public void onTimerEnd() {
        logger.finest("Handling timer end for: " + this.getClass().getSimpleName());
        endGame(GameResult.STALEMATE);
    }

    @Override
    public CluedoStateType getState() {
        return cluedoStateType;
    }

    @Override
    public void handlePlayer(CluedoPlayer cluedoPlayer) {
        PlayerUtil.cleanPlayer(cluedoPlayer.getPlayer(), false);
        if (!started) {
            timer.showTimer(cluedoPlayer.getPlayer());

            //Handle scoreboard
            cluedoPlayer.getPlayer().setScoreboard(gameScoreboard);
            invisibleNameTagTeam.addEntry(cluedoPlayer.getPlayer().getName());

            //Teleport player to random spawn
            spawns = CluedoPlugin.getSpawnLocationService().getSpawns();
            cluedoPlayer.getPlayer().teleport(spawns.get(new Random().nextInt(spawns.size())).getLocation());
        } else {
            cluedoPlayer.getPlayer().teleport(respawnLocation);
        }
    }

    @Override
    public void handlePlayerDeath(CluedoPlayer cluedoPlayer) {
        //Clear the player of his items and put him back in the lobby.
        PlayerUtil.cleanPlayer(cluedoPlayer.getPlayer(), true);

        //Make the player a spectator
        cluedoPlayer.setRole(RoleType.SPECTATOR);

        //Check if the game has to end
        GameResult result = checkGameEnd();
        if (result != null) {
            endGame(result);
        }

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(CluedoPlugin.class), ()
                -> cluedoPlayer.getPlayer().teleport(respawnLocation), 1L);
    }

    @Override
    public void handlePlayerLeave(CluedoPlayer cluedoPlayer) {
        timer.hideTimer(cluedoPlayer.getPlayer());

        //Handle item drops
        if (cluedoPlayer.getRole().getRoleType().equals(RoleType.DETECTIVE)) {
            Item item = cluedoPlayer.getPlayer().getLocation().getWorld()
                    .dropItem(cluedoPlayer.getPlayer().getLocation(), new ItemStack(Material.BOW));
            item.setInvulnerable(true);
        }

        //Clear the player of their items.
        PlayerUtil.cleanPlayer(cluedoPlayer.getPlayer(), true);

        //Check if the game has to end
        GameResult result = checkGameEnd();
        if (result != null) {
            endGame(result);
        }
    }

    @Override
    public Location getRespawnLocation() {
        return respawnLocation;
    }

    private GameResult checkGameEnd() {
        boolean murdererAlive = false;
        int innocentsAlive = 0;

        for (CluedoPlayer cluedoPlayer : cluedoMinigame.getCluedoPlayers()) {
            //Check if there is still a murderer
            if (!murdererAlive) {
                if (cluedoPlayer.getRole().getRoleType().equals(RoleType.MURDERER)) {
                    murdererAlive = true;
                }
            }

            //Check if there are any bystanders left
            if (cluedoPlayer.getRole().isInnocent()) {
                innocentsAlive++;
            }
        }

        if (!murdererAlive) {
            return GameResult.BYSTANDER_WIN;
        }

        if (innocentsAlive <= 0) {
            return GameResult.MURDERER_WIN;
        } else if (innocentsAlive == 1) {
            Optional<PlayerTracker> optionalPlayerTracker = cluedoMinigame.getCluedoPlayers().stream()
                    .filter(cluedoPlayer -> cluedoPlayer.getRole().isInnocent())
                    .findAny()
                    .map(cluedoPlayer -> new PlayerTracker(cluedoPlayer, gameScoreboard));
            if (optionalPlayerTracker.isPresent()) {
                playerTracker = optionalPlayerTracker.get();
                playerTracker.startTracker();
            }
//            cluedoMinigame.getCluedoPlayers().stream()
//                    .filter(cluedoPlayer -> cluedoPlayer.getRole().isInnocent())
//                    .forEach(cluedoPlayer -> {
//                        cluedoPlayer.getPlayer().setGlowing(true);
//                    });
        }

        return null;
    }

    private void endGame(GameResult result) {

        //Stop player jobs
        JobManager.getInstance().resetJobs();

        //Setup end game notifications
        String titleText = null;
        String subtitleText = null;
        switch (result) {
            case BYSTANDER_WIN:
                titleText = ChatColor.BLUE + "Bystanders Win";
                break;
            case MURDERER_WIN:
                titleText = ChatColor.RED + "Murderer Wins";
                break;
            //Will run when the timer has finished or the game stops for any other reason
            case STALEMATE:
            default:
                titleText = ChatColor.DARK_PURPLE + "Time limit reached";
                break;
        }

        for (CluedoPlayer player : cluedoMinigame.getCluedoPlayers()) {
            player.addXpToReward(player.getRole().getBaseXp());
            switch (result) {
                case BYSTANDER_WIN:
                    player.addXpToReward(10);
                    if (player.getRole().getRoleType() == RoleType.BYSTANDER || player.getRole().getRoleType() == RoleType.DETECTIVE || player.getRole().getRoleType() == RoleType.SPECTATOR) {
                        //non murderer players recieve +25 xp instead of +10
                        player.addXpToReward(15);
                    }
                    break;
//                case MURDERER_WIN:
//                    if (player.getRole().getRoleType() == RoleType.MURDERER) {
//                        player.addXpToReward(25);
//                    }
//                    break;
            }

            player.rewardXp();
            //Show the set-up title to all players in the minigame instance
            player.getPlayer().sendTitle(titleText, subtitleText, 10, 100, 10);

            //Hide Timer
            timer.hideTimer(player.getPlayer());

            //Remove player nametag invisibility
            player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            invisibleNameTagTeam.removeEntry(player.getPlayer().getName());

            //Remove footprints
            player.clearFootprints();
        }

        //Hide timer and prevent it from ticking
        CluedoPlugin.getTimerManager().removeTimer(timer);

        //Clean map
        if(playerTracker != null) {
            playerTracker.stopTracker();
        }

        //Remove footprint runner
        footprintHandler.cancel();
        footprintHandler = null;

        //Unregister Team
        invisibleNameTagTeam.unregister();

        //Change state
        cluedoMinigame.changeGameState(CluedoStateType.END_GAME);
    }

}

package nl.imine.minigame.cluedo.game.state.game;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerTracker {

    private static final String NO_COLLISION_TEAM_NAME = "NO_COLLISSION";

    private final CluedoPlayer cluedoPlayer;
    private final Scoreboard gameScoreboard;
    private MagmaCube trackingIconEntity;
    private BukkitTask task;

    public PlayerTracker(CluedoPlayer cluedoPlayer, Scoreboard gameScoreboard) {
        this.cluedoPlayer = cluedoPlayer;
        this.gameScoreboard = gameScoreboard;
    }

    public void startTracker(){
        this.trackingIconEntity = (MagmaCube) cluedoPlayer.getPlayer().getWorld().spawnEntity(cluedoPlayer.getPlayer().getLocation(), EntityType.MAGMA_CUBE);
        setupMarkerEntity();
        this.task = Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(CluedoPlugin.class), this::teleportTracker, 0, 5*20);
    }

    public void stopTracker(){
        if (trackingIconEntity == null) {
            return;
        }
        task.cancel();
        trackingIconEntity.remove();
    }

    private void setupMarkerEntity() {
        addToScoreBoard();
        trackingIconEntity.setGravity(false);
        trackingIconEntity.setInvulnerable(true);
        trackingIconEntity.setGlowing(true);
        trackingIconEntity.setSize(1);
        trackingIconEntity.setAI(false);
        trackingIconEntity.setSilent(true);
        trackingIconEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, true, false));
    }

    private void addToScoreBoard(){
        Team team = gameScoreboard.getTeam(NO_COLLISION_TEAM_NAME);
        if (team == null) {
            team = gameScoreboard.registerNewTeam(NO_COLLISION_TEAM_NAME);
        }
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.addEntry(trackingIconEntity.getUniqueId().toString());
    }

    private void teleportTracker() {
        if(!cluedoPlayer.getPlayer().getEyeLocation().getWorld().equals(CluedoPlugin.getGame().getCluedoWorld())) {
            stopTracker();
            return;
        }
        trackingIconEntity.teleport(cluedoPlayer.getPlayer().getEyeLocation().subtract(0, 1, 0));
    }
}

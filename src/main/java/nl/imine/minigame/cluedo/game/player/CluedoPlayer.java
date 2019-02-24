package nl.imine.minigame.cluedo.game.player;

import nl.imine.minigame.cluedo.game.player.role.CluedoRole;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import nl.imine.minigame.cluedo.game.state.game.jobs.Job;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public class CluedoPlayer {

    private final Player player;
    private final PreviousPlayerState previousState;


    //Gameplay Details
    private CluedoRole role;
    private Color footprintColor = Color.BLACK;
    private LinkedList<Location> footprints = new LinkedList<>();

    private Job activeJob;
    private int completedJobs;

    private int xpReward;

    public CluedoPlayer(Player player, RoleType role) {
        this.player = player;
        this.role = RoleType.getCluedoRole(role);
        this.previousState = PreviousPlayerState.loadFromPlayer(player);
    }

    public Player getPlayer() {
        return player;
    }

    public PreviousPlayerState getPreviousState() {
        return previousState;
    }

    /**
     * Sets the role and updates the player's inventory to the role specific
     * settings.
     *
     * @param roleType, The role to update to
     */
    public void setRole(RoleType roleType) {
        this.role = RoleType.getCluedoRole(roleType);
        role.preparePlayer(player);
    }

    public CluedoRole getRole() {
        return role;
    }

    public Color getFootprintColor() {
        return footprintColor;
    }

    public void setFootprintColor(Color footprintColor) {
        this.footprintColor = footprintColor;
    }

    /**
     * Get this player's footprints
     *
     * @return a list of the last 20 locations of this player
     */
    public LinkedList<Location> getFootprints() {
        return footprints;
    }

    /**
     * Reset this player's footprint list
     */
    public void clearFootprints() {
        footprints.clear();
    }

    /**
     * @param footprint
     */
    public void addFootprint(Location footprint) {
        if (footprints.size() > 20) {
            footprints.removeFirst();
        }
        footprints.add(footprint);
    }

    public void setRole(CluedoRole role) {
        this.role = role;
    }

    public void setFootprints(LinkedList<Location> footprints) {
        this.footprints = footprints;
    }

    public Job getActiveJob() {
        return activeJob;
    }

    public void setActiveJob(Job activeJob) {
        if (this.getActiveJob() != null) {
            this.getActiveJob().getJobItem().remove();
        }
        this.activeJob = activeJob;
    }

    public int getCompletedJobs() {
        return completedJobs;
    }

    public void setCompletedJobs(int completedJobs) {
        this.completedJobs = completedJobs;
    }

    public void rewardXp() {

        if (xpReward <= 0) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You received no experience!");
            return;
        }

        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You received " + xpReward + " experience!");
        player.giveExp(xpReward);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10L, 0L);

        xpReward = 0;
    }

    public void addXpToReward(int xp) {
        xpReward += xp;
    }

    public void removeXpFromReward(int xp) {
        xpReward -= xp;
    }

    public boolean isParticipatingInGame() {
        return role.getRoleType() == RoleType.BYSTANDER || role.getRoleType() == RoleType.DETECTIVE || role.getRoleType() == RoleType.MURDERER;
    }
}

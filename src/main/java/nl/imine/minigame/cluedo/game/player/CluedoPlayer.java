package nl.imine.minigame.cluedo.game.player;

import java.awt.Color;
import java.util.LinkedList;

import nl.imine.minigame.cluedo.game.state.game.jobs.Job;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import nl.imine.minigame.cluedo.game.player.role.CluedoRole;
import nl.imine.minigame.cluedo.game.player.role.RoleType;

public class CluedoPlayer {

	private final Player player;
	private CluedoRole role;

	//Gameplay Details
	private Color footprintColor = Color.black;
	private LinkedList<Location> footprints = new LinkedList<>();

	private Job activeJob;
	private int completedJobs;

	public CluedoPlayer(Player player, RoleType role) {
		this.player = player;
		this.role = RoleType.getCluedoRole(role);
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * Sets the role and updates the player's inventory to the role specific settings.
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
	 * @return a list of the last 20 locations of this player
	 */
	public LinkedList<Location> getFootprints() {
		return footprints;
	}

	/**
	 * Reset this player's footprint list
	 */
	public void clearFootprints(){
		footprints.clear();
	}

	/**
	 *
	 * @param footprint
	 */
	public void addFootprint(Location footprint) {
		if(footprints.size() > 20){
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
		this.activeJob = activeJob;
		this.getActiveJob().getJobItem().remove();
	}

	public int getCompletedJobs() {
		return completedJobs;
	}

	public void setCompletedJobs(int completedJobs) {
		this.completedJobs = completedJobs;
	}
}

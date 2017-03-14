package nl.imine.minigame.cluedo.game.player;

import org.bukkit.entity.Player;

import nl.imine.minigame.cluedo.game.player.role.CluedoRole;
import nl.imine.minigame.cluedo.game.player.role.RoleType;

public class CluedoPlayer {

	private final Player player;
	private CluedoRole role;

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
}

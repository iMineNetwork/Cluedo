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
	 * @param role, The role to update to
	 */
	public void setRole(CluedoRole role) {
		role.preparePlayer(player);
		this.role = role;
	}

	public CluedoRole getRole() {
		return role;
	}
}

package nl.imine.minigame.cluedo.game.player.role;

import java.util.List;

import org.bukkit.Material;

public class RoleInteractPermission {

	private final Material type;
	private final List<RoleType> roles;

	public RoleInteractPermission(Material type, List<RoleType> roles) {
		this.type = type;
		this.roles = roles;
	}

	public Material getType() {
		return type;
	}

	public List<RoleType> getRoles() {
		return roles;
	}

	/**
	 * Checks if a certain role can interact with this material.
	 * @param type the Roletype to check the permission for.
	 * @return true if the role should be able to interact with this Material, false if it should not
	 */
	public boolean canInteract(RoleType type){
		return roles.contains(type);
	}

}

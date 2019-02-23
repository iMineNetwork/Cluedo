package nl.imine.minigame.cluedo.game.player.role;

import org.bukkit.entity.Player;

public abstract class CluedoRole {

    private RoleType roleType;
    private int BaseXp;

    public CluedoRole(RoleType roleType) {
        this.roleType = roleType;
        this.BaseXp = 0;
    }

    public CluedoRole(RoleType roleType, int baseXp) {
        this.roleType = roleType;
        this.BaseXp = baseXp;
    }

    /**
     * Set's up the player for his role. This contains setting the gamemode and inventory
     *
     * @param player the player to prepare
     */
    public abstract void preparePlayer(Player player);

    /**
     * Checks if the player has a role that considers him innocent.
     * Currently the only non-innocent role is Murderer
     *
     * @return if the player is innocent
     */
    public boolean isInnocent() {
        return roleType.isInnocent();
    }

    /**
     * Get this role's type
     *
     * @return the RoleType of the role.
     */
    public RoleType getRoleType() {
        return roleType;
    }

    public int getBaseXp() {
        return BaseXp;
    }
}

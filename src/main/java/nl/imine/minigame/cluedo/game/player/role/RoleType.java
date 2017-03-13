package nl.imine.minigame.cluedo.game.player.role;

import nl.imine.minigame.cluedo.game.player.role.roles.*;

public enum RoleType {
	LOBBY(true), SPECTATOR(true), BYSTANDER(true), DETECTIVE(true), MURDERER(false);

	private boolean innocent;

	RoleType(boolean innocent) {
		this.innocent = innocent;
	}

	public boolean isInnocent() {
		return this.innocent;
	}

	public static CluedoRole getCluedoRole(RoleType role) {
		switch (role) {
			case BYSTANDER:
				return new BystanderRole();
			case DETECTIVE:
				return new DetectiveRole();
			case MURDERER:
				return new MurderRole();
			case SPECTATOR:
				return new SpectatorRole();
			default:
			case LOBBY:
				return new LobbyRole();
		}
	}
}

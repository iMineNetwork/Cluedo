package nl.imine.minigame.cluedo.model;

import java.util.UUID;

import nl.imine.minigame.cluedo.game.player.role.RoleType;

public class PlayerEntry {

	private UUID gameId;
	private UUID playerId;
	private RoleType role;

	public PlayerEntry(UUID gameId, UUID playerId, RoleType role) {
		this.gameId = gameId;
		this.playerId = playerId;
		this.role = role;
	}

	public UUID getGameId() {
		return gameId;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public void setPlayerId(UUID playerId) {
		this.playerId = playerId;
	}

	public RoleType getRole() {
		return role;
	}

	public void setRole(RoleType role) {
		this.role = role;
	}
}

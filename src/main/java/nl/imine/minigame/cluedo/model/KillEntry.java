package nl.imine.minigame.cluedo.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.bukkit.Material;

public class KillEntry {

	private UUID gameId;
	private UUID killerEntry;
	private UUID victimEntry;
	private Material weapon;
	private LocalDateTime timestamp;

	public KillEntry() {
	}

	public KillEntry(UUID gameId, UUID killerEntry, UUID victimEntry, Material weapon, LocalDateTime timestamp) {
		this.gameId = gameId;
		this.killerEntry = killerEntry;
		this.victimEntry = victimEntry;
		this.weapon = weapon;
		this.timestamp = timestamp;
	}

	public UUID getGameId() {
		return gameId;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

	public UUID getKillerEntry() {
		return killerEntry;
	}

	public void setKillerEntry(UUID killerEntry) {
		this.killerEntry = killerEntry;
	}

	public UUID getVictimEntry() {
		return victimEntry;
	}

	public void setVictimEntry(UUID victimEntry) {
		this.victimEntry = victimEntry;
	}

	public Material getWeapon() {
		return weapon;
	}

	public void setWeapon(Material weapon) {
		this.weapon = weapon;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}

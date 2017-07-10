package nl.imine.minigame.cluedo.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class GameEntry {

	private UUID gameId;
	private LocalDateTime startTime;
	private LocalDateTime endTime;

	public GameEntry(UUID gameId, LocalDateTime startTime, LocalDateTime endTime) {
		this.gameId = gameId;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public UUID getGameId() {
		return gameId;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
}

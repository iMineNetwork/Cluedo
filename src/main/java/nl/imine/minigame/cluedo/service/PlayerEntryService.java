package nl.imine.minigame.cluedo.service;

import java.util.List;
import java.util.UUID;

import nl.imine.minigame.cluedo.model.PlayerEntry;

public interface PlayerEntryService {

	void save(PlayerEntry playerEntry);

	void update(PlayerEntry playerEntry);

	List<PlayerEntry> getByPlayerId(UUID playerId);

	List<PlayerEntry> getFromGame(UUID gameId);

	PlayerEntry getByPlayerIdInGame(UUID playerId, UUID gameId);
}

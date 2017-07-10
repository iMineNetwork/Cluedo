package nl.imine.minigame.cluedo.service;

import java.util.List;
import java.util.UUID;

import nl.imine.minigame.cluedo.model.GameEntry;

public interface GameEntryService {

	void save(GameEntry gameEntry);

	void update(GameEntry gameEntry);

	GameEntry getById(UUID id);

	List<GameEntry> getAll();
}

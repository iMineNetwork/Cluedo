package nl.imine.minigame.cluedo.service;

import java.util.List;
import java.util.UUID;

import nl.imine.minigame.cluedo.model.KillEntry;

public interface KillEntryService {

	void save(KillEntry killEntry);

	void update(KillEntry killEntry);

	KillEntry getByKillerVictimInGame(UUID killer, UUID victim, UUID game);

	List<KillEntry> getByGame(UUID gameId);

	List<KillEntry> getByAsKiller(UUID killerId);

	List<KillEntry> getByAsVictim(UUID victimId);
}

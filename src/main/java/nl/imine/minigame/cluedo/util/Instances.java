package nl.imine.minigame.cluedo.util;

import nl.imine.minigame.cluedo.service.GameEntryService;
import nl.imine.minigame.cluedo.service.KillEntryService;
import nl.imine.minigame.cluedo.service.PlayerEntryService;
import nl.imine.minigame.cluedo.service.impl.GameEntryServiceImpl;
import nl.imine.minigame.cluedo.service.impl.KillEntryServiceImpl;
import nl.imine.minigame.cluedo.service.impl.PlayerEntryServiceImpl;
import nl.imine.minigame.cluedo.util.mysql.MySQLService;

public class Instances {

	private static GameEntryService gameEntryService;
	private static KillEntryService killEntryService;
	private static PlayerEntryService playerEntryService;

	public static void initInstances(MySQLService mySQLService) {
		gameEntryService = new GameEntryServiceImpl(mySQLService);
		killEntryService = new KillEntryServiceImpl(mySQLService);
		playerEntryService = new PlayerEntryServiceImpl(mySQLService);
	}

	public static GameEntryService getGameEntryService() {
		return gameEntryService;
	}

	public static KillEntryService getKillEntryService() {
		return killEntryService;
	}

	public static PlayerEntryService getPlayerEntryService() {
		return playerEntryService;
	}

}

package nl.imine.minigame.cluedo.util.mysql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TableCreator {

	private MySQLService mySQLService;

	public TableCreator(MySQLService mySQLService) {
		this.mySQLService = mySQLService;
	}

	public void createTables() {
		try {
			PreparedStatement createGameEntryTable = mySQLService.getConnection().prepareStatement("CREATE TABLE GameEntry (gameId VARCHAR(36), startTime TIMESTAMP, endTime TIMESTAMP,\n" +
					"PRIMARY KEY (gameId)\n" +
					");"
			);
			createGameEntryTable.execute();

			PreparedStatement createPlayerEntryTable = mySQLService.getConnection().prepareStatement("CREATE TABLE PlayerEntry (gameId VARCHAR(36),playerId VARCHAR(36),roleType VARCHAR(255),\n" +
					"CONSTRAINT PK_Player PRIMARY KEY (playerId, gameId),\n" +
					"FOREIGN KEY (gameId) REFERENCES GameEntry(gameId)\n" +
					");"
			);
			createPlayerEntryTable.execute();

			PreparedStatement createKillEntryTable = mySQLService.getConnection().prepareStatement("CREATE TABLE KillEntry (gameId VARCHAR(36),killerId VARCHAR(36),victimId VARCHAR(36),material VARCHAR(255),timestamp TIMESTAMP, \n" +
					"CONSTRAINT PK_KillEntry PRIMARY KEY (gameId,victimId),\n" +
					"FOREIGN KEY (gameId) REFERENCES GameEntry(gameId),\n" +
					"FOREIGN KEY (killerId) REFERENCES PlayerEntry(playerId),\n" +
					"FOREIGN KEY (victimId) REFERENCES PlayerEntry(playerId)\n" +
					");"
			);
			createKillEntryTable.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

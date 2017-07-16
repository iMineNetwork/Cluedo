package nl.imine.minigame.cluedo.service.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.model.GameEntry;
import nl.imine.minigame.cluedo.service.GameEntryService;
import nl.imine.minigame.cluedo.util.mysql.MySQLService;

public class GameEntryServiceImpl implements GameEntryService {

	private Logger logger = JavaPlugin.getPlugin(CluedoPlugin.class).getLogger();

	private MySQLService mySQLService;

	public GameEntryServiceImpl(MySQLService mySQLService) {
		this.mySQLService = mySQLService;
	}

	@Override
	public void save(GameEntry gameEntry) {
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("INSERT INTO GameEntry VALUES (?, ?, ?);");

			preparedStatement.setString(1, gameEntry.getGameId().toString());
			preparedStatement.setTimestamp(2, Timestamp.valueOf(gameEntry.getStartTime()));
			preparedStatement.setTimestamp(3, Timestamp.valueOf(gameEntry.getEndTime()));

			preparedStatement.execute();
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
	}

	@Override
	public void update(GameEntry gameEntry) {
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("UPDATE GameEntry SET startTime=?, endTime=? WHERE gameId LIKE ?;");

			preparedStatement.setTimestamp(1, Timestamp.valueOf(gameEntry.getStartTime()));
			preparedStatement.setTimestamp(2, Timestamp.valueOf(gameEntry.getEndTime()));

			preparedStatement.setString(3, gameEntry.getGameId().toString());

			preparedStatement.execute();
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
	}

	@Override
	public GameEntry getById(UUID gameId) {
		GameEntry entry = null;
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("SELECT * FROM GameEntry WHERE gameId LIKE ? LIMIT 1;");
			preparedStatement.setString(1, gameId.toString());

			ResultSet res = preparedStatement.executeQuery();
			while (res.next()) {
				LocalDateTime startDateTime = LocalDateTime.ofInstant(res.getTimestamp("startTime").toInstant(), ZoneId.systemDefault());
				LocalDateTime endDateTime = LocalDateTime.ofInstant(res.getTimestamp("endTime").toInstant(), ZoneId.systemDefault());
				entry = new GameEntry(gameId, startDateTime, endDateTime);
			}
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
		return entry;
	}

	@Override
	public List<GameEntry> getAll() {
		List<GameEntry> entries = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("SELECT * FROM PlayerEntry;");

			ResultSet res = preparedStatement.executeQuery();
			while (res.next()) {
				UUID gameId = UUID.fromString(res.getString("gameId"));
				LocalDateTime startDateTime = LocalDateTime.ofInstant(res.getTimestamp("startTime").toInstant(), ZoneId.systemDefault());
				LocalDateTime endDateTime = LocalDateTime.ofInstant(res.getTimestamp("endTime").toInstant(), ZoneId.systemDefault());
				entries.add(new GameEntry(gameId, startDateTime, endDateTime));
			}
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
		return entries;
	}
}

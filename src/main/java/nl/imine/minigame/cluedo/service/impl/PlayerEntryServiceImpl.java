package nl.imine.minigame.cluedo.service.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import nl.imine.minigame.cluedo.model.PlayerEntry;
import nl.imine.minigame.cluedo.service.PlayerEntryService;
import nl.imine.minigame.cluedo.util.mysql.MySQLService;

public class PlayerEntryServiceImpl implements PlayerEntryService {

	private Logger logger = JavaPlugin.getPlugin(CluedoPlugin.class).getLogger();

	private MySQLService mySQLService;

	public PlayerEntryServiceImpl(MySQLService mySQLService) {
		this.mySQLService = mySQLService;
	}

	@Override
	public void save(PlayerEntry playerEntry) {
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("INSERT INTO PlayerEntry VALUES(?, ?, ?);");

			preparedStatement.setString(1, playerEntry.getGameId().toString());
			preparedStatement.setString(2, playerEntry.getPlayerId().toString());
			preparedStatement.setString(3, playerEntry.getRole().toString());

			preparedStatement.execute();
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}

	@Override
	public void update(PlayerEntry playerEntry) {
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("UPDATE PlayerEntry SET roleType=? WHERE gameId LIKE ? AND playerId LIKE ?;");
			preparedStatement.setString(1, playerEntry.getRole().toString());

			preparedStatement.setString(2, playerEntry.getGameId().toString());
			preparedStatement.setString(3, playerEntry.getPlayerId().toString());

			preparedStatement.execute();
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
	}

	@Override
	public List<PlayerEntry> getByPlayerId(UUID uuid) {
		List<PlayerEntry> entries = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("SELECT * FROM PlayerEntry WHERE playerId LIKE ?;");
			preparedStatement.setString(1, uuid.toString());

			ResultSet res = preparedStatement.executeQuery();
			while (res.next()) {
				UUID gameId = UUID.fromString(res.getString("gameId"));
				UUID playerId = UUID.fromString(res.getString("playerId"));
				RoleType roleType = RoleType.valueOf(res.getString("roleType"));
				entries.add(new PlayerEntry(gameId, playerId, roleType));
			}
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
		return entries;
	}

	@Override
	public List<PlayerEntry> getFromGame(UUID gameId) {
		List<PlayerEntry> entries = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("SELECT * FROM PlayerEntry WHERE gameId LIKE ?;");
			preparedStatement.setString(1, gameId.toString());

			ResultSet res = preparedStatement.executeQuery();
			while (res.next()) {
				UUID playerId = UUID.fromString(res.getString("playerId"));
				RoleType roleType = RoleType.valueOf(res.getString("roleType"));
				entries.add(new PlayerEntry(gameId, playerId, roleType));
			}
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
		return entries;
	}

	@Override
	public PlayerEntry getByPlayerIdInGame(UUID playerId, UUID gameId) {
		PlayerEntry entry = null;
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("SELECT * FROM PlayerEntry WHERE gameId LIKE ? AND playerId LIKE ? LIMIT 1;");
			preparedStatement.setString(1, playerId.toString());
			preparedStatement.setString(2, gameId.toString());

			ResultSet res = preparedStatement.executeQuery();
			while (res.next()) {
				RoleType roleType = RoleType.valueOf(res.getString("roleType"));
				entry = new PlayerEntry(gameId, playerId, roleType);
			}
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
		return entry;
	}
}

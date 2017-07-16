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

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.model.KillEntry;
import nl.imine.minigame.cluedo.service.KillEntryService;
import nl.imine.minigame.cluedo.util.mysql.MySQLService;

public class KillEntryServiceImpl implements KillEntryService {

	private Logger logger = JavaPlugin.getPlugin(CluedoPlugin.class).getLogger();

	private MySQLService mySQLService;

	public KillEntryServiceImpl(MySQLService mySQLService) {
		this.mySQLService = mySQLService;
	}

	@Override
	public void save(KillEntry killEntry) {
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("INSERT INTO KillEntry VALUES (?, ?, ?, ?, ?);");

			preparedStatement.setString(1, killEntry.getGameId().toString());
			preparedStatement.setString(2, killEntry.getKillerEntry().toString());
			preparedStatement.setString(3, killEntry.getVictimEntry().toString());
			preparedStatement.setString(4, killEntry.getWeapon().toString());
			preparedStatement.setTimestamp(5, Timestamp.valueOf(killEntry.getTimestamp()));

			preparedStatement.execute();
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
	}

	@Override
	public void update(KillEntry killEntry) {
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("UPDATE KillEntry SET killerId=?, material=?, timestamp=? WHERE gameId LIKE ? AND victimId LIKE ?;");

			preparedStatement.setString(1, killEntry.getKillerEntry().toString());
			preparedStatement.setString(2, killEntry.getWeapon().toString());
			preparedStatement.setTimestamp(3, Timestamp.valueOf(killEntry.getTimestamp()));

			preparedStatement.setString(4, killEntry.getGameId().toString());
			preparedStatement.setString(5, killEntry.getVictimEntry().toString());


			preparedStatement.execute();
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
	}

	@Override
	public KillEntry getByKillerVictimInGame(UUID killerId, UUID victimId, UUID gameId) {
		KillEntry entry = null;
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("SELECT * FROM KillEntry WHERE gameId LIKE ? AND killerId LIKE ? AND victimId LIKE ? LIMIT 1;");
			preparedStatement.setString(1, gameId.toString());
			preparedStatement.setString(2, killerId.toString());
			preparedStatement.setString(3, victimId.toString());

			ResultSet res = preparedStatement.executeQuery();
			while (res.next()) {
				Material material = Material.getMaterial(res.getString("material"));
				LocalDateTime localDateTime = LocalDateTime.ofInstant(res.getTimestamp("timestamp").toInstant(), ZoneId.systemDefault());
				entry = new KillEntry(gameId, killerId, victimId, material, localDateTime);
			}
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
		return entry;
	}

	@Override
	public List<KillEntry> getByGame(UUID gameId) {
		List<KillEntry> entries = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("SELECT * FROM KillEntry WHERE gameId LIKE ?;");
			preparedStatement.setString(1, gameId.toString());

			ResultSet res = preparedStatement.executeQuery();
			while (res.next()) {
				UUID killerId = UUID.fromString(res.getString("killerId"));
				UUID victimId = UUID.fromString(res.getString("victimId"));
				Material material = Material.getMaterial(res.getString("material"));
				LocalDateTime localDateTime = LocalDateTime.ofInstant(res.getTimestamp("timestamp").toInstant(), ZoneId.systemDefault());
				entries.add(new KillEntry(gameId, killerId, victimId, material, localDateTime));
			}
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
		return entries;
	}

	@Override
	public List<KillEntry> getByAsKiller(UUID killerId) {
		List<KillEntry> entries = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("SELECT * FROM PlayerEntry WHERE killerId LIKE ?;");
			preparedStatement.setString(1, killerId.toString());

			ResultSet res = preparedStatement.executeQuery();
			while (res.next()) {
				UUID gameId = UUID.fromString(res.getString("gameId"));
				UUID victimId = UUID.fromString(res.getString("victimId"));
				Material material = Material.getMaterial(res.getString("material"));
				LocalDateTime localDateTime = LocalDateTime.ofInstant(res.getTimestamp("timestamp").toInstant(), ZoneId.systemDefault());
				entries.add(new KillEntry(gameId, killerId, victimId, material, localDateTime));
			}
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
		return entries;
	}

	@Override
	public List<KillEntry> getByAsVictim(UUID victimId) {
		List<KillEntry> entries = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = mySQLService.getConnection().prepareStatement("SELECT * FROM PlayerEntry WHERE victimId LIKE ?;");
			preparedStatement.setString(1, victimId.toString());

			ResultSet res = preparedStatement.executeQuery();
			while (res.next()) {
				UUID gameId = UUID.fromString(res.getString("gameId"));
				UUID killerId = UUID.fromString(res.getString("killerId"));
				Material material = Material.getMaterial(res.getString("material"));
				LocalDateTime localDateTime = LocalDateTime.ofInstant(res.getTimestamp("timestamp").toInstant(), ZoneId.systemDefault());
				entries.add(new KillEntry(gameId, killerId, victimId, material, localDateTime));
			}
		} catch (SQLException e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
		return entries;
	}
}

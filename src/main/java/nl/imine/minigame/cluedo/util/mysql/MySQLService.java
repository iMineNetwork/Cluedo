package nl.imine.minigame.cluedo.util.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import nl.imine.minigame.cluedo.CluedoPlugin;

public class MySQLService {

	private Logger logger = JavaPlugin.getPlugin(CluedoPlugin.class).getLogger();

	private String username;
	private String password;
	private String jdbcUrl;

	private transient Connection connection;

	public MySQLService(String username, String password, String jdbcUrl) {
		this.username = username;
		this.password = password;
		this.jdbcUrl = jdbcUrl;
	}

	public boolean connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcUrl, username, password);
			return true;
		} catch (Exception e) {
			System.err.println("Unable to connect to database | " + e.getClass().getSimpleName() + ": " + e.getMessage());
			return false;
		}
	}

	public Connection getNewConnection() throws SQLException {
		return DriverManager.getConnection(jdbcUrl, username, password);
	}

	public Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connect();
			}
		} catch (Exception e) {
			logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
		}
		return connection;
	}
}
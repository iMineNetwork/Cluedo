package nl.imine.minigame.cluedo.util.mysql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Logger;

import nl.imine.minigame.cluedo.CluedoPlugin;

public class MySQLConfig {

	private Logger logger = CluedoPlugin.getInstance().getLogger();


	private String jdbcUrl = "UNDEFINED";
	private String user = "UNDEFINED";
	private String password = "UNDEFINED";

	public boolean loadConfigFile() {
		try {
			Path dir = CluedoPlugin.getInstance().getDataFolder().toPath();
			logger.info("Datefolder: " + dir);
			if (!Files.exists(dir) || !Files.isDirectory(dir)) {
				Files.createDirectory(dir);
			}

			Properties properties = null;
			Path config = dir.resolve("mysql.properties");
			if (!Files.exists(config)) {
				createConfigFile(config);
				System.err.println("Created MySQL Properties file in " + dir.toString() + ". Please edit this file with the neccesary login credentials.");
				return false;
			}

			properties = new Properties();
			properties.load(Files.newInputStream(config));

			jdbcUrl = properties.getProperty("JDBC-URL");
			user = properties.getProperty("Username");
			password = properties.getProperty("Password");

			logger.info("JDBC-URL: " + jdbcUrl);
			logger.info("User: " + user);

			return true;
		} catch (Exception e) {
			System.err.println("Could not load MySQL config | " + e.getMessage());
			return false;
		}
	}

	private void createConfigFile(Path config) {
		Properties properties = new Properties();
		properties.setProperty("JDBC-URL", "jdbc:mysql://HOST:PORT/DATABASE");
		properties.setProperty("Username", "user");
		properties.setProperty("Password", "pass");
		try {
			properties.store(Files.newOutputStream(config), "This config contains the credentials and url to the Mysql server to connect to. \nhis is reloaded every time the plugin reloads.");
		} catch (IOException e) {
			System.err.println("Could not create MySQL config | " + e.getMessage());
		}
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}
}
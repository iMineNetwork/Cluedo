package nl.imine.minigame.cluedo.settings;

import nl.imine.minigame.cluedo.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

    private FileConfiguration configuration;

    public Settings(FileConfiguration configuration){
        this.configuration = configuration;
    }

    public void createDefaults(){
        // GENERAL
        configuration.addDefault(Setting.GAME_NAME, "minigame_name");
        configuration.addDefault(Setting.GAME_MAX_PLAYERS, 10);
        configuration.addDefault(Setting.GAME_WORLD_NAME, "world");

        //LOBBY
        configuration.addDefault(Setting.LOBBY_TIME, 15);
        configuration.addDefault(Setting.LOBBY_SPAWN_X, 0.0);
        configuration.addDefault(Setting.LOBBY_SPAWN_Y, 70.0);
        configuration.addDefault(Setting.LOBBY_SPAWN_Z, 0.0);
        
        //PRE-GAME
        configuration.addDefault(Setting.PRE_GAME_TIME, 15);
        configuration.addDefault(Setting.PRE_GAME_SPAWN_X, 0.0);
        configuration.addDefault(Setting.PRE_GAME_SPAWN_Y, 70.0);
        configuration.addDefault(Setting.PRE_GAME_SPAWN_Z, 0.0);
        
        //GAME
        configuration.addDefault(Setting.IN_GAME_TIME, 60);
        
        //END-GAME
        configuration.addDefault(Setting.END_GAME_TIME, 15);

        configuration.options().copyDefaults(true);
    }

    public String getString(String configPath){
        return configuration.getString(configPath);
    }

    public int getInt(String configPath){
        return configuration.getInt(configPath);
    }

    public double getDouble(String configPath){
        return configuration.getDouble(configPath);
    }

    public Location getLocation(String configPath){
        return new Location(Bukkit.getWorld(getString(Setting.GAME_WORLD_NAME)),
                getDouble(configPath + ".x"),
                getDouble(configPath + ".y"),
                getDouble(configPath + ".z")
                );
    }
}

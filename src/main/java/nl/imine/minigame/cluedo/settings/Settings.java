package nl.imine.minigame.cluedo.settings;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import nl.imine.minigame.cluedo.game.player.role.RoleInteractPermission;
import nl.imine.minigame.cluedo.game.player.role.RoleType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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

        //JOB
        configuration.addDefault(Setting.GAME_JOB_REQUIRED_AMOUNT, 5);
        configuration.addDefault(Setting.GAME_JOB_REFRESH_RATE, 30);

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

        //Role block interactions
        Map<String, List<String>> defaults = new HashMap<>();
        defaults.put(Material.OAK_BUTTON.toString(), Collections.singletonList(RoleType.MURDERER.toString()));
        configuration.addDefault(Setting.ROLE_INTERACTION_LIST, defaults);

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

    public List<RoleInteractPermission> getRoleInteractPermissions(){
        List<RoleInteractPermission> roleInteractPermissions = new ArrayList<>();

        //Read the configuration file. Get all the defined Keys. The key names are the Materials subject to the permission and should be used as an identifier.
        ConfigurationSection configurationList = configuration.getConfigurationSection(Setting.ROLE_INTERACTION_LIST);

        //Go through all the keys
        configurationList.getKeys(false).forEach(material -> {
            //After finding a key, get all the roleTypes associated to it.
        	List<RoleType> roles = configurationList.getStringList(material).stream()
					.map(RoleType::valueOf)         //Convert the String to a RoleType
					.collect(Collectors.toList());  //Collect the stream and create a new array containing the allowed RoleTypes

        	roleInteractPermissions.add(new RoleInteractPermission(Material.getMaterial(material), roles));
		});

        return roleInteractPermissions;
    }
}

package nl.imine.minigame.cluedo;

import nl.imine.minigame.cluedo.command.CommandDetective;
import nl.imine.minigame.cluedo.command.CommandMurderer;
import nl.imine.minigame.cluedo.command.CommandParticipate;
import nl.imine.minigame.cluedo.game.CluedoListener;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.game.state.game.CluedoSpawn;
import nl.imine.minigame.cluedo.game.state.game.jobs.AvailableJob;
import nl.imine.minigame.cluedo.game.state.game.jobs.JobManager;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.settings.Settings;
import nl.imine.minigame.cluedo.settings.SpawnLocationService;
import nl.imine.minigame.timer.TimerManager;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class CluedoPlugin extends JavaPlugin {

    private static CluedoMinigame game;
    private static Settings settings;
    private static TimerManager timerManager;
    private static SpawnLocationService spawnLocationService;

    @Override
    public void onEnable() {

        //Initialize Settings
        settings = new Settings(this.getConfig());
        settings.createDefaults();
        this.saveConfig();

        //Initialize Timer Manager
        timerManager = new TimerManager();
        timerManager.init(this);

        //Create World
        WorldCreator worldCreator = new WorldCreator(settings.getString(Setting.GAME_WORLD_NAME))
                .environment(World.Environment.NORMAL)
                .generateStructures(false)
                .type(WorldType.FLAT)
                .generatorSettings("3;minecraft:air;127;decoration;2;");
        World world = Bukkit.createWorld(worldCreator);

        //Set world Gamerules
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.NATURAL_REGENERATION, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setPVP(true);

        //Set world time and weather
        world.setWeatherDuration(1);
        world.setThunderDuration(1);
        world.setTime(18000);

        //Initialize Jobs
        ConfigurationSerialization.registerClass(AvailableJob.class);
        JobManager.init();

        //Initialize spawns
        ConfigurationSerialization.registerClass(CluedoSpawn.class);
        spawnLocationService = new SpawnLocationService();
        spawnLocationService.init();

        //Start Plugin
        game = new CluedoMinigame();

        this.getCommand("murderer").setExecutor(new CommandMurderer());
        this.getCommand("detective").setExecutor(new CommandDetective());
        this.getCommand("cluedo").setExecutor(new CommandParticipate(game));

        game.changeGameState(CluedoStateType.LOBBY);

        //Load Listener
        CluedoListener.init();
    }

    @Override
    public void onDisable() {
        game.shutdown();
        game = null;
        settings = null;
        timerManager = null;
        spawnLocationService = null;
    }

    public static CluedoMinigame getGame() {
        return game;
    }

    public static TimerManager getTimerManager() {
        return timerManager;
    }

    public static Settings getSettings() {
        return settings;
    }

    public static SpawnLocationService getSpawnLocationService() {
        return spawnLocationService;
    }

}

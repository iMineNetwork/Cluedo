package nl.imine.minigame.cluedo;

import nl.imine.minigame.MinigameManager;
import nl.imine.minigame.cluedo.command.CommandDetective;
import nl.imine.minigame.cluedo.command.CommandMurderer;
import nl.imine.minigame.cluedo.command.RemoveMeeseeksCommand;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.CluedoListener;
import nl.imine.minigame.cluedo.game.meeseeks.MeeseeksManager;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.game.state.game.CluedoSpawn;
import nl.imine.minigame.cluedo.game.state.game.jobs.AvailableJob;
import nl.imine.minigame.cluedo.game.state.game.jobs.JobManager;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.settings.Settings;
import nl.imine.minigame.cluedo.settings.SpawnLocationService;
import nl.imine.minigame.cluedo.util.Instances;
import nl.imine.minigame.cluedo.util.mysql.MySQLConfig;
import nl.imine.minigame.cluedo.util.mysql.MySQLService;
import nl.imine.minigame.cluedo.util.mysql.TableCreator;
import nl.imine.minigame.timer.TimerManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CluedoPlugin extends JavaPlugin {

    private static Plugin plugin;
    private static CluedoMinigame game;
    private static Settings settings;
    private static TimerManager timerManager;
    private static SpawnLocationService spawnLocationService;

    @Override
    public void onEnable() {
        CluedoPlugin.plugin = this;

        //Initialize Settings
        CluedoPlugin.settings = new Settings(this.getConfig());
        setUpConfig();

        MySQLService mySQLService = initDatabase();
        TableCreator tableCreator = new TableCreator(mySQLService);
        tableCreator.createTables();
        Instances.initInstances(mySQLService);

        //Initialize Timer Manager
        CluedoPlugin.timerManager = new TimerManager();
        CluedoPlugin.timerManager.init(this);

        this.getCommand("removemeeseeks").setExecutor(new RemoveMeeseeksCommand());
        this.getCommand("removemeeseeks").setTabCompleter(new RemoveMeeseeksCommand());
        
        this.getCommand("murderer").setExecutor(new CommandMurderer());
        this.getCommand("detective").setExecutor(new CommandDetective());

        //Create World
        WorldCreator worldCreator = new WorldCreator(settings.getString(Setting.GAME_WORLD_NAME))
                .environment(World.Environment.NORMAL)
                .generateStructures(false)
                .type(WorldType.FLAT)
                .generatorSettings("3;minecraft:air;127;decoration;2;");
        World world = Bukkit.createWorld(worldCreator);

        //Set world Gamerules
        world.setGameRuleValue("spectatorsGenerateChunks", "false");
        world.setGameRuleValue("doWeatherCycle", "false");
        world.setGameRuleValue("doTileDrops", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("naturalRegeneration", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setGameRuleValue("doEntityDrops", "false");

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
        game.changeGameState(CluedoStateType.LOBBY);

        //Load Listener
        CluedoListener.init();

        //Initialization Finished, register Minigame
        MinigameManager.registerMinigame(game);
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onDisable() {
        CluedoPlugin.plugin = null;
        MeeseeksManager.getInstance().removeAllMeeseekses();
    }

    public static Plugin getInstance() {
        return plugin;
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

    private void setUpConfig() {
        settings.createDefaults();
        this.saveConfig();
    }

    private MySQLService initDatabase() {
        MySQLConfig mySQLConfig = new MySQLConfig();
        mySQLConfig.loadConfigFile();

        MySQLService mySQLService = new MySQLService(mySQLConfig.getUser(), mySQLConfig.getPassword(), mySQLConfig.getJdbcUrl());
        mySQLService.connect();

        return mySQLService;
    }
}

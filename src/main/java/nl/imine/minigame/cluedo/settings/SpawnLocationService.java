package nl.imine.minigame.cluedo.settings;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.state.game.CluedoSpawn;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SpawnLocationService {

    private Logger logger = JavaPlugin.getPlugin(CluedoPlugin.class).getLogger();

    private static final String SPAWNS_PATH = "Spawns";
    private static final Path SPAWNS_FILE = JavaPlugin.getPlugin(CluedoPlugin.class).getDataFolder().toPath().resolve("spawns.yml");

    public SpawnLocationService() {
    }

    /**
     * Create a spawns file if it doesn't exist.
     */
    public void init() {
        try {
            if (!Files.exists(SPAWNS_FILE)) {
                Files.createFile(SPAWNS_FILE);
                FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(SPAWNS_FILE));
                fileConfiguration.set(SPAWNS_PATH, new ArrayList<CluedoSpawn>());
                fileConfiguration.save(SPAWNS_FILE.toFile());
            }
        } catch (IOException e) {
            logger.warning(e.getClass().getSimpleName() + ": " + e.getMessage());;
        }
    }

    /**
     * Read the spawns from file.
     * @return a list containing the possible spawns.
     */
    public List<CluedoSpawn> getSpawns() {
        List<CluedoSpawn> cluedoSpawns = new ArrayList<>();
        try {
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(SPAWNS_FILE));

            //Check objects from file for type as the map may contain non-spawn data.
            //This Could've just been an List#addAll() but that resulted in an "unchecked" warning.
            fileConfiguration.getList(SPAWNS_PATH).stream()
                    .filter(object -> object instanceof CluedoSpawn)
                    .map(object -> (CluedoSpawn) object)
                    .forEach(cluedoSpawns::add);

        } catch (IOException e) {
            logger.warning("Could not loads spawns from file | Caused by: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return cluedoSpawns;
    }
}

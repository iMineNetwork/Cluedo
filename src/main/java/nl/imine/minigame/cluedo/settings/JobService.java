package nl.imine.minigame.cluedo.settings;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.state.game.jobs.AvailableJob;
import nl.imine.minigame.cluedo.util.Log;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JobService {

    private static final String JOBS_PATH = "Jobs";
    private static final Path JOBS_FILE = CluedoPlugin.getInstance().getDataFolder().toPath().resolve("jobs.yml");

    private int requiredJobs = CluedoPlugin.getSettings().getInt(Setting.GAME_JOB_REQUIRED_AMOUNT);

    public JobService() {

    }

    /**
     * Create a jobs file if it doesn't exist.
     */
    public void init() {
        try {
            if (!Files.exists(JOBS_FILE)) {
                Files.createFile(JOBS_FILE);
                FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(JOBS_FILE));
                List<AvailableJob> jobs = new ArrayList<>();
                jobs.add(new AvailableJob(0, 0, 0, "Test", new ItemStack(Material.STONE)));
                fileConfiguration.set(JOBS_PATH, jobs);
                fileConfiguration.save(JOBS_FILE.toFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the jobs from file.
     * @return a list containing the possible jobs.
     */
    public List<AvailableJob> getAvailableJobs() {
        List<AvailableJob> availableJobs = new ArrayList<>();
        try {
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(JOBS_FILE));

            //Check objects from file for type as the map may contain non-job data.
            //This Could've just been an List#addAll() but that resulted in an "unchecked" warning.
            fileConfiguration.getList(JOBS_PATH).stream()
                    .filter(object -> object instanceof AvailableJob)
                    .map(object -> (AvailableJob) object)
                    .forEach(availableJobs::add);

        } catch (IOException e) {
            Log.warning("Could not loads jobs from file | Caused by: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return availableJobs;
    }
}

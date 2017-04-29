package nl.imine.minigame.cluedo.game.state.game.jobs;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class Job extends AvailableJob {

    private Item jobItem;

    public Job(AvailableJob job, Item jobItem) {
        super(job.getLocation(), job.getDescription(), job.getDisplayItem());
        this.jobItem = jobItem;
    }

    public Job(Location location, String jobLocationName, ItemStack displayItem, Item jobItem) {
        super(location, jobLocationName, displayItem);
        this.jobItem = jobItem;
    }

    public Item getJobItem() {
        return jobItem;
    }

    public void setJobItem(Item jobItem) {
        this.jobItem = jobItem;
    }
}

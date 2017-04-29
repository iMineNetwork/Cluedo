package nl.imine.minigame.cluedo.game.state.game.jobs;

import nl.imine.minigame.cluedo.CluedoPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Job")
public class AvailableJob implements ConfigurationSerializable {

    private Location location;
    private String description;

    private ItemStack displayItem;

    public AvailableJob(Location location, String description, ItemStack displayItem) {
        this.location = location;
        this.description = description;
        this.displayItem = displayItem;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("spawnX", location.getX());
        ret.put("spawnY", location.getY());
        ret.put("spawnZ", location.getZ());

        ret.put("description", description);

        ret.put("description", displayItem.getType());
        ret.put("description", displayItem.getDurability());

        return ret;
    }

    public static AvailableJob deserialize(Map<String, Object> objectMap){
        Location location = new Location(CluedoPlugin.getGame().getCluedoWorld(),
                Double.valueOf(objectMap.get("spawnX").toString()),
                Double.valueOf(objectMap.get("spawnY").toString()),
                Double.valueOf(objectMap.get("spawnZ").toString()));

        String displayName = objectMap.get("description").toString();

        ItemStack item = new ItemStack(Material.valueOf(objectMap.get("jobDisplayItemType").toString()), Integer.parseInt(objectMap.get("jobDisplayItemData").toString()));
        return new AvailableJob(
                location,
                displayName,
                item);
    }
}

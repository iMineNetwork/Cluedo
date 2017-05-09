package nl.imine.minigame.cluedo.game.state.game.jobs;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.state.game.CluedoGame;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Job")
public class AvailableJob implements ConfigurationSerializable {

    private double spawnX;
    private double spawnY;
    private double spawnZ;
    private String description;

    private ItemStack displayItem;

    public AvailableJob(double spawnX, double spawnY, double SpawnZ, String description, ItemStack displayItem) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        this.description = description;
        this.displayItem = displayItem;
    }

    public Location getLocation() {
        return new Location(CluedoPlugin.getGame().getCluedoWorld(), spawnX, spawnY, spawnZ);
    }

    public String getDescription() {
        return description;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("spawnX", spawnX);
        ret.put("spawnY", spawnY);
        ret.put("spawnZ", spawnZ);

        ret.put("description", description);

        ret.put("itemType", displayItem.getType().toString());
        ret.put("itemData", displayItem.getDurability());

        return ret;
    }

    public static AvailableJob deserialize(Map<String, Object> objectMap){

        String displayName = objectMap.get("description").toString();

        ItemStack item = new ItemStack(Material.valueOf(objectMap.get("itemType").toString()), Integer.parseInt(objectMap.get("itemData").toString()));
        return new AvailableJob(
                Double.parseDouble(objectMap.get("spawnX").toString()),
                Double.parseDouble(objectMap.get("spawnY").toString()),
                Double.parseDouble(objectMap.get("spawnZ").toString()),
                displayName,
                item);
    }
}

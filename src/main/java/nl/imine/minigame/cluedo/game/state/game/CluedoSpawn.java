package nl.imine.minigame.cluedo.game.state.game;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CluedoSpawn implements ConfigurationSerializable {

    private static final World world = Bukkit.getWorld(CluedoPlugin.getSettings().getString(Setting.GAME_WORLD_NAME));

    private double spawnX;
    private double spawnY;
    private double spawnZ;

    private float spawnYaw;
    private float spawnPitch;

    public CluedoSpawn(double spawnX, double spawnY, double spawnZ, float spawnYaw, float spawnPitch) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        this.spawnYaw = spawnYaw;
        this.spawnPitch = spawnPitch;
    }

    public Location getLocation(){
        return new Location(world, spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("spawnX", spawnX);
        ret.put("spawnY", spawnY);
        ret.put("spawnZ", spawnZ);
        ret.put("spawnYaw", spawnYaw);
        ret.put("spawnPitch", spawnPitch);
        return ret;
    }

    public static CluedoSpawn deserialize(Map<String, Object> objectMap){
        return new CluedoSpawn(Double.valueOf(objectMap.get("spawnX").toString()),
                Double.valueOf(objectMap.get("spawnY").toString()),
                        Double.valueOf(objectMap.get("spawnZ").toString()),
                Float.valueOf(objectMap.get("spawnYaw").toString()),
                Float.valueOf(objectMap.get("spawnPitch").toString()));
    }
}

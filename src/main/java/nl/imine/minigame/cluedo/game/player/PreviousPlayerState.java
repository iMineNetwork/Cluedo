package nl.imine.minigame.cluedo.game.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PreviousPlayerState {

    private final ItemStack[] content;
    private final Location location;
    private final double health;
    private final int food;
    private final float saturation;
    private final float fallingDistance;
    private final float exhaustion;
    private final int oxygen;

    public static PreviousPlayerState loadFromPlayer(Player player) {
        return new PreviousPlayerState(
                player.getInventory().getContents(),
                player.getLocation(),
                player.getHealth(),
                player.getFoodLevel(),
                player.getSaturation(),
                player.getFallDistance(),
                player.getExhaustion(),
                player.getRemainingAir()
        );
    }

    public PreviousPlayerState(ItemStack[] content, Location location, double health, int food, float saturation, float fallingDistance, float exhaustion, int oxygen) {
        this.content = content;
        this.location = location;
        this.health = health;
        this.food = food;
        this.saturation = saturation;
        this.fallingDistance = fallingDistance;
        this.exhaustion = exhaustion;
        this.oxygen = oxygen;
    }

    public void restoreState(Player player) {
        player.teleport(location);
        player.getInventory().setContents(content);
        player.setHealth(health);
        player.setFoodLevel(food);
        player.setSaturation(saturation);
        player.setFallDistance(fallingDistance);
        player.setExhaustion(exhaustion);
        player.setRemainingAir(oxygen);
    }

    public ItemStack[] getContent() {
        return content;
    }

    public Location getLocation() {
        return location;
    }

    public double getHealth() {
        return health;
    }

    public int getFood() {
        return food;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getFallingDistance() {
        return fallingDistance;
    }

    public float getExhaustion() {
        return exhaustion;
    }

    public int getOxygen() {
        return oxygen;
    }
}

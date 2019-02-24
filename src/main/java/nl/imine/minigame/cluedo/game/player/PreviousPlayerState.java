package nl.imine.minigame.cluedo.game.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PreviousPlayerState {

    private final ItemStack[] inventoryContent;
    private final ItemStack[] enderChestContent;
    private final Location location;
    private final double health;
    private final int food;
    private final float saturation;
    private final float fallingDistance;
    private final float exhaustion;
    private final int oxygen;
    private final int fireTicks;


    public static PreviousPlayerState loadFromPlayer(Player player) {
        return new PreviousPlayerState(
                player.getInventory().getContents(),
                player.getEnderChest().getContents(),
                player.getLocation(),
                player.getHealth(),
                player.getFoodLevel(),
                player.getSaturation(),
                player.getFallDistance(),
                player.getExhaustion(),
                player.getRemainingAir(),
                player.getFireTicks()
        );
    }

    public PreviousPlayerState(ItemStack[] inventoryContent, ItemStack[] enderChestContent, Location location, double health, int food, float saturation, float fallingDistance, float exhaustion, int oxygen, int fireTicks) {
        this.inventoryContent = inventoryContent;
        this.enderChestContent = enderChestContent;
        this.location = location;
        this.health = health;
        this.food = food;
        this.saturation = saturation;
        this.fallingDistance = fallingDistance;
        this.exhaustion = exhaustion;
        this.oxygen = oxygen;
        this.fireTicks = fireTicks;
    }

    public void restoreState(Player player) {
        player.teleport(location);
        player.getInventory().setContents(inventoryContent);
        player.setHealth(health);
        player.setFoodLevel(food);
        player.setSaturation(saturation);
        player.setFallDistance(fallingDistance);
        player.setExhaustion(exhaustion);
        player.setRemainingAir(oxygen);
        player.setFireTicks(fireTicks);
    }

    public ItemStack[] getInventoryContent() {
        return inventoryContent;
    }

    public ItemStack[] getEnderChestContent() {
        return enderChestContent;
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

    public int getFireTicks() {
        return fireTicks;
    }
}

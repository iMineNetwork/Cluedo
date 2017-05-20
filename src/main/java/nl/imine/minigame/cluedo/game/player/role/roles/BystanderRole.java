package nl.imine.minigame.cluedo.game.player.role.roles;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import nl.imine.minigame.cluedo.game.player.role.CluedoRole;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BystanderRole extends CluedoRole {

    public BystanderRole() {
        super(RoleType.BYSTANDER);
    }

    @Override
    public void preparePlayer(Player player) {
        //Clean player's inventory
        player.closeInventory();
        player.getInventory().clear();

        ItemStack mrMeeseeks = new ItemStack(Material.CARROT_STICK);
        mrMeeseeks.setAmount(1);
        mrMeeseeks.setDurability((short) 1);

        if (player.hasPermission("imine.cluedo.mrmeeseeks")) {
            player.getInventory().setItem(8, mrMeeseeks);
        }
        //Set gamemode
        player.setGameMode(GameMode.ADVENTURE);
    }
}

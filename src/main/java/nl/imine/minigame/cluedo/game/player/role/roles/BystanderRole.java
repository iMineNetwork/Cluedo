package nl.imine.minigame.cluedo.game.player.role.roles;

import nl.imine.minigame.cluedo.game.meeseeks.MeeseeksManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import nl.imine.minigame.cluedo.game.player.role.CluedoRole;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BystanderRole extends CluedoRole {

    public BystanderRole() {
        super(RoleType.BYSTANDER, 25);
    }

    @Override
    public void preparePlayer(Player player) {
        //Clean player's inventory
        player.closeInventory();
        player.getInventory().clear();

        ItemStack grayDye = new ItemStack(Material.INK_SACK, 1, (short) 8);
        ItemMeta grayDyeMeta = grayDye.getItemMeta();
        grayDyeMeta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Bystander");
        grayDye.setItemMeta(grayDyeMeta);

        //Give the player their items
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().setItem(17, grayDye);

        if (player.hasPermission("imine.cluedo.pet")) {
            player.getInventory().setItem(8, MeeseeksManager.getInstance().getMeeseeksSpawningItem());
        }
        //Set gamemode
        player.setGameMode(GameMode.ADVENTURE);
    }
}

package nl.imine.minigame.cluedo.game.player.role.roles;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nl.imine.minigame.cluedo.game.player.role.CluedoRole;
import nl.imine.minigame.cluedo.game.player.role.RoleType;

public class DetectiveRole extends CluedoRole {

	public DetectiveRole() {
		super(RoleType.DETECTIVE);
	}

	@Override
	public void preparePlayer(Player player) {
		//Clean player's inventory
		player.closeInventory();
		player.getInventory().clear();

		//Set gamemode
		player.setGameMode(GameMode.ADVENTURE);

		//Set inventory
		ItemStack bow = new ItemStack(Material.BOW);
		ItemMeta bowMeta = bow.getItemMeta();
		bowMeta.setUnbreakable(true);
		bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		bow.setItemMeta(bowMeta);

		player.getInventory().addItem(bow, new ItemStack(Material.ARROW));
	}
}
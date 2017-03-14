package nl.imine.minigame.cluedo.game.player.role.roles;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nl.imine.minigame.cluedo.game.player.role.CluedoRole;
import nl.imine.minigame.cluedo.game.player.role.RoleType;

public class MurderRole extends CluedoRole {

	public MurderRole() {
		super(RoleType.MURDERER);
	}

	@Override
	public void preparePlayer(Player player) {
		//Clean player's inventory
		player.closeInventory();
		player.getInventory().clear();

		//Set gamemode
		player.setGameMode(GameMode.ADVENTURE);

		//Set inventory
		ItemStack knife = new ItemStack(Material.WOOD_SWORD);
		ItemMeta knifeMeta = knife.getItemMeta();
		knifeMeta.setUnbreakable(true);
		knife.setItemMeta(knifeMeta);

		player.getInventory().addItem(knife);
	}
}

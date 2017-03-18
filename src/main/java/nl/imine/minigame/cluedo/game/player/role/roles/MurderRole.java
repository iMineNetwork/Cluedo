package nl.imine.minigame.cluedo.game.player.role.roles;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;

import nl.imine.minigame.cluedo.game.player.role.CluedoRole;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import org.bukkit.ChatColor;
import org.bukkit.Color;

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

        //Create knife itemstack
        ItemStack knife = new ItemStack(Material.WOOD_SWORD);
        ItemMeta knifeMeta = knife.getItemMeta();
        knifeMeta.setUnbreakable(true);
        knife.setItemMeta(knifeMeta);

        //Create Potion itemstack
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.setDisplayName(ChatColor.WHITE + "Potion of Invisibility");
        potionMeta.setColor(Color.GRAY);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 400, 0, true, false), true);
        potion.setItemMeta(potionMeta);

        //Give the player their items
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().setItem(1, knife);
        player.getInventory().setItem(2, potion);

    }
}

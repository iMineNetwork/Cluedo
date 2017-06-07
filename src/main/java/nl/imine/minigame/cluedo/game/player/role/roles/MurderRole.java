package nl.imine.minigame.cluedo.game.player.role.roles;

import java.util.ArrayList;
import java.util.List;
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
import org.bukkit.inventory.ItemFlag;

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

        //Create invisibility potion itemstack
        ItemStack invisibilityPotion = new ItemStack(Material.POTION);
        PotionMeta invisibilitypotionMeta = (PotionMeta) invisibilityPotion.getItemMeta();
        invisibilitypotionMeta.setDisplayName(ChatColor.WHITE + "Potion of Invisibility");
        invisibilitypotionMeta.setColor(Color.GRAY);
        invisibilitypotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 400, 0, true, false), true);
        invisibilityPotion.setItemMeta(invisibilitypotionMeta);

        //Create Damage Potion itemstack
        ItemStack damagePotion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta damagePotionMeta = (PotionMeta) damagePotion.getItemMeta();
        damagePotionMeta.setDisplayName(ChatColor.WHITE + "Instant kill potion");
        damagePotionMeta.setColor(Color.PURPLE);
        damagePotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 50, true, false), true);
        damagePotionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED + "Instant kill, use with caution!");
        damagePotionMeta.setLore(lore);
        damagePotion.setItemMeta(damagePotionMeta);

        ItemStack mrMeeseeks = new ItemStack(Material.CARROT_STICK);
        mrMeeseeks.setAmount(1);
        mrMeeseeks.setDurability((short) 1);

        //Give the player their items
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().setItem(1, knife);
        player.getInventory().setItem(2, invisibilityPotion);
        player.getInventory().setItem(3, damagePotion);
        if (player.hasPermission("imine.cluedo.pet")) {
            player.getInventory().setItem(8, mrMeeseeks);
        }

    }
}

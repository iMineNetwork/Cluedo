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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemFlag;

public class MurderRole extends CluedoRole {

    public MurderRole() {
        super(RoleType.MURDERER, 25);
    }

    @Override
    public void preparePlayer(Player player) {
        //Clean player's inventory
        player.closeInventory();
        player.getInventory().clear();

        //Set gamemode
        player.setGameMode(GameMode.ADVENTURE);

        //Create knife ItemStack
        ItemStack knife = new ItemStack(Material.WOODEN_SWORD);

        ItemMeta knifeMeta = knife.getItemMeta();
        knifeMeta.setUnbreakable(true);
        knife.setItemMeta(knifeMeta);

        //Create invisibility potion ItemStack
        ItemStack invisibilityPotion = new ItemStack(Material.POTION);
        PotionMeta invisibilityPotionMeta = (PotionMeta) invisibilityPotion.getItemMeta();
        invisibilityPotionMeta.setDisplayName(ChatColor.WHITE + "Potion of Invisibility");
        invisibilityPotionMeta.setColor(Color.GRAY);
        invisibilityPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 400, 0, true, false), true);
        invisibilityPotion.setItemMeta(invisibilityPotionMeta);

        //Create Damage Potion ItemStack
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

        ItemStack murdererRoleToken = new ItemStack(Material.RED_DYE, 1);
        ItemMeta murdererRoleTokenItemMeta = murdererRoleToken.getItemMeta();
        murdererRoleTokenItemMeta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Murderer");
        murdererRoleToken.setItemMeta(murdererRoleTokenItemMeta);

        //Give the player their items
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().setItem(1, knife);
        player.getInventory().setItem(2, invisibilityPotion);
        player.getInventory().setItem(3, damagePotion);
        player.getInventory().setItem(17, murdererRoleToken);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.minigame.cluedo.command;

import net.md_5.bungee.api.ChatColor;
import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Dennis
 */
public class CommandDetective implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (CluedoPlugin.getGame().getCluedoPlayer(player) == null) {
            player.sendMessage(ChatColor.RED + "You cannot use this it you are not part of the game");
            return true;
        }

        if (!(CluedoPlugin.getGame().getGameState().getState() == CluedoStateType.LOBBY)) {
            player.sendMessage(ChatColor.RED + "This can only be used when the game is in the lobby state");
            return true;
        }

        if (player.getInventory().contains(new ItemStack(Material.BLUE_DYE, 1))) {
            player.sendMessage(ChatColor.RED + "You're already going to spawn as a detective");
            return true;
        }

        if (player.getInventory().contains(new ItemStack(Material.RED_DYE, 1))) {
            player.sendMessage(ChatColor.RED + "We're sorry, but the murderer cannot be a detective at the same time");
            return true;
        }

        if (player.getLevel() >= 10) {
            player.setLevel(player.getLevel() - 10);
            ItemStack detectiveRoleToken = new ItemStack(Material.BLUE_DYE, 1);

            ItemMeta detectiveRoleTokenItemMeta = detectiveRoleToken.getItemMeta();

            detectiveRoleTokenItemMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "You are guaranteed to be a detective next game");

            detectiveRoleToken.setItemMeta(detectiveRoleTokenItemMeta);

            player.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "You will be a detective next game, good luck!");
            player.getInventory().setHeldItemSlot(0);
            player.getInventory().setItem(4, detectiveRoleToken);
        }else{
             player.sendMessage(ChatColor.RED + "You'll need 10 levels to become a detective!");
        }

        return true;
    }

}

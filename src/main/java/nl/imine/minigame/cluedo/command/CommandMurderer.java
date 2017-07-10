/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.minigame.cluedo.command;

import java.util.ArrayList;
import java.util.List;
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
public class CommandMurderer implements CommandExecutor {

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

        if (player.getInventory().contains(new ItemStack(Material.INK_SACK, 1, (short) 1))) {
            player.sendMessage(ChatColor.RED + "You're already going to spawn as a murderer");
            return true;
        }

        if (player.getInventory().contains(new ItemStack(Material.INK_SACK, 1, (short) 4))) {
            player.sendMessage(ChatColor.RED + "We're sorry, but the detective cannot be a murderer at the same time");
            return true;
        }

        if (player.getLevel() >= 15) {
            player.setLevel(player.getLevel() - 15);
            ItemStack roseRed = new ItemStack(Material.INK_SACK, 1, (short) 1);

            ItemMeta roseRedMeta = roseRed.getItemMeta();

            roseRedMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "You are guaranteed a murderer next game");

            roseRed.setItemMeta(roseRedMeta);

            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You will be a murderer next game, good luck!");
            player.getInventory().setHeldItemSlot(0);
            player.getInventory().setItem(4, roseRed);
        }else{
             player.sendMessage(ChatColor.RED + "You'll need 15 levels to become a murderer!");
        }

        return true;
    }

}

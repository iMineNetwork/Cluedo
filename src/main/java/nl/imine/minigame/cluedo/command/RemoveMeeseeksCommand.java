/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.minigame.cluedo.command;

import java.util.ArrayList;
import java.util.List;
import nl.imine.minigame.cluedo.game.meeseeks.MeeseeksManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public class RemoveMeeseeksCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("imine.cluedo.command")) {
            return true;
        }
        if (args.length == 0) {
            MeeseeksManager.getInstance().removeAllMeeseekses();
        } else {
            for (String arg : args) {
                MeeseeksManager.getInstance().removeMeeseekses(Bukkit.getPlayer(arg));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("imine.cluedo.command")) {
            return null;
        }
        List<String> players = new ArrayList<>();

        if (sender instanceof Player) {
            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(onlinePlayer -> ((Player) sender).canSee(onlinePlayer))
                    .filter(onlinePlayer -> (args.length == 0 || onlinePlayer.getDisplayName().contains(args[args.length])))
                    .forEach(onlinePlayer -> {
                        players.add(onlinePlayer.getDisplayName());
                    });
        } else {
            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(onlinePlayer -> (args.length == 0 || onlinePlayer.getDisplayName().contains(args[args.length])))
                    .forEach(onlinePlayer -> {
                        players.add(onlinePlayer.getDisplayName());

                    });
        }

        return players;
    }

}

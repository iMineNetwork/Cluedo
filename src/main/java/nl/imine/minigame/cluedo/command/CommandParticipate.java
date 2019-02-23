package nl.imine.minigame.cluedo.command;

import nl.imine.minigame.cluedo.game.CluedoMinigame;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandParticipate implements TabExecutor {

    private final CluedoMinigame cluedoMinigame;

    public CommandParticipate(CluedoMinigame cluedoMinigame) {
        this.cluedoMinigame = cluedoMinigame;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by an in-game Player");
            return true;
        }

        if(args.length != 1){
            return false;
        }

        if(args[0].equalsIgnoreCase("join")) {
            cluedoMinigame.joinPlayer((Player) sender);
        } else if (args[0].equalsIgnoreCase("leave")){
            cluedoMinigame.leavePlayer((Player) sender);
        } else{
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by an in-game Player");
            return Collections.emptyList();
        }

        if(args.length != 1){
            return Arrays.asList("join", "leave");
        }

        return Collections.emptyList();
    }
}

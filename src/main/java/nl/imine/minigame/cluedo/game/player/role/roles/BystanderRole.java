package nl.imine.minigame.cluedo.game.player.role.roles;

import nl.imine.minigame.cluedo.game.meeseeks.MeeseeksManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import nl.imine.minigame.cluedo.game.player.role.CluedoRole;
import nl.imine.minigame.cluedo.game.player.role.RoleType;

public class BystanderRole extends CluedoRole {

    public BystanderRole() {
        super(RoleType.BYSTANDER, 50);
    }

    @Override
    public void preparePlayer(Player player) {
        //Clean player's inventory
        player.closeInventory();
        player.getInventory().clear();

        if (player.hasPermission("imine.cluedo.pet")) {
            player.getInventory().setItem(8, MeeseeksManager.getInstance().getMeeseeksSpawningItem());
        }
        //Set gamemode
        player.setGameMode(GameMode.ADVENTURE);
    }
}

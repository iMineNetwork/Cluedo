package nl.imine.minigame.cluedo.game.player.role.roles;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import nl.imine.minigame.cluedo.game.player.role.CluedoRole;
import nl.imine.minigame.cluedo.game.player.role.RoleType;

public class LobbyRole extends CluedoRole {

	public LobbyRole() {
		super(RoleType.LOBBY);
	}

	@Override
	public void preparePlayer(Player player) {
		//Clean player's inventory
		player.closeInventory();
		player.getInventory().clear();

		//Set gamemode
		player.setGameMode(GameMode.ADVENTURE);
	}
}

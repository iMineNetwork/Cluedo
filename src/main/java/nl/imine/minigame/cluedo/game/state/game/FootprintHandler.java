package nl.imine.minigame.cluedo.game.state.game;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.Particle;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.player.role.RoleType;

public class FootprintHandler implements Runnable {

	@Override
	public void run() {
		List<CluedoPlayer> players = CluedoPlugin.getGame().getCluedoPlayers();

		players.forEach(cluedoPlayer -> {
			//Track footprints of detectives and bystanders
			if (cluedoPlayer.getRole().isInnocent()) {
				cluedoPlayer.addFootprint(cluedoPlayer.getPlayer().getLocation());
			}
			//Do not check directly against murderer so spectators can view too
			if (!cluedoPlayer.getRole().isInnocent()) {
				players.stream()
						.filter(trackingPlayer -> !trackingPlayer.getRole().getRoleType().equals(RoleType.MURDERER))
						.forEach(trackingPlayer -> {
							//Show the footprints
							trackingPlayer.getFootprints().forEach(footprint -> {
								cluedoPlayer.getPlayer().spawnParticle(Particle.REDSTONE, footprint, 0, new Particle.DustOptions(trackingPlayer.getFootprintColor(), 1));
							});
						});
			}
		});
	}
}

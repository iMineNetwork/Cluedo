package nl.imine.minigame.cluedo.game.state.game;

import nl.imine.minigame.cluedo.CluedoPlugin;

public class FootprintHandler implements Runnable {

	@Override
	public void run() {
		CluedoPlugin.getGame().getCluedoPlayers().forEach(cluedoPlayer -> {
			cluedoPlayer.addFootprint(cluedoPlayer.getPlayer().getLocation());
		});
	}
}

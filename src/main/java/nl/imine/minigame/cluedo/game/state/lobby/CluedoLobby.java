package nl.imine.minigame.cluedo.game.state.lobby;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import nl.imine.minigame.cluedo.CluedoPlugin;
import nl.imine.minigame.cluedo.game.CluedoMinigame;
import nl.imine.minigame.cluedo.game.player.CluedoPlayer;
import nl.imine.minigame.cluedo.game.player.role.RoleType;
import nl.imine.minigame.cluedo.game.state.CluedoState;
import nl.imine.minigame.cluedo.game.state.CluedoStateType;
import nl.imine.minigame.cluedo.settings.Setting;
import nl.imine.minigame.cluedo.util.PlayerUtil;
import nl.imine.minigame.timer.Timer;
import nl.imine.minigame.timer.TimerHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CluedoLobby extends CluedoState implements TimerHandler {

    private Logger logger = JavaPlugin.getPlugin(CluedoPlugin.class).getLogger();

    private CluedoMinigame cluedoMinigame;
    private int gameTimer = CluedoPlugin.getSettings().getInt(Setting.LOBBY_TIME);
    private Location spawnLocation = CluedoPlugin.getSettings().getLocation(Setting.LOBBY_SPAWN);
    private Timer timer;

    public CluedoLobby(CluedoMinigame cluedoMinigame) {
        super(CluedoStateType.LOBBY);
        this.cluedoMinigame = cluedoMinigame;
    }

    @Override
    public void handleStateChange() {
        logger.finer("Handling state change for: " + this.getClass().getSimpleName());
        this.timer = CluedoPlugin.getTimerManager().createTimer("Lobby", gameTimer, this);
        cluedoMinigame.getPlayers().forEach(this::handlePlayer);
    }

    @Override
    public void onTimerEnd() {
        logger.finest("Handling timer end for: " + this.getClass().getSimpleName());
        cluedoMinigame.getPlayers().forEach(timer::hideTimer);

        //A game should always contain at least 3 players
        if (cluedoMinigame.getCluedoPlayers().size() >= 3) {

            /* As we don't accept players anymore when the game has already started. We assign roles
            at the end of the lobby rather then when a player joins preparation. */
            Random random = new Random();

            List<CluedoPlayer> assignablePlayers = cluedoMinigame.getCluedoPlayers().stream()
                    .filter(player -> player.getRole().getRoleType().equals(RoleType.LOBBY))
                    .collect(Collectors.toList());

            //if the player has red dye in his inventory he has to become the murderer
            //if the player has lapis in his inventory he has to become the detective
            List<CluedoPlayer> forceMurderers = new ArrayList<>();
            assignablePlayers.forEach(player -> {
                for (ItemStack item : player.getPlayer().getInventory().getContents()) {
                    if (item != null && item.getType() == Material.INK_SACK && item.getDurability() == (short) 1) {
                        forceMurderers.add(player);
                    }
                }
            });

            forceMurderers.stream().forEach(player -> {
                player.setRole(RoleType.MURDERER);
                assignablePlayers.remove(player);
            });

            List<CluedoPlayer> forceDetectives = new ArrayList<>();
            
            assignablePlayers.forEach(player -> {
                for (ItemStack item : player.getPlayer().getInventory().getContents()) {
                    if (item != null && item.getType() == Material.INK_SACK && item.getDurability() == (short) 4) {
                        forceDetectives.add(player);
                    }
                }
            });

            forceDetectives.stream().forEach(player -> {
                player.setRole(RoleType.DETECTIVE);
                assignablePlayers.remove(player);
            });

            if (forceMurderers.isEmpty()) {
                //Select a random player and make him the murderer, then remove him from the assignable list
                int assignIndex = random.nextInt(assignablePlayers.size());
                assignablePlayers.get(assignIndex).setRole(RoleType.MURDERER);
                assignablePlayers.remove(assignIndex);
            }

            if (forceDetectives.isEmpty() && !assignablePlayers.isEmpty()) {
                //Select a random player and make him the detective, then remove him from the assignable list
                int assignIndex = random.nextInt(assignablePlayers.size());
                assignablePlayers.get(assignIndex).setRole(RoleType.DETECTIVE);
                assignablePlayers.remove(assignIndex);
            }

            //Assign all the remaining players to Bystander
            assignablePlayers.forEach(assignablePlayer -> assignablePlayer.setRole(RoleType.BYSTANDER));

            //Set the player's footprint color
            for (int i = 0; i < cluedoMinigame.getCluedoPlayers().size(); i++) {
                CluedoPlayer cluedoPlayer = cluedoMinigame.getCluedoPlayers().get(i);

                //Get the color in a relative spaced HSB (Hue, Saturation, Brightness) spectrum.
                //HSB Ensures we get bright, vibrant colors without doing difficult calculations.
                Color color = Color.getHSBColor((1F / cluedoMinigame.getCluedoPlayers().size()) * i, 1, 1);

                //Debug: Calculate RGB from HSV values.
                logger.finest(String.format("(%s) HSV: R:%s, G:%s, B:%s", cluedoPlayer.getPlayer().getName(), color.getRed(), color.getGreen(), color.getBlue()));
                float colorR = (cluedoPlayer.getFootprintColor().getRed() / 255F) - 1F;
                float colorG = (cluedoPlayer.getFootprintColor().getGreen() / 255F);
                float colorB = (cluedoPlayer.getFootprintColor().getBlue() / 255F);
                logger.finest(String.format("(%s) RGB: R:%s, G:%s, B:%s", cluedoPlayer.getPlayer().getName(), colorR, colorG, colorB));

                //Set the player's footprint color
                cluedoPlayer.setFootprintColor(color);
            }

            cluedoMinigame.changeGameState(CluedoStateType.PRE_GAME);
        } else {
            //Restart lobby timer
            this.timer = CluedoPlugin.getTimerManager().createTimer("Lobby", gameTimer, this);
            cluedoMinigame.getPlayers().forEach(timer::showTimer);
        }
    }

    @Override
    public CluedoStateType getState() {
        return cluedoStateType;
    }

    @Override
    public void handlePlayer(Player player) {
        PlayerUtil.cleanPlayer(player, false);
        timer.showTimer(player);
        cluedoMinigame.getCluedoPlayers().stream()
                .filter(cluedoPlayer -> cluedoPlayer.getPlayer().equals(player))
                .findAny()
                .ifPresent(cluedoPlayer -> cluedoPlayer.setRole(RoleType.LOBBY));
        player.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public void handlePlayerDeath(Player player) {
        PlayerUtil.cleanPlayer(player, true);
        player.teleport(spawnLocation);
    }

    @Override
    public Location getRespawnLocation() {
        return spawnLocation;
    }
}

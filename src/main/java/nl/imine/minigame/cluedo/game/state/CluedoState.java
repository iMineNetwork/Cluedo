package nl.imine.minigame.cluedo.game.state;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class CluedoState {

    /**
     *alle blocks waarmee een speler tijdens een bepaalde game state mee kunnen interacten
     */
    public static final ArrayList<Material> interactableItems = new ArrayList<Material>();
    
    /**
     * alle blocks waar de murderer mee kan interacten tijdens een bepaalde game state
     */
    public static final ArrayList<Material> murdererInteractableItems = new ArrayList<Material>();
    
    public CluedoStateType cluedoStateType;
    
    public ArrayList<Material> getInteractableItems(){
        return interactableItems;
    }
    
    public ArrayList<Material> getMurdererInteractableItems(){
        return murdererInteractableItems;
    }
    
    public abstract void handleStateChange();
    public abstract CluedoStateType getState();
    public abstract void handlePlayer(Player player);
    public abstract void handlePlayerDeath(Player player);
    public abstract Location getRespawnLocation();
}

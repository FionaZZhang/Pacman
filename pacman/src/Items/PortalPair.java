package src.Items;

import ch.aplu.jgamegrid.*;
import java.util.ArrayList;

public class PortalPair {
    // Each portal is an Item
    private ArrayList<Item> portals;
    // Flag to check the nature of PacMan-Portal overlap
    // (i.e., did PacMan move onto Portal, or was PacMan transported onto Portal?)
    private boolean movedOntoPortal = true;
    public final static int VALID_PAIR = 2;
    public final static int EMPTY_PAIR = 0;

    public PortalPair(){
        portals = new ArrayList<>();
    }

    /**
     * Function to add portals during Pacman game
     */
    public void addPortal(Item portal){
        portals.add(portal);
    }

    /**
     * Function used in Level Check to detect validity of current portal pair, either 0 or 2 for each "type" of portal
     */
    public boolean checkPortalTypeIsValid(){
        return (portals.size() == VALID_PAIR) || (portals.size() == EMPTY_PAIR);
    }

    /**
     * Function to transport PacMan to the other portal
     */
    public Location moveActor(Actor actor){
        if (portals.size() == 0){
            return null;
        }
        // If PacMan overlapped with portal #1, move PacMan to portal #2
        Item portal1 = portals.get(0);
        Item portal2 = portals.get(1);
        if (portal1.getLocation().equals(actor.getLocation()) && movedOntoPortal){
            movedOntoPortal = false;
            return portal2.getLocation();
        }
        // If PacMan overlapped with portal #2, move PacMan to portal #1
        else if (portal2.getLocation().equals(actor.getLocation()) && movedOntoPortal){
            movedOntoPortal = false;
            return portal1.getLocation();
        }
        return null;
    }

    /**
     * Function to reset movedOntoPortal flag, so that PacMan can repeatedly travel via portals
     */
    public void setMovedOntoPortal(Actor actor){
        if (portals.size() == 0){
            return;
        }
        Item portal1 = portals.get(0);
        Item portal2 = portals.get(1);
        if (!(portal1.getLocation().equals(actor.getLocation()) &&
                    portal2.getLocation().equals(actor.getLocation()))){
            movedOntoPortal = true;
        }
    }
}

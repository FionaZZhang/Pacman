package src.checker;

import ch.aplu.jgamegrid.Location;
import src.grid.Grid;
import src.Items.Item;
import src.Items.ItemType;
import src.Items.PortalPair;
import java.io.File;
import java.util.ArrayList;

public class CheckB extends LevelCheckComponent{
    /**
     * check whether all portals are valid,
     * if fails the check, return error log as a string
     */
    public String checkLevel(File file, Grid grid) {
        char tileChar;
        Location location;
        String log = "";
        PortalPair white = new PortalPair();
        PortalPair yellow = new PortalPair();
        PortalPair darkGold = new PortalPair();
        PortalPair darkGray = new PortalPair();
        ArrayList<Location> whites = new ArrayList<>();
        ArrayList<Location> yellows = new ArrayList<>();
        ArrayList<Location> darkGolds = new ArrayList<>();
        ArrayList<Location> darkGreys = new ArrayList<>();
        for (int y = 0; y < grid.getHeight(); y++){
            for (int x = 0; x < grid.getWidth(); x++){
                tileChar = grid.getTile(x, y);
                location = new Location(x, y);
                if (tileChar == 'i'){
                    white.addPortal(new Item(ItemType.WHITE_PORTAL.getImage(), location));
                    whites.add(location);
                }
                else if (tileChar == 'j'){
                    yellow.addPortal(new Item(ItemType.YELLOW_PORTAL.getImage(), location));
                    yellows.add(location);
                }
                else if (tileChar == 'k'){
                    darkGold.addPortal(new Item(ItemType.DARK_GOLD_PORTAL.getImage(), location));
                    darkGolds.add(location);
                }
                else if (tileChar == 'l'){
                    darkGray.addPortal(new Item(ItemType.DARK_GRAY_PORTAL.getImage(), location));
                    darkGreys.add(location);
                }
            }
        }
        // only 0 or 2 are valid numbers for portal pairs
        if (!white.checkPortalTypeIsValid()){
            log = log + "Level " + file.getName() + " – portal White count is not 2: " +
                    locationListToString(whites) + "\n";
        }
        if (!yellow.checkPortalTypeIsValid()){
            log = log + "Level " + file.getName() + " – portal Yellow count is not 2: " +
                    locationListToString(yellows) + "\n";
        }
        if (!darkGold.checkPortalTypeIsValid()){
            log = log + "Level " + file.getName() + " – portal DarkGold count is not 2: " +
                    locationListToString(darkGolds) + "\n";
        }
        if (!darkGray.checkPortalTypeIsValid()){
            log = log + "Level " + file.getName() + " – portal DarkGrey count is not 2: " +
                    locationListToString(darkGreys) + "\n";
        }
        return log;
    }
}

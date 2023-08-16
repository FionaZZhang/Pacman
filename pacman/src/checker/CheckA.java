package src.checker;

import ch.aplu.jgamegrid.Location;
import src.grid.Grid;
import java.io.File;
import java.util.ArrayList;

public class CheckA extends LevelCheckComponent{
    /**
     * check whether there is exactly one starting point for pacman
     * if fails the check, return error log as a string
     */
    @Override
    public String checkLevel(File file, Grid grid) {
        int countPacMan = 0;
        ArrayList<Location> pacmans = new ArrayList<>();
        char tileChar;
        Location location;
        String log = "";
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                tileChar = grid.getTile(x, y);
                location = new Location(x, y);
                if (tileChar == 'f') {
                    countPacMan++;
                    pacmans.add(location);
                }
            }
        }
        // no pacman start point
        if (countPacMan == 0){
            log =  "Level " + file.getName() + " - no start for PacMan\n";
            return log;
        }
        // multiple pacman start points
        if (countPacMan > 1){
            log = "Level " + file.getName() + " - more than one start for Pacman: " + locationListToString(pacmans) + "\n";
            return log;
        }
        return log;
    }
}

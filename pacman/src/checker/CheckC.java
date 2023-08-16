package src.checker;

import ch.aplu.jgamegrid.Location;
import src.grid.Grid;
import java.io.File;
import java.util.ArrayList;

public class CheckC extends LevelCheckComponent{
    /**
     * check whether there are more than 2 golds and pills in total
     * if fails the check, return error log as a string
     */
    public String checkLevel(File file, Grid grid) {
        int countGold = 0;
        int countPill = 0;
        char tileChar;
        Location location;
        String log = "";
        ArrayList<Location> pills = new ArrayList<>();
        ArrayList<Location> golds = new ArrayList<>();
        for (int y = 0; y < grid.getHeight(); y++){
            for (int x = 0; x < grid.getWidth(); x++){
                tileChar = grid.getTile(x, y);
                location = new Location(x, y);
                if (tileChar == 'c') {
                    countPill++;
                    pills.add(location);
                }
                else if (tileChar == 'd'){
                    countGold++;
                    golds.add(location);
                }
            }
        }
        // number of pills and golds should exceed 2 in total
        if (countPill + countGold < 2){
            log = "Level " + file.getName() + " – less than 2 Gold and Pill\n";
        }
        return log;
    }
}

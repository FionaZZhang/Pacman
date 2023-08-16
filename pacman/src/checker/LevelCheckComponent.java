package src.checker;

import ch.aplu.jgamegrid.Location;
import src.grid.Grid;
import java.io.File;
import java.util.ArrayList;

public abstract class LevelCheckComponent {
    public abstract String checkLevel(File file, Grid grid);

    /**
     * used for printing log as locations should be displayed as "(x1, y1); (x2, y2); (x3, y3)"
     */
    public String locationListToString(ArrayList<Location> locations) {
        String log = "";
        for(Location l: locations) {
            // in order to match log coordinate, since our coordinate starts from (0,0), while coordinate in the
            // map editor starts from (1,1)
            l.x += 1;
            l.y += 1;
            // if l is the last element in locations, don't need to add "; " to the output string
            if (locations.indexOf(l) == locations.size() - 1) {
                log = log.concat(l.toString());
            }
            else {
                log = log.concat(l.toString()).concat("; ");
            }
        }
        return log;
    }
}

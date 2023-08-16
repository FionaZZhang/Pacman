package src.checker;

import ch.aplu.jgamegrid.Location;
import src.grid.Grid;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CheckD extends LevelCheckComponent{
    private final static int Horz = 20;
    private final static int Vert = 11;
    private final static int[] delta_x = {-1, 0, 1, 0};
    private final static int[] delta_y = {0, 1, 0, -1};
    private final static char[] portals = {'i', 'j', 'k', 'l'};
    /**
     * check whether all golds and pills are accessible
     * if fails the check, return error log as a string
     */
    public String checkLevel(File file, Grid grid) {
        char tileChar;
        Location pacLocation = null;
        String log = "";
        // if either checkA or checkB fails, unnecessary to check accessibility
        if (new CheckA().checkLevel(file, grid).length() != 0 || new CheckB().checkLevel(file, grid).length() != 0) {
            return log;
        }
        ArrayList<Location> pills = new ArrayList<>();
        ArrayList<Location> golds = new ArrayList<>();
        for (int y = 0; y < grid.getHeight(); y++){
            for (int x = 0; x < grid.getWidth(); x++){
                tileChar = grid.getTile(x, y);
                Location location = new Location(x, y);
                if (tileChar == 'f') {
                    pacLocation = location;
                }
                else if (tileChar == 'c') {
                    pills.add(location);
                }
                else if (tileChar == 'd'){
                    golds.add(location);
                }
            }
        }
        boolean allPillsAccessible = true;
        boolean allGoldsAccessible = true;
        ArrayList<Location> inaccessiblePills = new ArrayList<>();
        ArrayList<Location> inaccessibleGolds = new ArrayList<>();
        boolean[][] accessibleLocations = bfs(grid, pacLocation);
        // loop the item list to see whether every item is at a reachable location
        for (Location l : pills) {
            if (!accessibleLocations[l.x][l.y]) {
                allPillsAccessible = false;
                inaccessiblePills.add(l);
            }
        }
        for (Location l : golds) {
            if (!accessibleLocations[l.x][l.y]) {
                allGoldsAccessible = false;
                inaccessibleGolds.add(l);
            }
        }
        // write log and return
        if (!allPillsAccessible || !allGoldsAccessible) {
            if (!allPillsAccessible) {
                log = "Level " + file.getName() + " - Pill not accessible: " +
                        locationListToString(inaccessiblePills) + "\n";
            }
            if (!allGoldsAccessible) {
                log = log + "Level " + file.getName() + " - Gold not accessible: " +
                        locationListToString(inaccessibleGolds) + "\n";
            }
        }
        return log;
    }

    /**
     * given a start point and map, the breadth first search will return a 2 D boolean array indicating
     * all the reachable locations on the map from the start point
     */
    public boolean[][] bfs(Grid grid, Location start) {
        boolean[][] visited = new boolean[Horz][Vert];
        List<Location> reachableLocations = new ArrayList<>();
        Queue<Location> queue = new LinkedList<>();
        // mark the starting location as visited and add it to the queue
        visited[start.x][start.y] = true;
        queue.add(start);
        while (!queue.isEmpty()) {
            Location current = queue.poll();
            reachableLocations.add(current);
            // check four adjacent locations
            for (int i = 0; i < 4; i ++) {
                int newX = current.x + delta_x[i];
                int newY = current.y + delta_y[i];
                // skip if the new location is out of bounds or visited
                if ((newX < 0 || newX >= Horz) || (newY < 0 || newY >= Vert)) {
                    continue;
                }
                // skip if the new location is a wall
                char cell = grid.getTile(newX, newY);
                if (visited[newX][newY] || cell == 'b') {
                    continue;
                }
                visited[newX][newY] = true;
                queue.add(new Location(newX, newY));
                // if the new location is a portal, add its corresponding portal to the queue
                for (char c: portals) {
                    if (cell == c) {
                        for (int m = 0; m < Horz; m ++) {
                            for (int n = 0; n < Vert; n ++) {
                                if ((grid.getTile(m, n) == c) && !visited[m][n]) {
                                    queue.add(new Location(m, n));
                                }
                            }
                        }
                    }
                }
            }
        }
        return visited;
    }
}

package src.Pacman;

import ch.aplu.jgamegrid.Location;
import src.grid.Grid;
import src.Game;
import src.Items.Item;
import java.util.*;

public class DirectedApproach implements MoveStrategy {
    private final static int Horz = 20;
    private final static int Vert = 11;
    private final static int[] delta_x = {-1, 0, 1, 0};
    private final static int[] delta_y = {0, 1, 0, -1};
    private final static char[] portals = {'i', 'j', 'k', 'l'};
    private List<Item> items = new ArrayList<>();
    private Location target = null;
    private List<Location> path;

    public DirectedApproach(Game game) {
        items.addAll(game.getPills());
        items.addAll(game.getGoldPieces());
    }

    /**
     * this method is used to choose the location which is closest to a remaining item
     * will be used in auto move mode, to help pacman automatically choose next step
     */
    private Location closestPillLocation(PacActor pacman) {
        int currentDistance = 1000;
        Location currentLocation = null;
        int distanceToItem;
        Item closestItem = null;
        for (Item item : items) {
            distanceToItem = item.getLocation().getDistanceTo(pacman.getLocation());
            if (distanceToItem < currentDistance) {
                currentLocation = item.getLocation();
                currentDistance = distanceToItem;
                closestItem = item;
            }
        }
        items.remove(closestItem);
        return currentLocation;
    }

    /**
     * implements the move method in moveStrategy interface
     */
    public Location move(PacActor pacman, Grid grid) {
        // if there is no target, get one, and generate a path to the target using breadth first search
        if (target == null || path.size() == 0) {
            target = closestPillLocation(pacman);
            path = bfs(pacman.getLocation(), target, grid);
            path.remove(0); // remove the first element in the path arrayList because it is the start point
        }
        // next location will be the first location of the path arrayList
        Location next = path.remove(0);
        if (next.equals(target)) {
            target = null;
            path.clear();
        }
        // set direction
        Location.CompassDirection compassDir = pacman.getLocation().get4CompassDirectionTo(next);
        pacman.setDirection(compassDir);
        return next;
    }

    /**
     * Given the game grid, with a start point and target, breadth first search will return a path
     * the path is an ArrayList of locations indicating the sequence of going from start to target
     */
    public static List<Location> bfs(Location start, Location target, Grid grid) {
        // this 2 D boolean array is used to record visited locations
        boolean[][] visited = new boolean[Horz][Vert];
        visited[start.x][start.y] = true;
        Queue<Location> queue = new LinkedList<>();
        queue.add(start);
        // this hashMap is used to record the parent location of a new visited location
        Map<Location, Location> parentMap = new HashMap<>();
        parentMap.put(start, null);
        while (!queue.isEmpty()) {
            Location current = queue.poll();
            // reach the target, construct a path and return
            if (current.equals(target)) {
                return constructPath(parentMap, current);
            }
            // check four adjacent locations
            for (int i = 0; i < 4; i ++) {
                int newX = current.x + delta_x[i];
                int newY = current.y + delta_y[i];
                // skip if the new location is out of bounds
                if ((newX < 0 || newX >= Horz) || (newY < 0 || newY >= Vert)) {
                    continue;
                }
                // skip if the new location is visited or is a wall
                char cell = grid.getTile(newX, newY);
                if (visited[newX][newY] || cell == 'b') {
                    continue;
                }
                visited[newX][newY] = true;
                Location newLocation = new Location(newX, newY);
                queue.add(newLocation);
                boolean isPortal = false;
                // if the newLocation is a portal, add its corresponding portal to the queue
                for (char c: portals) {
                    if (cell == c) {
                        isPortal = true;
                        for (int m = 0; m < Horz; m ++) {
                            for (int n = 0; n < Vert; n ++) {
                                if ((grid.getTile(m, n) == c) && !visited[m][n]) {
                                    newLocation =new Location(m, n);
                                    queue.add(newLocation);
                                    parentMap.put(newLocation, current);
                                }
                            }
                        }
                    }
                }
                if (!isPortal) {
                    parentMap.put(newLocation, current);
                }
            }
        }
        return null;
    }

    /**
     * back track the target, keep adding its parent location to the array list until reaching the starting point
     */
    public static List<Location> constructPath(Map<Location, Location> parentMap, Location target) {
        ArrayList<Location> path = new ArrayList<>();
        Location current = target;
        while (current != null) {
            path.add(0, current);
            current = parentMap.get(current);
        }
        return path;
    }
}
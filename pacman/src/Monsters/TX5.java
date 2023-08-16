package src.Monsters;

import ch.aplu.jgamegrid.Location;
import src.Game;

public class TX5 extends Monster{
    public TX5(Game game) {
        super(game, MonsterType.TX5);
        stopMoving(5);
    }

    /**
     * TX5 determines the direction to pacActor and try to move in that direction. Otherwise, random walk.
     */
    public Location walkApproach() {
        Location pacLocation = game.getPacActor().getLocation();
        Location.CompassDirection compassDir = getLocation().get4CompassDirectionTo(pacLocation);
        Location next = getLocation().getNeighbourLocation(compassDir);
        setDirection(compassDir);
        // If the next location is a maze wall or has been visited, it will randomly walk
        if (!isVisited(next) && canMove(next)){
            return next;
        }
        else {
            return randomWalk();
        }
    }
}

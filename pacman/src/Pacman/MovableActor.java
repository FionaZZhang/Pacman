package src.Pacman;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import src.Game;
import src.Monsters.MonsterType;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public abstract class MovableActor extends Actor {
    protected static final int nbSprites = 4;
    protected Game game;
    protected ArrayList<Location> visitedList = new ArrayList<>();
    protected final int listLength = 10;
    protected int seed;
    protected Random randomiser = new Random();

    /**
     * constructor for pacActor
     */
    public MovableActor(Game game) {
        super(true, "sprites/pacpix.gif", nbSprites);
        this.game = game;
    }

    /**
     * constructor for monsters
     */
    public MovableActor(Game game, MonsterType type){
        super("sprites/" + type.getImageName());
        this.game = game;
    }

    /**
     * check whether a location is out of bound or is a wall, if so, that location is prohibited to step on
     */
    public boolean canMove(Location location) {
        if ( isWall(location)|| location.getX() >= game.getNumHorzCells()
                || location.getX() < 0 || location.getY() >= game.getNumVertCells() || location.getY() < 0)
            return false;
        else
            return true;
    }

    /**
     * check whether a location is a wall
     */
    public boolean isWall(Location location) {
        Color c = getBackground().getColor(location);
        if (c.equals(Color.gray)) {
            return true;
        } else {
            return false;
        }
    }

    public void setSeed(int seed) {
        this.seed = seed;
        randomiser.setSeed(seed);
    }

    /**
     * add a location to recently visited list, but the list will only keep records of ten recent visited locations
     */
    public void addVisitedList(Location location) {
        visitedList.add(location);
        if (visitedList.size() == listLength)
            visitedList.remove(0);
    }

    /**
     * check whether a location has been recently visited
     */
    public boolean isVisited(Location location) {
        for (Location loc : visitedList)
            if (loc.equals(location))
                return true;
        return false;
    }

    public abstract void act();
}

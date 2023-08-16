package src.Items;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

public class Item extends Actor {
    private final Location itemLocation;

    /**
     * this constructor is used to create gold pieces and ice cubes, as they have image files
     */
    public Item(String image, Location itemLocation){
        super(image);
        this.itemLocation = new Location(itemLocation);
    }

    /**
     * this constructor is usd to create pills since pills don't have an image file
     */
    public Item(Location itemLocation){
        this.itemLocation = new Location(itemLocation);
    }
}

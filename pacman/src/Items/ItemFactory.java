package src.Items;

import ch.aplu.jgamegrid.Location;

public class ItemFactory {
    private static ItemFactory instance;

    private ItemFactory(){
    }

    /**
     * singleton item factory global access point here
     */
    public static ItemFactory getInstance(){
        if (instance == null) {
            instance = new ItemFactory();
        }
        return instance;
    }

    /**
     * create new item according to the given type and location, then return
     */
    public Item createItem(char type, Location location) {
        return switch (type) {
            case 'c' -> new Item(location);
            case 'd' -> new Item(ItemType.GOLD_PIECE.getImage(), location);
            case 'e' -> new Item(ItemType.ICE_CUBE.getImage(), location);
            case 'i' -> new Item(ItemType.WHITE_PORTAL.getImage(), location);
            case 'j' -> new Item(ItemType.YELLOW_PORTAL.getImage(), location);
            case 'k' -> new Item(ItemType.DARK_GOLD_PORTAL.getImage(), location);
            case 'l' -> new Item(ItemType.DARK_GRAY_PORTAL.getImage(), location);
            default -> null;
        };
    }

    /**
     * Create new PortalPair object
     */
    public PortalPair createPortalPair(){
        return new PortalPair();
    }
}

package src.Items;

public enum ItemType {
    PILL,
    ICE_CUBE,
    GOLD_PIECE,
    WHITE_PORTAL,
    DARK_GOLD_PORTAL,
    DARK_GRAY_PORTAL,
    YELLOW_PORTAL;

    public int getScore(){
        return switch (this) {
            case PILL -> 1;
            case GOLD_PIECE -> 5;
            default -> 0;
        };
    }

    public String getImage(){
        return switch (this) {
            case GOLD_PIECE -> "pacman/sprites/data/d_goldTile.png";
            case ICE_CUBE -> "pacman/sprites/data/e_iceTile.png";
            case WHITE_PORTAL -> "pacman/sprites/data/i_portalWhiteTile.png";
            case YELLOW_PORTAL -> "pacman/sprites/data/j_portalYellowTile.png";
            case DARK_GOLD_PORTAL -> "pacman/sprites/data/k_portalDarkGoldTile.png";
            case DARK_GRAY_PORTAL -> "pacman/sprites/data/l_portalDarkGrayTile.png";
            default -> "";
        };
    }
}

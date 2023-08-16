package src.Pacman;

import ch.aplu.jgamegrid.*;
import src.grid.Grid;

/**
 * If any smarter walk approach is required in the future, just implements this move method.
 * The strategy pattern allows for easy extension.
 */
public interface MoveStrategy {
    public Location move(PacActor pacman, Grid grid);
}

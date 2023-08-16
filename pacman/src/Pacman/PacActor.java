// PacActor.java
// Used for PacMan
package src.Pacman;

import ch.aplu.jgamegrid.*;
import src.grid.Grid;
import src.Game;
import src.Items.ItemType;
import java.awt.event.KeyEvent;
import java.awt.Color;

public class PacActor extends MovableActor implements GGKeyRepeatListener {
  private int idSprite = 0;
  private int nbPills = 0;
  private int score = 0;
  private boolean isAuto;
  private Grid grid;
  private MoveStrategy moveStrategy;

  public PacActor(Game game, Grid grid, boolean isAuto) {
    super(game);
    this.grid = grid;
    this.isAuto = isAuto;
    if (isAuto) {
      // instantiate moveStrategy with the current required walk approach
      moveStrategy = new DirectedApproach(game);
    }
  }

  public void act() {
    show(idSprite);
    idSprite++;
    if (idSprite == nbSprites)
      idSprite = 0;
    if (isAuto) {
      Location next = moveStrategy.move(this, this.grid);
      setLocation(next);
      eatPill(next);
      addVisitedList(next);
      if ((next = game.moveActorThroughPortal(this)) != null){
        setLocation(next);
      }
      game.resetPortal();
    }
    this.game.getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
  }

  /**
   * this method is used for user keyboard input to control pacman when pacman is not in auto mode
   */
  public void keyRepeated(int keyCode) {
    Location pacNewLocation;
    if (isAuto) {
      return;
    }
    if (isRemoved())  // Already removed
      return;
    Location next = null;
    switch (keyCode) {
      case KeyEvent.VK_LEFT:
        next = getLocation().getNeighbourLocation(Location.WEST);
        setDirection(Location.WEST);
        break;
      case KeyEvent.VK_UP:
        next = getLocation().getNeighbourLocation(Location.NORTH);
        setDirection(Location.NORTH);
        break;
      case KeyEvent.VK_RIGHT:
        next = getLocation().getNeighbourLocation(Location.EAST);
        setDirection(Location.EAST);
        break;
      case KeyEvent.VK_DOWN:
        next = getLocation().getNeighbourLocation(Location.SOUTH);
        setDirection(Location.SOUTH);
        break;
    }
    if (next != null && canMove(next))
    {
      setLocation(next);
      eatPill(next);
      if ((pacNewLocation = game.moveActorThroughPortal(this)) != null){
        setLocation(pacNewLocation);
      }
      game.resetPortal();
    }
  }

  /**
   * check whether the current location pacman stands in has an item, if so, different items have different effects
   */
  private void eatPill(Location location) {
    Color c = getBackground().getColor(location);
    // If pill was eaten
    if (c.equals(Color.white)) {
      nbPills++;
      score += ItemType.PILL.getScore();
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "pills");
    }
    // If gold piece was eaten
    else if (c.equals(Color.yellow)) {
      nbPills++;
      score += ItemType.GOLD_PIECE.getScore();
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "gold");
      game.removeItem(ItemType.GOLD_PIECE,location);
    }
    // If ice cube was eaten
    else if (c.equals(Color.blue)) {
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "ice");
      game.removeItem(ItemType.ICE_CUBE,location);
    }
    String title = "[PacMan in the Torusverse] Current score: " + score;
    gameGrid.setTitle(title);
  }

  public void setAuto(boolean auto) {
    isAuto = auto;
  }

  public int getNbPills() {
    return nbPills;
  }
}

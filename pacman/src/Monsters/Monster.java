// Monster.java
// Used for PacMan
package src.Monsters;

import ch.aplu.jgamegrid.*;
import src.Game;
import src.Pacman.MovableActor;
import java.util.*;

public abstract class Monster extends MovableActor {
  protected MonsterType type;
  protected boolean stopMoving = false;

  public Monster(Game game, MonsterType type) {
    super(game, type);
    this.type = type;
  }

  public void act() {
    // if stopMoving is true, monsters stand still, this act() method will not reach the walk() method
    if (stopMoving) {
      return;
    }
    walk();
    if (getDirection() > 150 && getDirection() < 210) {
      setHorzMirror(false);
    }
    else {
      setHorzMirror(true);
    }
  }

  /**
   * Return the next move of the monster
   */
  public abstract Location walkApproach();

  /**
   * The monsters walk according to different states
   */
  public void walk() {
    Location next = walkApproach();
    setLocation(next);
    addVisitedList(next);
    if ((next = game.moveActorThroughPortal(this)) != null){
      setLocation(next);
    }
    game.resetPortal();

    game.getGameCallback().monsterLocationChanged(this);
  }


  /**
   * Randomly turn left or right, and turn back to the original direction and go forward or turn the other side if
   * hits maze wall
   */
  public Location randomWalk() {
    double oldDirection = getDirection();
    int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;
    setDirection(oldDirection);
    turn(sign * 90);  // Try to turn left/right
    Location next = getNextMoveLocation();
    if (canMove(next)) {
      return next;
    } else {
      setDirection(oldDirection);
      next = getNextMoveLocation();
      if (canMove(next)) { // Try to move forward{
        addVisitedList(next);
        return next;
      } else {
        setDirection(oldDirection);
        turn(-sign * 90);  // Try to turn right/left
        next = getNextMoveLocation();
        if (canMove(next)) {
          addVisitedList(next);
          return next;
        } else {
          setDirection(oldDirection);
          turn(180);  // Turn backward
          next = getNextMoveLocation();
          addVisitedList(next);
          return next;
        }
      }
    }
  }

  public void stopMoving(int seconds) {
    this.stopMoving = true;
    Timer timer = new Timer(); // Instantiate Timer Object
    int SECOND_TO_MILLISECONDS = 1000;
    final Monster monster = this;
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        monster.stopMoving = false;
      }
    }, seconds * SECOND_TO_MILLISECONDS);
  }

  public void setStopMoving(boolean stopMoving) {
    this.stopMoving = stopMoving;
  }

  public MonsterType getType() {
    return type;
  }
}
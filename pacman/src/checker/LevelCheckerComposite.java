package src.checker;

import src.grid.Grid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LevelCheckerComposite extends LevelCheckComponent{
    private static LevelCheckerComposite instance;
    private List<LevelCheckComponent> levelCheckers;

    private LevelCheckerComposite () {
        levelCheckers = new ArrayList<>();
        addLevelChecker(new CheckA());
        addLevelChecker(new CheckB());
        addLevelChecker(new CheckC());
        addLevelChecker(new CheckD());
    }

    /**
     * singleton level checker global access point
     */
    public static LevelCheckerComposite getInstance(){
        if (instance == null) {
            instance = new LevelCheckerComposite();
        }
        return instance;
    }

    /**
     * allow easy addition of new level check requirement
     */
    public void addLevelChecker(LevelCheckComponent levelChecker) {
        levelCheckers.add(levelChecker);
    }

    /**
     * allow deletion of current checker outside the class, though it has not been used so far
     */
    public void removeLevelChecker(LevelCheckComponent levelChecker) {
        levelCheckers.remove(levelChecker);
    }

    /**
     * work as a composite pattern, check all the checkers in composite
     */
    public String checkLevel(File file, Grid grid) {
        String log = "";
        for (LevelCheckComponent checker: levelCheckers) {
            log = log + checker.checkLevel(file, grid);
        }
        return log;
    }
}



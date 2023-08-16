// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.grid.Grid;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import src.Items.Item;
import src.Items.ItemFactory;
import src.Items.PortalPair;
import src.Monsters.Monster;
import src.Monsters.MonsterFactory;
import src.Pacman.PacActor;
import src.utility.PropertiesLoader;

/**
 * GameEngine reads the property file, initialises the game, and runs the game.
 */
public class GameEngine extends GameGrid {
    private final static int nbHorzCells = 20;
    private final static int nbVertCells = 11;
    private final static int cellSize = 32;
    private final static int SIMULATION_PERIOD = 100;
    private final static int KEY_PERIOD = 150;
    private final static String TITLE = "[PacMan in the Torusverse]";
    private final static boolean isNavigation = false;
    private int seed = 30006;
    private boolean isAuto;
    private Game game;
    private Controller controller;
    private GGBackground background;
    protected PacActor pacActor;
    private Grid grid;
    private final String[] monsterCharacters = {"g", "h"};
    private final String[] itemCharacters = {"c", "d", "e", "i", "j", "k", "l"};
    private ArrayList<Monster> monsters;
    private ArrayList<Item> pills;
    private ArrayList<Item> goldPieces;
    private ArrayList<Item> iceCubes;
    private PortalPair whitePortals;
    private PortalPair yellowPortals;
    private PortalPair darkGoldPortals;
    private PortalPair darkGrayPortals;
    private Properties properties;
    private final int SPEED_DOWN = 3;

    public GameEngine(String propertiesPath, String gameMapPath) {
        // Setup game engine
        super(nbHorzCells, nbVertCells, cellSize, isNavigation);
        this.properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        controller = new Controller(gameMapPath);
        grid = controller.getGrid();
        if (grid != null) {
            seed = Integer.parseInt(properties.getProperty("seed"));
            isAuto = Boolean.parseBoolean(properties.getProperty("PacMan.isAuto"));
            setSimulationPeriod(SIMULATION_PERIOD);
            setTitle(TITLE);
            game = new Game(nbHorzCells, nbVertCells);
            setUpAll();
            GGBackground bg = getBg();
            this.background = bg;
            drawGrid();
            runGame();
        }
    }

    private void setupPacActorAttributes() {
        //Setup for auto test
        pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
        //Setup Random seeds
        pacActor.setSeed(seed);
        addKeyRepeatListener(pacActor);
        setKeyRepeatPeriod(KEY_PERIOD);
        pacActor.setSlowDown(SPEED_DOWN);
    }

    private void setupMonsterAttributes() {
        // loop the monsters ArrayList to set seed and slowdown
        for(Monster monster: monsters){
            monster.setSeed(seed);
            monster.setSlowDown(SPEED_DOWN);
        }
    }

    private void setupActorLocations() {
        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                Location location = new Location(x, y);
                char type = grid.getTile(x, y);
                if (type == 'f') {
                    addActor(pacActor, location);
                }
                // use monster factory to create troll and tx5, and any other monsters if more are coming in the future
                if (Arrays.asList(monsterCharacters).contains(Character.toString(type))) {
                    Monster newMonster = MonsterFactory.getInstance().createMonster(type, game);
                    addActor(newMonster, location, Location.NORTH);
                    monsters.add(newMonster);
                }
            }
        }
    }

    private void setupPillAndItemsLocations() {
        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                Location location = new Location(x, y);
                char type = grid.getTile(x, y);
                // use item factory to create pills, golds, ice cubes, and portals
                if (Arrays.asList(itemCharacters).contains(Character.toString(type))) {
                    Item item = ItemFactory.getInstance().createItem(type, location);
                    addActor(item, location);
                    switch (type) {
                        case 'c' -> pills.add(item);
                        case 'd' -> goldPieces.add(item);
                        case 'e' -> iceCubes.add(item);
                        case 'i' -> whitePortals.addPortal(item);
                        case 'j' -> yellowPortals.addPortal(item);
                        case 'k' -> darkGoldPortals.addPortal(item);
                        case 'l' -> darkGrayPortals.addPortal(item);
                    }
                }
            }
        }
    }

    private void runGame() {
        GGBackground bg = this.background;
        doRun();
        show();
        do {
            delay(10);
        } while(!game.checkWin() && !game.checkLost());
        delay(120);

        Location loc = pacActor.getLocation();
        for(Monster monster: monsters){
            monster.setStopMoving(true);
        }
        pacActor.removeSelf();
        String title = " ";
        if (game.isLost()) {
            bg.setPaintColor(Color.red);
            title = "GAME OVER";
            setTitle(title);
            addActor(new Actor("sprites/explosion3.gif"), loc);
            setTitle(title);
            game.getGameCallback().endOfGame(title);
            controller.edit();
        } else if (game.isWin()) {
            File nextFile = controller.nextLevel();
            if (nextFile != null) {
                grid = controller.getGrid();
                if (grid != null){
                    removeAllActors();
                    this.game = new Game(nbHorzCells, nbVertCells);
                    setUpAll();
                    this.background = getBg();
                    drawGrid();
                    runGame();
                } else {
                    bg.setPaintColor(Color.red);
                    title = "ERROR MAP";
                    setTitle(title);
                    controller.editCurrMap(nextFile);
                    game.getGameCallback().endOfGame(title);
                    this.getFrame().dispose();
                }
            } else{
                bg.setPaintColor(Color.yellow);
                title = "YOU WIN";
                setTitle(title);
                controller.edit();
                setTitle(title);
                game.getGameCallback().endOfGame(title);
            }
        }
        doPause();
    }

    public void drawGrid() {
        GGBackground bg = this.background;
        Location location;
        int cellValue;
        bg.clear(Color.gray);
        bg.setPaintColor(Color.white);
        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                bg.setPaintColor(Color.white);
                location = new Location(x, y);
                cellValue = grid.getTile(x, y);
                bg.fillCell(location, Color.gray);
                if (cellValue != 'b') {
                    bg.fillCell(location, Color.lightGray);
                }
            }
        }
        for (Item item: pills) {
            bg.fillCircle(toPoint(item.getLocation()), 10);
        }
        for (Item item : goldPieces) {
            bg.setPaintColor(Color.yellow);
            bg.fillCircle(toPoint(item.getLocation()), 10);
        }
        for (Item item: iceCubes) {
            bg.setPaintColor(Color.blue);
            bg.fillCircle(toPoint(item.getLocation()), 5);
        }
    }

    /**
     * used to set up all actors and map entities
     */
    public void setUpAll() {
        pills = new ArrayList<>();
        goldPieces = new ArrayList<>();
        iceCubes = new ArrayList<>();
        whitePortals = ItemFactory.getInstance().createPortalPair();
        yellowPortals = ItemFactory.getInstance().createPortalPair();
        darkGoldPortals = ItemFactory.getInstance().createPortalPair();
        darkGrayPortals = ItemFactory.getInstance().createPortalPair();
        setupPillAndItemsLocations();
        game.addItems(pills, goldPieces, iceCubes, whitePortals, yellowPortals, darkGrayPortals, darkGoldPortals);
        pacActor = new PacActor(game, grid, isAuto);
        setupPacActorAttributes();
        monsters = new ArrayList<>();
        setupActorLocations();
        setupMonsterAttributes();
        game.addPacMan(pacActor);
        game.addMonsters(monsters);
    }
}

package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import src.editor.*;
import src.checker.LevelCheckerComposite;
import src.grid.Camera;
import src.grid.Grid;
import src.grid.GridCamera;
import src.grid.GridModel;
import src.grid.GridView;


/**
 * Controller of the application.
 *
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 *
 */
public class Controller implements ActionListener, GUIInformation {
	/**
	 * The model of the map editor.
	 */
	private Grid model = null;
	private Grid gameGrid = null;
	private Tile selectedTile = null;
	private Camera camera = null;
	private List<Tile> tiles;
	private GridView grid = null;
	private View view = null;
	private int gridWith = Constants.MAP_WIDTH;
	private int gridHeight = Constants.MAP_HEIGHT;
	private boolean isTest = false;
	private ArrayList<File> sortedFile = new ArrayList<>();
	private FileHandler fileHandler;

	/**
	 * Construct the controller.
	 */
	public Controller(String arg) {

		fileHandler = new FileHandler();

		if (arg != null) {
			File entry = new File(arg);
			if (entry.isDirectory()) {
				isTest = true;
				this.tiles = TileManager.getTilesFromFolder("pacman/sprites/data/");
				this.model = new GridModel(Constants.MAP_WIDTH, Constants.MAP_HEIGHT, tiles.get(0).getCharacter());
				this.camera = new GridCamera(model, Constants.GRID_WIDTH, Constants.GRID_HEIGHT);
				this.grid = new GridView(this, camera, tiles);
			} else {
				init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
			}
			processEntry(entry);
		}
	}

	/**
	 * Set up a window.
	 */
	public void init(int width, int height) {
		this.tiles = TileManager.getTilesFromFolder("pacman/sprites/data/");
		this.model = new GridModel(width, height, tiles.get(0).getCharacter());
		this.camera = new GridCamera(model, Constants.GRID_WIDTH,
				Constants.GRID_HEIGHT);
		this.grid = new GridView(this, camera, tiles);
		this.view = new View(this, camera, grid, tiles);
	}

	public Grid getGrid() {
		return this.gameGrid;
	}

	/**
	 * Different commands that comes from the view.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		for (Tile t : tiles) {
			if (e.getActionCommand().equals(Character.toString(t.getCharacter()))) {
				selectedTile = t;
				break;
			}
		}
		switch (e.getActionCommand()) {
			case "flipGrid":
				break;
			case "save":
				File savedFile = fileHandler.saveFile(model);
				model = fileHandler.loadFile(savedFile.getPath(), model);
				grid.redrawGrid();
				String log = LevelCheckerComposite.getInstance().checkLevel(savedFile, model);
				if (!log.isEmpty()) {
					fileHandler.writeErrorLog(savedFile.getName(), log);
				}
				break;
			case "load":
				File selectedFile = fileHandler.selectFile(model);
				if (selectedFile != null) {
					processEntry(selectedFile);
				}
				break;
			case "update":
				updateGrid(gridWith, gridHeight);
				break;
		}
	}

	/**
	 * Logic for processing a map or a folder.
	 */
	private void processEntry(File entry) {
		if (entry.isFile()) {
			loadFileAndLogErrors(entry);
		} else if (entry.isDirectory()) {
			sortedFile = fileHandler.processFolder(entry.getPath());
			if (sortedFile != null && !sortedFile.isEmpty()) {
				loadFileAndLogErrors(sortedFile.get(0));
			} else {
				edit();
			}
		}
	}

	/**
	 * Updates the model and perform level check.
	 */
	private void loadFileAndLogErrors(File file) {
		model = fileHandler.loadFile(file.getPath(), model);
		grid.redrawGrid();
		String log = LevelCheckerComposite.getInstance().checkLevel(file, model);
		if (!log.isEmpty()) {
			fileHandler.writeErrorLog(file.getName(), log);
			gameGrid = null;
		} else if (isTest){
			gameGrid = model;
		}
	}

	/**
	 * Get the next map.
	 */
	public File nextLevel() {
		File nextFile = fileHandler.loadNextFile();
		if (nextFile != null) {
			processEntry(nextFile);
			return nextFile;
		} else {
			gameGrid = null;
			return null;
		}
	}

	/**
	 * Edit mode with no current map.
	 */
	public void edit() {
		if (view != null) {
			view.close();
		}
		init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
	}

	/**
	 * Edit mode on a map.
	 */
	public void editCurrMap(File editMap) {
		if (view != null) {
			view.close();
		}
		init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
		model = fileHandler.loadFile(editMap.getPath(), model);
		grid.redrawGrid();
	}


	public void updateGrid(int width, int height) {
		if (view != null) {
			view.close();
		}
		init(width, height);
		view.setSize(width, height);
	}

	public DocumentListener updateSizeFields = new DocumentListener() {
		public void changedUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}
		public void removeUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}
		public void insertUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tile getSelectedTile() {
		return selectedTile;
	}
}
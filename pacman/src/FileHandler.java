package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import src.checker.GameChecker;
import src.grid.Grid;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class FileHandler{

    private final String ERROR_STRING = "_ErrorMapLog.txt";
    private ArrayList<File> sortedFile = new ArrayList<>();
    public File currFile = null;
    public FileWriter fileWriter = null;

    public FileHandler() {
    }

    public void writeErrorLog(String fileName, String log) {
        fileName = fileName + ERROR_STRING;
        try {
            fileWriter = new FileWriter(new File(fileName));
            writeString(log);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Save the current map.
     */
    public File saveFile(Grid model) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "xml files", "xml");
        chooser.setFileFilter(filter);
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);
        int returnVal = chooser.showSaveDialog(null);
        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                Element level = new Element("level");
                Document doc = new Document(level);
                doc.setRootElement(level);
                Element size = new Element("size");
                int height = model.getHeight();
                int width = model.getWidth();
                size.addContent(new Element("width").setText(width + ""));
                size.addContent(new Element("height").setText(height + ""));
                doc.getRootElement().addContent(size);
                for (int y = 0; y < height; y++) {
                    Element row = new Element("row");
                    for (int x = 0; x < width; x++) {
                        char tileChar = model.getTile(x,y);
                        String type = "PathTile";
                        switch (tileChar) {
                            case 'b' -> type = "WallTile";
                            case 'c' -> type = "PillTile";
                            case 'd' -> type = "GoldTile";
                            case 'e' -> type = "IceTile";
                            case 'f' -> type = "PacTile";
                            case 'g' -> type = "TrollTile";
                            case 'h' -> type = "TX5Tile";
                            case 'i' -> type = "PortalWhiteTile";
                            case 'j' -> type = "PortalYellowTile";
                            case 'k' -> type = "PortalDarkGoldTile";
                            case 'l' -> type = "PortalDarkGrayTile";
                        }
                        Element e = new Element("cell");
                        row.addContent(e.setText(type));
                    }
                    doc.getRootElement().addContent(row);
                }
                XMLOutputter xmlOutput = new XMLOutputter();
                xmlOutput.setFormat(Format.getPrettyFormat());
                xmlOutput
                        .output(doc, new FileWriter(chooser.getSelectedFile()));
            }
        } catch (FileNotFoundException e1) {
            JOptionPane.showMessageDialog(null, "Invalid file!", "error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(chooser.getSelectedFile().getPath());
    }

    /**
     * User GUI for selecting a file. Returns the file or directory selected.
     */
    public File selectFile(Grid model) {
        File path = null;
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            File workingDirectory = new File(System.getProperty("user.dir"));
            chooser.setCurrentDirectory(workingDirectory);

            int returnVal = chooser.showOpenDialog(null);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return null;
            }
            path = chooser.getSelectedFile();
            if (path.isFile()){
                loadFile(path.getPath(), model);
            } else if (path.isDirectory()){
                processFolder(path.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * Load a file and returns the grid.
     */
    public Grid loadFile(String pathStr, Grid model) {
        File mPath;
        try {
            mPath = new File(pathStr);
            currFile = mPath;
            Grid newModel = processFile(currFile, model);
            return newModel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return a grid of the next file in the sorted file list.
     */
    public File loadNextFile() {
        int i = sortedFile.indexOf(currFile);
        if ((i + 1) < sortedFile.size()) {
            return sortedFile.get(i + 1);
        } else {
            return null;
        }
    }


    /**
     * Return a sorted arraylist of files that meets the requirement.
     */
    public ArrayList<File> processFolder(String folderPath){
        File folder = new File(folderPath);
        ArrayList<File> mapFiles = GameChecker.getInstance().checkGame(folder);
        if (mapFiles == null){
            return null;
        }
        mapFiles.sort((file1, file2) -> {
            int num1 = Integer.parseInt(file1.getName().replaceAll("\\D+", ""));
            int num2 = Integer.parseInt(file2.getName().replaceAll("\\D+", ""));
            return Integer.compare(num1, num2);
        });
        sortedFile.addAll(mapFiles);
        return sortedFile;
    }

    /**
     * Return a grid from a selected file.
     */
    private Grid processFile(File selectedFile, Grid model){
        SAXBuilder builder = new SAXBuilder();
        Document document;
        try {
            if (selectedFile.canRead() && selectedFile.exists()) {
                document = (Document) builder.build(selectedFile);
                Element rootNode = document.getRootElement();
                List sizeList = rootNode.getChildren("size");
                Element sizeElem = (Element) sizeList.get(0);
                int height = Integer.parseInt(sizeElem
                        .getChildText("height"));
                int width = Integer
                        .parseInt(sizeElem.getChildText("width"));
                List rows = rootNode.getChildren("row");
                for (int y = 0; y < rows.size(); y++) {
                    Element cellsElem = (Element) rows.get(y);
                    List cells = cellsElem.getChildren("cell");
                    for (int x = 0; x < cells.size(); x++) {
                        Element cell = (Element) cells.get(x);
                        String cellValue = cell.getText();
                        char tileNr = '0';
                        switch (cellValue) {
                            case "PathTile" -> tileNr = 'a';
                            case "WallTile" -> tileNr = 'b';
                            case "PillTile" -> tileNr = 'c';
                            case "GoldTile" -> tileNr = 'd';
                            case "IceTile" -> tileNr = 'e';
                            case "PacTile" -> tileNr = 'f';
                            case "TrollTile" -> tileNr = 'g';
                            case "TX5Tile" -> tileNr = 'h';
                            case "PortalWhiteTile" -> tileNr = 'i';
                            case "PortalYellowTile" -> tileNr = 'j';
                            case "PortalDarkGoldTile" -> tileNr = 'k';
                            case "PortalDarkGrayTile" -> tileNr = 'l';
                        }
                        model.setTile(x, y, tileNr);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    public void writeString(String str) {
        try {
            fileWriter.write(str);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

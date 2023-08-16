package src.checker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class GameChecker {
    private static GameChecker instance;
    FileWriter fileWriter = null;
    private GameChecker() {
    }

    /**
     * singleton game checker global access point
     */
    public static GameChecker getInstance(){
        if (instance == null) {
            instance = new GameChecker();
        }
        return instance;
    }

    /**
     * Function to check if folder is valid (section 2.2.4). Checks following:
     * 1. at least one correctly named map file in the folder
     * 2. the sequence of map files well-defined, where only one map file named with a particular number.
     */
    public ArrayList<File> checkGame(File folder){
        ArrayList<File> mapFiles = new ArrayList<>();
        File[] files = folder.listFiles();
        HashMap<Integer, ArrayList<String>> nameHashMap = new HashMap<>();
        int mapNum, countValidFiles = 0;
        boolean startsWithUniqueNumbers = true;
        // Folder must contain contents to be valid
        if (files != null){
            for (int i = 0; i < files.length; i++){
                // We only use folder contents which are files
                if (files[i].isFile()){
                    String startNum = getStartNum(files[i].getName());
                    if (!startNum.isEmpty()) {
                        mapNum = Integer.parseInt(startNum);
                        // If a particular number has not been used for a map file name
                        if (!nameHashMap.containsKey(mapNum)) {
                            ArrayList<String> names = new ArrayList<>();
                            names.add(files[i].getName());
                            nameHashMap.put(mapNum, names);
                            // Add any valid map files to our arraylist
                            mapFiles.add(files[i]);
                        }
                        else {
                            nameHashMap.get(mapNum).add(files[i].getName());
                        }
                        countValidFiles++;
                    }
                }
            }
        }
        if (countValidFiles == 0){
            try {
                fileWriter = new FileWriter(new File("GameCheckErrorLog.txt"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            writeString("GameFolder " + folder.getName() + " – no maps found");
        }
        for (Integer key: nameHashMap.keySet()){
            if (nameHashMap.get(key).size() > 1){
                startsWithUniqueNumbers = false;
                try {
                    fileWriter = new FileWriter(new File("GameCheckErrorLog.txt"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                String conflictFiles = nameHashMap.get(key).stream().collect(Collectors.joining("; "));
                writeString("GameFolder " + folder.getName() + " – multiple maps at same level: " + conflictFiles);
            }
        }
        boolean status = startsWithUniqueNumbers && countValidFiles >= 1;
        if (!status) {
            return null;
        }
        return mapFiles;
    }

    /**
     * get the beginning numbers of a filename, return as a string
     */
    public String getStartNum(String filename) {
        String startNum = "";
        char c;
        for (int i = 0; i < filename.length(); i ++) {
            c = filename.charAt(i);
            if (!Character.isDigit(c)) {
                return startNum;
            }
            startNum = startNum + c;
        }
        return startNum;
    }

    public void writeString(String str) {
        try {
            fileWriter.write(str);
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

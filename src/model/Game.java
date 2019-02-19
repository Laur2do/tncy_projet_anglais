package model;

import model.loader.DataLoader;
import model.loader.InvalidGridFileException;
import model.loader.InvalidWordFileException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    private static Game instance;
    private ArrayList<Grid> listOfGrids;
    private HashMap<String, Word> words;

    private Game() {
        this.listOfGrids = new ArrayList<>();
        this.words = new HashMap<>();

    }

    public static Game getInstance(){
        if (instance == null){
            instance = new Game();
        }
        return instance;
    }

    public ArrayList<Grid> getListOfGrids() {
        return listOfGrids;
    }

    public void addGrid(Grid grid){
        this.listOfGrids.add(grid);
    }

   public void addWord(Word word){
        this.words.put(word.getContent(), word);
   }

    public HashMap<String, Word> getWords() {
        return words;
    }

    public int loadWords(String filePath) throws InvalidWordFileException {
        return DataLoader.loadCSVFile(filePath);
    }

    public int loadGrids(String folderPath) throws InvalidGridFileException, IOException {
        File folder = new File(folderPath);
        if ( ! folder.isDirectory()) {
            throw new NotDirectoryException(folderPath);
        }
        File[] gridFiles = folder.listFiles((File directory, String fileName) -> fileName.endsWith(".csv"));
        int gridsLoaded = 0;
        for( File gridFile : gridFiles) {
            DataLoader.loadGridFile(gridFile.getCanonicalPath());
            gridsLoaded++;
        }
        return gridsLoaded;
    }


}

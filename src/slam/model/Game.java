package slam.model;

import slam.model.loader.DataLoader;
import slam.model.loader.InvalidGridFileException;
import slam.model.loader.InvalidQuestionFileException;
import slam.model.loader.InvalidWordFileException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import static slam.Main.printDebugLn;

public class Game extends Observable {

    private static Game instance;
    private final ArrayList<Grid> listOfGrids;
    private final HashMap<String, HashMap<String, Word>> words;
    private final HashMap<Character, ArrayList<Question>> questions;

    private Grid currentGrid;

    private Game() {
        this.listOfGrids = new ArrayList<>();
        this.words = new HashMap<>();
        this.questions = new HashMap<>();
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            this.questions.put(letter, new ArrayList<>());
        }

    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public ArrayList<Grid> getListOfGrids() {
        return listOfGrids;
    }

    public void addGrid(Grid grid) {
        this.listOfGrids.add(grid);
    }

    public void addWord(String type, Word word) {
        if (this.words.containsKey(word.getContent())) {
            System.err.println("Warning: adding word entry which is already present. Overriding previous entry.");
            System.err.println("\t new: " + word.getDescription());
            System.err.println("\t old: " + this.words.get(word.getContent()));
        }
        if( !  this.words.containsKey(type)) {
            this.words.put(type, new HashMap<>());
        }
        this.words.get(type).put(word.getContent(), word);
    }


    public void addQuestion(Question question) {
        questions.get(question.getLetter()).add(question);
    }

    public HashMap<String, Word> getWords() {
        HashMap<String, Word> allWords = new HashMap<>();
        for(HashMap<String, Word> words : this.words.values()) {
            allWords.putAll(words);
        }
        return allWords;
    }

    public HashMap<String, Word> getWords(String type) {
        return words.get(type);
    }

    public int loadWords(String filePath) throws InvalidWordFileException {
        return DataLoader.loadWordFile(filePath);
    }

    public int loadGrids(String folderPath) throws InvalidGridFileException, IOException {
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            throw new NotDirectoryException(folderPath);
        }
        File[] gridFiles = folder.listFiles((File directory, String fileName) -> fileName.endsWith(".csv"));
        if (gridFiles == null) {
            throw new FileNotFoundException("Cant find any CSV files in " + folderPath);
        }
        int gridsLoaded = 0;
        for (File gridFile : gridFiles) {
            DataLoader.loadGridFile(gridFile.getCanonicalPath());
            gridsLoaded++;
        }
        return gridsLoaded;
    }


    public Grid randomChangeCurrentGrid() {
        if( this.listOfGrids.isEmpty() ) {
            return null;
        }
        printDebugLn("Loading new grid");
        int index = (int) (Math.random() * this.listOfGrids.size());
        this.currentGrid = this.listOfGrids.get(index);
        setChanged();
        notifyObservers();
        return currentGrid;
    }

    public void generateRandomCurrentGrid() {
        printDebugLn("Computing new grid");
        this.currentGrid = new Grid();
        this.currentGrid.compute();
        setChanged();
        notifyObservers();
    }

    public void reset() {
        this.currentGrid.reset();
        setChanged();
        notifyObservers();
    }

    public Grid getCurrentGrid() {
        return this.currentGrid;
    }

    public int loadQuestions(String filePath) throws InvalidQuestionFileException {
        return DataLoader.loadQuestionFile(filePath);
    }

    public Question getRandomQuestionForLetter(char letter) {
        ArrayList<Question> letterQuestions = questions.get(letter);
        if (letterQuestions.size() == 0) {
            if( ! this.currentGrid.isRevealed()) {
                // Incoherent state: no question found but grid is not revealed
                System.err.println("No question found for letter "+letter);
                System.err.println(this.currentGrid);
            }
            return null;
        }
        int index = (int) (Math.random() * letterQuestions.size());
        return letterQuestions.get(index);
    }

    public static boolean validLetter(String s) {
        return s.length() == 1 && validLetter(s.charAt(0));
    }

    public static boolean validLetter(char c) {
        c = Character.toUpperCase(c);
        return c >= 'A' && c <= 'Z';
    }


}

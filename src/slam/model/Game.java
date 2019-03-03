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

public class Game {

    private static Game instance;
    private ArrayList<Grid> listOfGrids;
    private HashMap<String, Word> words;
    private HashMap<Character, ArrayList<Question>> questions;

    private Game() {
        this.listOfGrids = new ArrayList<>();
        this.words = new HashMap<>();
        this.questions = new HashMap<>();
        for(char letter = 'A'; letter <= 'Z'; letter++) {
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

    public void addWord(Word word) {
        this.words.put(word.getContent(), word);
    }

    public void addQuestion(Question question) {
        questions.get(question.getLetter()).add(question);
    }

    public HashMap<String, Word> getWords() {
        return words;
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
            throw new FileNotFoundException("Cant find any CSV files in "+folderPath);
        }
        int gridsLoaded = 0;
        for (File gridFile : gridFiles) {
            DataLoader.loadGridFile(gridFile.getCanonicalPath());
            gridsLoaded++;
        }
        return gridsLoaded;
    }


    public Grid getRandomGrid() {
        int index = (int)(Math.random()*this.listOfGrids.size());
        return this.listOfGrids.get(index);
    }

    public int loadQuestions(String filePath) throws InvalidQuestionFileException {
        return DataLoader.loadQuestionFile(filePath);
    }


    public Question getRandomQuestionForLetter(char letter) {
        ArrayList<Question> letterQuestions = questions.get(letter);
        if( letterQuestions.size() == 0 ) {
            return null;
        }
        int index = (int)(Math.random()*letterQuestions.size());
        return letterQuestions.get(index);
    }

    public static boolean validLetter(String s) {
        return s.length() == 1 && validLetter(s.charAt(0));
    }

    public static boolean validLetter(char  c) {
        c = Character.toUpperCase(c);
        return c >= 'A' && c <= 'Z';
    }


}

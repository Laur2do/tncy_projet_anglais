package slam.model;

import javafx.application.Platform;
import slam.model.loader.DataLoader;
import slam.model.loader.InvalidQuestionFileException;
import slam.model.loader.InvalidWordFileException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;

import static slam.Main.printDebugLn;

public class Game extends Observable {

    private static Game instance;
    private final HashMap<String, HashMap<String, Word>> words;
    private final HashMap<Character, ArrayList<Question>> questions;

    private Grid currentGrid;

    private boolean canGuessWord;

    private Game() {
        this.words = new HashMap<>();
        this.questions = new HashMap<>();
        init();
    }

    private void init() {
        words.clear();
        questions.clear();
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

    public boolean canGuessWord() {
        return canGuessWord;
    }

    public void setCanGuessWord(boolean canGuessWord) {
        this.canGuessWord = canGuessWord;
    }

    public boolean canStart() {
        int questionCount = 0;
        for (ArrayList<Question> questionsDesk : this.questions.values()) {
            if (questionsDesk.size() == 0) {
                return false;
            }
            questionCount += questionsDesk.size();
        }

        int wordCount = 0;
        for (HashMap<String, Word> wordsDeck : this.words.values()) {
            wordCount += wordsDeck.size();
        }

        return wordCount >= Grid.MIN_WORDS_COUNT && questionCount > 0;
    }

    public Collection<String> getWordDecks() {
        return this.words.keySet();
    }

    public void addWord(String type, Word word) {
        if (this.words.containsKey(word.getContent())) {
            System.err.println("Warning: adding word entry which is already present. Overriding previous entry.");
            System.err.println("\t new: " + word.getDescription());
            System.err.println("\t old: " + this.words.get(word.getContent()));
        }
        if (!this.words.containsKey(type)) {
            this.words.put(type, new HashMap<>());
        }
        this.words.get(type).put(word.getContent(), word);
    }


    public void addQuestion(Question question) {
        questions.get(question.getLetter()).add(question);
    }

    public HashMap<String, Word> getWords() {
        HashMap<String, Word> allWords = new HashMap<>();
        for (HashMap<String, Word> words : this.words.values()) {
            allWords.putAll(words);
        }
        return allWords;
    }

    public int loadWords(String filePath) throws InvalidWordFileException {
        return DataLoader.loadWordFile(filePath, false);
    }

    public void generateRandomCurrentGrid() {
        printDebugLn("Computing new grid");
        this.currentGrid = new Grid();
        this.currentGrid.compute();
        canGuessWord = false;
        Platform.runLater(this::notifyObservers);
    }

    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }

    public void resetGrid() {
        if (this.currentGrid != null) {
            this.currentGrid.reset();
            canGuessWord = false;
            notifyObservers();
        }
    }

    public void resetGame() {
        this.init();
        this.currentGrid = null;
        this.canGuessWord = false;
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
            if (!this.currentGrid.isRevealed()) {
                // Incoherent state: no question found but grid is not revealed
                System.err.println("No question found for letter " + letter);
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

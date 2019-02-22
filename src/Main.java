import model.*;
import model.loader.InvalidGridFileException;
import model.loader.InvalidQuestionFileException;
import model.loader.InvalidWordFileException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;


public class Main {

    /*@Override
    public void start(Stage primaryStage) throws Exception {
        /*Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }*/

    public final static boolean DEBUG = false;


    public static void main(String[] args) {
        // launch(args);
        Game game = Game.getInstance();
        try {
            int loadedWords = game.loadWords("data/words.csv");
            System.out.println("Loaded " + loadedWords + " words");
            if (DEBUG) {
                HashMap<String, Word> words = game.getWords();
                for (Word w : words.values()) {
                    System.out.println(w.toString());
                }
            }

            int gridLoaded = game.loadGrids("data/grids");

            System.out.println("Loaded " + gridLoaded + " grids");

            int questionsLoaded = game.loadQuestions("data/questions.csv");

            System.out.println("Loaded " + questionsLoaded + " questions");

            Grid grid = game.getRandomGrid();
            System.out.println(grid.shortInfo());
            System.out.println(grid.toString());

            Scanner scanner = new Scanner(System.in);
            while (!grid.isRevealed()) {
                Question q = grid.getRandomQuestionForRemainingLetters();

                System.out.print("Here is the question: \t" + q + "\n> ");
                String answer = scanner.next();
                while (!Game.validLetter(answer)) {
                    System.err.println("Please enter a valid letter");
                    System.out.print("Here is the question: \t" + q + "\n> ");
                    answer = scanner.next();
                }
                if (!q.validate(answer)) {
                    System.out.println("Wrong answer!");
                    System.out.println("Correct answer was: "+q.getLetter());
                    if(q.getExplanation() != null ) {
                        System.out.println("\tBecause "+q.getExplanation());
                    }
                    continue;
                }

                // We have a correct answer
                System.out.println("Correct!");
                if(q.getExplanation() != null ) {
                    System.out.println("\tBecause " + q.getExplanation());
                }

                char c = Character.toUpperCase(answer.charAt(0));
                grid.revealLetter(c);
                System.out.println(grid.toString());
            }

            System.out.println("Congratulations!");
        } catch (InvalidWordFileException | InvalidGridFileException | InvalidQuestionFileException | IOException e) {
            e.printStackTrace();
        }
    }

}


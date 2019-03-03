package slam;

import slam.model.*;
import slam.model.loader.InvalidGridFileException;
import slam.model.loader.InvalidQuestionFileException;
import slam.model.loader.InvalidWordFileException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class MainCLI {

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

            Scanner scanner = new Scanner(System.in);
            while (!grid.isRevealed()) {
                System.out.println(grid);
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
                System.out.println(grid);

                // We can now try to reveal a word
                ArrayList<GridWord> remainingWords = grid.getNonRevealedWords();
                System.out.println("You can try to guess a complete word. Please choose among:");
                for(int i = 0; i < remainingWords.size(); i++) {
                    System.out.println("\t"+i+ ": "+remainingWords.get(i));
                }
                System.out.print("\n> ");
                int wordIndex = scanner.nextInt();
                while (wordIndex < 0 || wordIndex >= remainingWords.size()) {
                    System.err.println("Please enter a correct word number");
                    System.out.println("You can try to guess a complete word. Please choose among:");
                    for(int i = 0; i < remainingWords.size(); i++) {
                        System.out.println("\t"+i+ ": "+remainingWords.get(i));
                    }
                    wordIndex = scanner.nextInt();
                }
                GridWord selectedWordToGuess = remainingWords.get(wordIndex);
                System.out.println("What's your guess for this word?\n\t"+selectedWordToGuess);
                System.out.print("\n> ");
                String wordAnswer = scanner.next();
                if( wordAnswer.toUpperCase().equals(selectedWordToGuess.getContent())) {
                    System.out.println("This is correct!");
                    selectedWordToGuess.reveal();
                }else {
                    System.out.println("Wrong guess, try again next time!");
                }
            }

            System.out.println("Congratulations!");
        } catch (InvalidWordFileException | InvalidGridFileException | InvalidQuestionFileException | IOException e) {
            e.printStackTrace();
        }
    }

}


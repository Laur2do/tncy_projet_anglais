import model.*;
import model.loader.InvalidGridFileException;
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

            Grid grid = game.getRandomGrid();
            System.out.println(grid.shortInfo());
            System.out.println(grid.toString());

            Scanner scanner = new Scanner(System.in);
            while (!grid.isRevealed()) {
                System.out.print("Pick a letter\n> ");
                String answer = scanner.next();
                if (!Game.validLetter(answer)) {
                    System.err.println("Please enter a valid letter");
                    continue;
                }
                char c = Character.toUpperCase(answer.charAt(0));
                grid.revealLetter(c);
                System.out.println(grid.toString());
            }

            System.out.println("Congratulations!");
        } catch (InvalidWordFileException | InvalidGridFileException | IOException e) {
            e.printStackTrace();
        }
    }

}


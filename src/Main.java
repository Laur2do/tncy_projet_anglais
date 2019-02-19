import model.*;
import model.loader.InvalidGridFileException;
import model.loader.InvalidWordFileException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Main{

    /*@Override
    public void start(Stage primaryStage) throws Exception {
        /*Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }*/


    public static void main(String[] args) {
       // launch(args);
        Game g = Game.getInstance();
        try {
            int loadedWords = g.loadWords("data/words.csv");
            System.out.println("Loaded " + loadedWords + " words");
            HashMap<String, Word> words = g.getWords();
            for (Word w : words.values()) {
                System.out.println(w.toString());
            }

            int gridLoaded = g.loadGrids("data/grids");

            System.out.println("Loaded " + gridLoaded + " grids");
        }catch(InvalidWordFileException e) {
            e.printStackTrace();
        } catch (InvalidGridFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}


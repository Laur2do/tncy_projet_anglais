import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.*;

import java.util.ArrayList;


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
        g.loadData("data.csv");
        ArrayList<Word> arraylist = g.getListofWords();
        for(Word w : arraylist){
            System.out.println(w.toString());
        }

    }

}


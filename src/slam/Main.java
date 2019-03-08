package slam;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import slam.controller.GridCtl;
import slam.controller.QuestionCtl;
import slam.model.Game;
import slam.model.Word;
import slam.model.loader.InvalidGridFileException;
import slam.model.loader.InvalidQuestionFileException;
import slam.model.loader.InvalidWordFileException;

import java.io.IOException;
import java.util.HashMap;

public class Main extends Application {

    private final static boolean DEBUG = true;
    public final static String TITLE = "Grand Chelem";

    public static void printdebugln(Object obj) {
        if( DEBUG ) {
            System.out.println(obj);
        }
    }

    private void loadGameData() {
        Game game = Game.getInstance();
        try {
            int loadedWords = game.loadWords("data/words.csv");
            int gridLoaded = game.loadGrids("data/grids");
            int questionsLoaded = game.loadQuestions("data/questions.csv");

            if (DEBUG) {
                System.out.println("Loaded " + loadedWords + " words");
                System.out.println("================================");
                HashMap<String, Word> words = game.getWords();
                for (Word w : words.values()) {
                    System.out.println(w.getDescription());
                }
                System.out.println("================================");
                System.out.println("Loaded " + questionsLoaded + " questions");
                System.out.println("Loaded " + gridLoaded + " grids");
            }

        } catch (InvalidWordFileException | InvalidGridFileException | InvalidQuestionFileException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load model's data
        loadGameData();

        // Load Window (root)
        BorderPane rootPane = FXMLLoader.load(getClass().getResource("view/Window.fxml"));
        primaryStage.setTitle("Grand Chelem");
        // required now for lookup
        primaryStage.show();

        // Set the scene from the root
        Scene scene = new Scene(rootPane, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("view/style.css").toExternalForm());
        primaryStage.setScene(scene);

        // Load the center
        BorderPane centerBorderPane = (BorderPane) rootPane.lookup("#centerBorderPane");

        // Load the grid
        FXMLLoader gridFXML = new FXMLLoader();
        gridFXML.setLocation(getClass().getResource("view/Grid.fxml"));
        GridPane gridPane = gridFXML.load();

        // Load the question pane
        FXMLLoader questionFXML = new FXMLLoader();
        questionFXML.setLocation(getClass().getResource("view/Question.fxml"));
        VBox questionPane = questionFXML.load();


        // Assemble grid & root's center pane
        centerBorderPane.setCenter(gridPane);

        // Assemble grid & root
        centerBorderPane.setBottom(questionPane);

        GridCtl gridCtl = new GridCtl(gridPane);
        gridCtl.updateGridPane();

        QuestionCtl questionCtl = new QuestionCtl(gridCtl, questionPane);
        questionCtl.setNewQuestion();
    }


    public static void main(String[] args) {
        launch(args);
    }

}


package slam;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import slam.controller.WelcomeCtl;
import slam.controller.WindowCtl;

public class Main extends Application {

    private final static boolean DEBUG = true;
    public final static String TITLE = "Grand Chelem";

    public static void printDebugLn(Object obj) {
        if (DEBUG) {
            System.out.println(obj);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load Window (root)
        FXMLLoader windowFXML = new FXMLLoader();
        windowFXML.setLocation(getClass().getResource("view/Window.fxml"));
        BorderPane rootPane = windowFXML.load();

        primaryStage.setTitle("Grand Chelem");

        // Set the scene from the root
        Scene scene = new Scene(rootPane);
        scene.getStylesheets().add(getClass().getResource("view/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show(); // required now for lookup

        // Load the center
        BorderPane centerBorderPane = (BorderPane) rootPane.lookup("#centerBorderPane");

        // Load the welcome page
        FXMLLoader welcomeFXML = new FXMLLoader();
        welcomeFXML.setLocation(getClass().getResource("view/Welcome.fxml"));
        BorderPane welcome = welcomeFXML.load();

        // Load the question pane
        FXMLLoader questionFXML = new FXMLLoader();
        questionFXML.setLocation(getClass().getResource("view/Question.fxml"));
        VBox questionPane = questionFXML.load();
        questionPane.setVisible(false);

        // Assemble welcome pane & root's center pane
        centerBorderPane.setCenter(welcome);
        primaryStage.sizeToScene();

        WelcomeCtl welcomeCtl = welcomeFXML.getController();
        welcomeCtl.setPanes(centerBorderPane, questionPane);

        WindowCtl windowCtl = windowFXML.getController();
        windowCtl.setWelcomePane(welcome, welcomeCtl);
    }

    public static void main(String[] args) {
        launch(args);
    }

}


package slam;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import slam.controller.WelcomeCtl;
import slam.controller.WindowCtl;

public class Main extends Application {

    private final static boolean DEBUG = true;
    public final static String TITLE = "Slam Learning";

    public static void printDebugLn(Object obj) {
        if (DEBUG) {
            System.out.println(obj);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load Window (root)
        FXMLLoader windowFXML = new FXMLLoader();
        windowFXML.setLocation(Main.class.getResource("view/Window.fxml"));
        BorderPane rootPane = windowFXML.load();

        primaryStage.setTitle(TITLE);
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("view/res/icon.png")));

        // Set the scene from the root
        Scene scene = new Scene(rootPane);
        scene.getStylesheets().add(Main.class.getResource("view/res/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show(); // required now for lookup

        // Load the center
        BorderPane centerBorderPane = (BorderPane) rootPane.lookup("#centerBorderPane");

        // Load the welcome page
        FXMLLoader welcomeFXML = new FXMLLoader();
        welcomeFXML.setLocation(Main.class.getResource("view/Welcome.fxml"));
        BorderPane welcome = welcomeFXML.load();

        // Load the question pane
        FXMLLoader questionFXML = new FXMLLoader();
        questionFXML.setLocation(Main.class.getResource("view/Question.fxml"));
        VBox questionPane = questionFXML.load();
        questionPane.setVisible(false);

        // Assemble welcome pane & root's center pane
        centerBorderPane.setCenter(welcome);
        primaryStage.sizeToScene();

        WelcomeCtl welcomeCtl = welcomeFXML.getController();
        welcomeCtl.setPanes(centerBorderPane, questionPane);

        WindowCtl windowCtl = windowFXML.getController();
        windowCtl.setWelcomePane(welcome, welcomeCtl);

        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }

}


package slam.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import slam.Main;
import slam.model.Game;

import static slam.Main.DEBUG;


public class WindowCtl {

    @FXML
    private BorderPane root;

    public WindowCtl() {
    }

    public void fileLoadGrid() {
        //TODO
    }

    public void fileLoadQuestions() {
        //TODO
    }

    public void newGrid() {
        Game.getInstance().randomChangeCurrentGrid();
        if (DEBUG) {
            System.out.println("Loading new grid");
            System.out.println(Game.getInstance().getCurrentGrid());
        }
    }

    public void reset() {
        Game.getInstance().reset();
        if (DEBUG) {
            System.out.println("Reseting current grid");
            System.out.println(Game.getInstance().getCurrentGrid());
        }
    }

    public void exit() {
        if (DEBUG) {
            System.out.println("Exiting");
        }
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();


    }

    public void about() {
        String about = "This is a Java implementation of the TV French game Slam, made for a TELECOM Nancy 2A English project";
        if (DEBUG) {
            System.out.println(about);
        }
        Dialog d = new Dialog();
        Window window = d.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());
        d.setTitle("About "+ Main.TITLE);
        d.setHeaderText(d.getTitle());
        d.setContentText(about);
        d.show();
    }

}

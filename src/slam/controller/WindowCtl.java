package slam.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import slam.Main;
import slam.model.Game;

import static slam.Main.printdebugln;


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
        printdebugln(Game.getInstance().getCurrentGrid());
    }

    public void newRandomGrid() {
        Game.getInstance().generateRandomCurrentGrid();
        printdebugln(Game.getInstance().getCurrentGrid());
    }

    public void reset() {
        Game.getInstance().reset();
        printdebugln("Reseting current grid");
        printdebugln(Game.getInstance().getCurrentGrid());
    }

    public void exit() {
        printdebugln("Exiting");
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();


    }

    public void about() {
        String about = "This is a Java implementation of the TV French game Slam, made for a TELECOM Nancy 2A English project";
        printdebugln(about);
        Dialog d = new Dialog();
        Window window = d.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());
        d.setTitle("About "+ Main.TITLE);
        d.setHeaderText(d.getTitle());
        d.setContentText(about);
        d.show();
    }

}

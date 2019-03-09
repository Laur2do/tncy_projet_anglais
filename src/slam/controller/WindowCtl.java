package slam.controller;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import slam.Main;
import slam.model.Game;
import slam.model.Word;
import slam.model.loader.DataLoader;
import slam.model.loader.InvalidGridFileException;
import slam.model.loader.InvalidQuestionFileException;
import slam.model.loader.InvalidWordFileException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static slam.Main.printDebugLn;


public class WindowCtl {

    @FXML
    private BorderPane root;

    public WindowCtl() {
    }

    private void showErrorAlert(File f, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error reading file");
        alert.setHeaderText("Error reading file " + f);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private static String listToPrettyString(List l) {
        String filesList = l.toString();
        filesList = filesList.substring(1, filesList.length()-1);
        filesList = "\t- "+filesList.replaceAll(", ", ",\n\t- ");
        return filesList;
    }

    public void fileLoadDefaultData() {
        try {
            int loadedWords = Game.getInstance().loadWords("data/words.csv");
            int loadedGrids = Game.getInstance().loadGrids("data/grids");
            int loadedQuestions = Game.getInstance().loadQuestions("data/questions.csv");

            printDebugLn("Loaded " + loadedWords + " words");
            printDebugLn("================================");
            HashMap<String, Word> words = Game.getInstance().getWords();
            for (Word w : words.values()) {
                printDebugLn(w.getDescription());
            }
            printDebugLn("================================");
            printDebugLn("Loaded " + loadedQuestions + " questions");
            printDebugLn("Loaded " + loadedGrids + " grids");

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Default data files loaded!");
            confirm.setHeaderText("Default data files loaded!");
            confirm.setContentText("Default data files have been loaded successfully:\n" + loadedWords + " words\n" + loadedQuestions + " questions\n" + loadedGrids + " grids are now available.");
            confirm.showAndWait();

        } catch (IOException | InvalidGridFileException | InvalidWordFileException | InvalidQuestionFileException e) {
            System.err.println("System error while loading default files.");
            e.printStackTrace();
            showErrorAlert(new File("data"), e);
        }
    }

    public void fileLoadWords() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Comma Separated files", "*.csv"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Any", "*.*"));
        fc.setInitialDirectory(new File("data"));
        fc.setTitle("Choose a Word file (.CSV)");
        List<File> selectedWordFiles = fc.showOpenMultipleDialog(root.getScene().getWindow());
        if (selectedWordFiles == null) {
            return;
        }

        ArrayList<File> loadedWordFiles = new ArrayList<>();
        int wordsLoaded = 0;
        for (File wordFile : selectedWordFiles) {
            try {
                wordsLoaded += DataLoader.loadWordFile(wordFile.getCanonicalPath());
                loadedWordFiles.add(wordFile);
            } catch (IOException ioe) {
                System.err.println("System error while loading Word file " + wordFile);
                ioe.printStackTrace();
                showErrorAlert(wordFile, ioe);
            } catch (InvalidWordFileException iwfe) {
                printDebugLn(iwfe);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error in Word file");
                alert.setHeaderText("Error in Word file " + wordFile);
                alert.setContentText(iwfe.getMessage());
                alert.showAndWait();
                System.err.println("Invalid Grid file " + wordFile);
                iwfe.printStackTrace();
            }
        }

        printDebugLn("Loaded " + loadedWordFiles.size() + " Word file(s: " + loadedWordFiles);

        if (loadedWordFiles.size() > 0) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Word file(s) loaded!");
            confirm.setHeaderText("Word file(s) loaded!");

            confirm.setContentText(wordsLoaded + " words from the " + loadedWordFiles.size() + " Word file(s) \n" + listToPrettyString(loadedWordFiles) + "\n have been loaded successfully!");
            confirm.showAndWait();
        }
    }

    public void fileLoadGrid() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Comma Separated files", "*.csv"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Any", "*.*"));
        fc.setInitialDirectory(new File("data/grids"));
        fc.setTitle("Choose a Grid file (.CSV)");
        List<File> selectedGridFiles = fc.showOpenMultipleDialog(root.getScene().getWindow());
        if (selectedGridFiles == null) {
            return;
        }

        ArrayList<File> loadedGridFiles = new ArrayList<>();
        for (File gridFile : selectedGridFiles) {
            try {
                DataLoader.loadGridFile(gridFile.getCanonicalPath());
                loadedGridFiles.add(gridFile);
            } catch (IOException ioe) {
                System.err.println("System error while loading Grid file " + gridFile);
                ioe.printStackTrace();
                showErrorAlert(gridFile, ioe);
            } catch (InvalidGridFileException igfe) {
                printDebugLn(igfe);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error in Grid file");
                alert.setHeaderText("Error in Grid file " + gridFile);
                alert.setContentText(igfe.getMessage());
                alert.showAndWait();
                System.err.println("Invalid Grid file " + gridFile);
                igfe.printStackTrace();
            }
        }

        printDebugLn("Loaded " + loadedGridFiles.size() + " Grid file(s): " + loadedGridFiles);

        if (loadedGridFiles.size() > 0) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Grid file(s) loaded!");
            confirm.setHeaderText("Grid file(s) loaded!");
            confirm.setContentText(loadedGridFiles.size() + " from the " + loadedGridFiles.size() + " Grid file(s) \n" + listToPrettyString(loadedGridFiles) + "\n have been loaded successfully!");
            confirm.showAndWait();
        }
    }

    public void fileLoadQuestions() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Comma Separated files", "*.csv"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Any", "*.*"));
        fc.setInitialDirectory(new File("data"));
        fc.setTitle("Choose a Question file (.CSV)");
        List<File> selectedQuestionFiles = fc.showOpenMultipleDialog(root.getScene().getWindow());
        if (selectedQuestionFiles == null) {
            return;
        }

        ArrayList<File> loadedQuestionFiles = new ArrayList<>();
        int loadedQuestions = 0;
        for (File questionFile : selectedQuestionFiles) {
            try {
                loadedQuestions += DataLoader.loadQuestionFile(questionFile.getCanonicalPath());
                loadedQuestionFiles.add(questionFile);
            } catch (IOException ioe) {
                System.err.println("System error while loading Question file " + questionFile);
                ioe.printStackTrace();
                showErrorAlert(questionFile, ioe);
            } catch (InvalidQuestionFileException iqfe) {
                printDebugLn(iqfe);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error in Question file");
                alert.setHeaderText("Error in Question file " + questionFile);
                alert.setContentText(iqfe.getMessage());
                alert.showAndWait();
                System.err.println("Invalid Question file " + questionFile);
                iqfe.printStackTrace();
            }
        }
        printDebugLn("Loaded " + loadedQuestions + " questions from " + loadedQuestionFiles.size() + " Question file(s): " + loadedQuestionFiles);

        if (loadedQuestionFiles.size() > 0) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Question file(s) loaded!");
            confirm.setHeaderText("Question file(s) loaded!");
            confirm.setContentText(loadedQuestions + " questions from the "+loadedQuestionFiles.size()+" Question file(s) \n" + listToPrettyString(loadedQuestionFiles) + "\n have been loaded successfully!");
            confirm.showAndWait();
        }
    }

    public void newGrid() {
        Game.getInstance().randomChangeCurrentGrid();
        printDebugLn(Game.getInstance().getCurrentGrid());
    }

    public void newRandomGrid() {
        root.setCursor(Cursor.WAIT);
        root.getCenter().setDisable(true);

        Game.getInstance().generateRandomCurrentGrid();
        root.setCursor(Cursor.DEFAULT);
        root.getCenter().setDisable(false);
        printDebugLn(Game.getInstance().getCurrentGrid());
    }

    public void reset() {
        Game.getInstance().reset();
        printDebugLn("Reseting current grid");
        printDebugLn(Game.getInstance().getCurrentGrid());
    }

    public void exit() {
        printDebugLn("Exiting");
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();


    }

    public void about() {
        String about = "This is a Java implementation of the TV French game Slam, made for a TELECOM Nancy 2A English project";
        printDebugLn(about);
        Dialog d = new Dialog();
        Window window = d.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());
        d.setTitle("About " + Main.TITLE);
        d.setHeaderText(d.getTitle());
        d.setContentText(about);
        d.show();
    }

}

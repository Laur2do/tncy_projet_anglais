package slam.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Pair;
import slam.Main;
import slam.model.Game;
import slam.model.Grid;
import slam.model.loader.DataLoader;
import slam.model.loader.InvalidGridFileException;
import slam.model.loader.InvalidQuestionFileException;
import slam.model.loader.InvalidWordFileException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import static slam.Main.printDebugLn;


public class WindowCtl {

    @FXML
    private BorderPane root;

    public static final FilenameFilter CSVFileFilter = (File current, String name) -> {
        if (!name.endsWith(".csv")) {
            return false;
        }
        File f = new File(current, name);
        return !f.isDirectory() && f.canRead();
    };

    public WindowCtl() {
    }

    public  static void showErrorAlert(File f, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error reading file");
        alert.setHeaderText("Error reading file " + f);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    public static String listToPrettyString(Collection l) {
        String filesList = l.toString();
        filesList = filesList.substring(1, filesList.length() - 1);
        filesList = "\t- " + filesList.replaceAll(", ", ",\n\t- ");
        return filesList;
    }

    public void fileLoadDefaultData() {
        int loadedGrids, loadedQuestions, loadedWords;

        File wordsDir = new File("data/words");
        File[] wordsFile = wordsDir.listFiles(CSVFileFilter);
        if (wordsFile == null) {
            showErrorAlert(wordsDir, new IOException("Can't read directory!"));
            loadedWords = 0;
        } else {
            loadedWords = loadWords(Arrays.asList(wordsFile)).getKey();
        }

        File gridsDir = new File("data/grids");

        File[] gridsFile = gridsDir.listFiles(CSVFileFilter);
        if (gridsFile == null) {
            showErrorAlert(gridsDir, new IOException("Can't read directory!"));
            loadedGrids = 0;
        } else {
            loadedGrids = this.loadGrids(Arrays.asList(gridsFile)).size();
        }

        loadedQuestions = loadAllDefaultQuestions().getKey();

        if (loadedGrids > 0 && loadedQuestions > 0 && loadedWords > 0) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Default data files loaded!");
            confirm.setHeaderText("Default data files loaded!");
            confirm.setContentText("Default data files have been loaded successfully:\n" + loadedWords + " words\n" + loadedQuestions + " questions\n" + loadedGrids + " grids are now available.");
            confirm.showAndWait();
        } else if (loadedGrids > 0 || loadedQuestions > 0 || loadedWords > 0) {
            Alert confirm = new Alert(Alert.AlertType.WARNING);
            confirm.setTitle("Only some default data files could be loaded!");
            confirm.setHeaderText("Only some default data files could be loaded!");
            confirm.setContentText("Default data files have been loaded, but partially:\n" + loadedWords + " words\n" + loadedQuestions + " questions\n" + loadedGrids + " grids are now available.");
            confirm.showAndWait();
        } else {
            Alert confirm = new Alert(Alert.AlertType.ERROR);
            confirm.setTitle("Default data files not loaded!");
            confirm.setHeaderText("Default data files not loaded!");
            confirm.setContentText("Default data files could not be loaded! Check that the directory " + new File("data").getAbsolutePath() + " contains correct data.");
            confirm.showAndWait();
        }
    }

    public static Pair<Integer, Collection<File>> loadWords(Collection<File> wordFiles) {
        ArrayList<File> loadedWordFiles = new ArrayList<>();
        int wordsLoaded = 0;
        for (File wordFile : wordFiles) {
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
        return new Pair<>(wordsLoaded, loadedWordFiles);
    }

    public static List<File> CSVFileSelector(Window window) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Comma Separated files", "*.csv"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Any", "*.*"));
        fc.setInitialDirectory(new File("data/words"));
        fc.setTitle("Choose a Word file (.CSV)");
        return fc.showOpenMultipleDialog(window);
    }

    public void fileLoadWords() {
        List<File> selectedWordFiles = CSVFileSelector(root.getScene().getWindow());

        Pair<Integer, Collection<File>> loadedWords = loadWords(selectedWordFiles);
        int loadedFiles = loadedWords.getValue().size();

        if (loadedFiles > 0) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Word file(s) loaded!");
            confirm.setHeaderText("Word file(s) loaded!");

            confirm.setContentText(loadedWords.getKey() + " Words from the " + loadedFiles + " Word file(s) \n" + listToPrettyString(loadedWords.getValue()) + "\n have been loaded successfully!");
            confirm.showAndWait();
        }
    }

    private Collection<File> loadGrids(Collection<File> gridFiles) {
        ArrayList<File> loadedGridFiles = new ArrayList<>();
        for (File gridFile : gridFiles) {
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
        return loadedGridFiles;
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

        Collection<File> loadedGridFiles = loadGrids(selectedGridFiles);
        int loadedFiles = loadedGridFiles.size();

        if (loadedFiles > 0) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Grid file(s) loaded!");
            confirm.setHeaderText("Grid file(s) loaded!");
            confirm.setContentText(loadedFiles + " Grids from the " + loadedFiles + " Grid file(s) \n" + listToPrettyString(loadedGridFiles) + "\n have been loaded successfully!");
            confirm.showAndWait();
        }
    }

    public static Pair<Integer, Collection<File>> loadAllDefaultQuestions() {
        File questionsDir = new File("data/questions");
        File[] questionsFiles = questionsDir.listFiles(CSVFileFilter);
        if (questionsFiles == null) {
            showErrorAlert(questionsDir, new IOException("Can't read directory!"));
            return new Pair<>(0, Collections.emptyList());
        } else {
            return loadQuestions(Arrays.asList(questionsFiles));
        }
    }

    public static Pair<Integer, Collection<File>> loadQuestions(Collection<File> questionFiles) {
        ArrayList<File> loadedQuestionFiles = new ArrayList<>();
        int loadedQuestions = 0;
        for (File questionFile : questionFiles) {
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
        return new Pair<>(loadedQuestions, loadedQuestionFiles);
    }

    public void fileLoadQuestions() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Comma Separated files", "*.csv"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Any", "*.*"));
        fc.setInitialDirectory(new File("data/questions"));
        fc.setTitle("Choose a Question file (.CSV)");
        List<File> selectedQuestionFiles = fc.showOpenMultipleDialog(root.getScene().getWindow());
        if (selectedQuestionFiles == null) {
            return;
        }

        Pair<Integer, Collection<File>> loadedQuestions = loadQuestions(selectedQuestionFiles);
        int loadedFiles = loadedQuestions.getValue().size();

        if (loadedFiles > 0) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Question file(s) loaded!");
            confirm.setHeaderText("Question file(s) loaded!");
            confirm.setContentText(loadedQuestions.getKey() + " questions from the " + loadedFiles + " Question file(s) \n" + listToPrettyString(loadedQuestions.getValue()) + "\n have been loaded successfully!");
            confirm.showAndWait();
        }
    }

    public void newGrid() {
        Game.getInstance().randomChangeCurrentGrid();
        printDebugLn(Game.getInstance().getCurrentGrid());
    }

    public static void newRandomGrid(Node n, Callback<Grid, Void> cb) {
        n.setCursor(Cursor.WAIT);
        n.setDisable(true);
        new Thread(() -> {
            Game.getInstance().generateRandomCurrentGrid();
            printDebugLn(Game.getInstance().getCurrentGrid());
            Platform.runLater(() -> {
                Game.getInstance().notifyObservers();
                n.setCursor(Cursor.DEFAULT);
                n.setDisable(false);
                if(cb != null) {
                    cb.call(Game.getInstance().getCurrentGrid());
                }
            });
        }).start();
    }

    public void newRandomGrid() {
        newRandomGrid(root.getCenter(), null);
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

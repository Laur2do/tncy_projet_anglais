package slam.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Pair;
import slam.Main;
import slam.model.Game;
import slam.model.Grid;
import slam.model.loader.DataLoader;
import slam.model.loader.InvalidQuestionFileException;
import slam.model.loader.InvalidWordFileException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import static slam.Main.printDebugLn;


public class WindowCtl {

    private static final boolean SHOW_POPUP_MESSAGES = false;

    private static Label statusRef;

    @FXML
    private BorderPane root;

    @FXML
    private Label status;

    public static final FilenameFilter CSVFileFilter = (File current, String name) -> {
        if (!name.endsWith(".csv")) {
            return false;
        }
        File f = new File(current, name);
        return !f.isDirectory() && f.canRead();
    };
    private BorderPane welcomePane;
    private WelcomeCtl welcomeCtl;


    public WindowCtl() {
    }

    public static void packWindow() {
        if(statusRef != null) {
            Window w = statusRef.getScene().getWindow();
            w.sizeToScene();
        }
    }

    @FXML
    private void initialize() {
        WindowCtl.statusRef = this.status;
    }

    public static void showErrorAlert(File f, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error reading file");
        alert.setHeaderText("Error reading file " + f);
        alert.setContentText(e.getMessage());
        alert.getDialogPane().getScene().getWindow().centerOnScreen();

        alert.initOwner(statusRef.getScene().getWindow());
        alert.showAndWait();
    }

    public static String listToPrettyString(Collection l) {
        String filesList = l.toString();
        filesList = filesList.substring(1, filesList.length() - 1);
        filesList = filesList.replaceAll("data[/\\\\](words|questions)[/\\\\]", "");
        return filesList;
    }

    public static void showMessage(AlertType type, String title, String message) {
        if (SHOW_POPUP_MESSAGES) {
            showPopup(type, title, message);
        } else {
            if (message == null || message.isEmpty()) {
                message = title;
            }
            setStatus(type, message.replaceAll("\n", " "));
        }
    }

    private static void showPopup(AlertType type, String title, String message) {
        Alert confirm = new Alert(type);

        confirm.setTitle(title);
        confirm.setHeaderText(title);
        confirm.initOwner(statusRef.getScene().getWindow());
        confirm.initModality(Modality.WINDOW_MODAL);
        if (message != null) {
            confirm.setContentText(message);
        }
        confirm.showAndWait();
    }

    private static void setStatus(AlertType type, String statusText) {
        if (WindowCtl.statusRef == null) {
            System.err.println("Trying to set the status while it isn't set yet");
            return;
        }
        switch (type) {
            case INFORMATION:
                WindowCtl.statusRef.getStyleClass().add("information");
                break;
            case ERROR:
                WindowCtl.statusRef.getStyleClass().add("error");
                break;
            case WARNING:
                WindowCtl.statusRef.getStyleClass().add("warning");
                break;
            default:
                WindowCtl.statusRef.getStyleClass().add("dialog-pane");
                break;
        }
        if (statusText != null) {
            WindowCtl.statusRef.setText(statusText);
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
                showPopup(AlertType.ERROR,
                        "Error in Word file",
                        iwfe.getMessage());
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
            showMessage(AlertType.INFORMATION,
                    "Word file(s) loaded!",
                    loadedWords.getKey() + " Words from the " + loadedFiles + " Word file(s) \n" + listToPrettyString(loadedWords.getValue()) + "\n have been loaded successfully!");
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

    private static Pair<Integer, Collection<File>> loadQuestions(Collection<File> questionFiles) {
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
                showPopup(AlertType.ERROR,
                        "Error in Question file",
                        iqfe.getMessage());
                System.err.println("Invalid Question file " + questionFile);
                iqfe.printStackTrace();
            }
        }
        printDebugLn("Loaded " + loadedQuestions + " questions from " + loadedQuestionFiles.size() + " Question file(s): " + loadedQuestionFiles);
        return new Pair<>(loadedQuestions, loadedQuestionFiles);
    }

    public void fileLoadQuestions() {
        fileLoadQuestions(this.root.getScene().getWindow());
    }

    public static void fileLoadQuestions(Window w) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Comma Separated files", "*.csv"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Any", "*.*"));
        fc.setInitialDirectory(new File("data/questions"));
        fc.setTitle("Choose a Question file (.CSV)");
        List<File> selectedQuestionFiles = fc.showOpenMultipleDialog(w);
        if (selectedQuestionFiles == null) {
            return;
        }

        Pair<Integer, Collection<File>> loadedQuestions = loadQuestions(selectedQuestionFiles);
        int loadedFiles = loadedQuestions.getValue().size();

        if (loadedFiles > 0) {
            showMessage(AlertType.CONFIRMATION,
                    "Question file(s) loaded!",
                    loadedQuestions.getKey() + " questions from the " + loadedFiles + " Question file(s) \n" + listToPrettyString(loadedQuestions.getValue()) + "\n have been loaded successfully!");
        }
    }

    public static void newRandomGrid(Node n, Callback<Grid, Void> cb) {
        if ( !Game.getInstance().canStart()) {
            return;
        }
        n.setCursor(Cursor.WAIT);
        n.setDisable(true);
        new Thread(() -> {
            Game.getInstance().generateRandomCurrentGrid();
            printDebugLn(Game.getInstance().getCurrentGrid());
            Platform.runLater(() -> {
                Game.getInstance().notifyObservers();
                n.setCursor(Cursor.DEFAULT);
                n.setDisable(false);
                if (cb != null) {
                    cb.call(Game.getInstance().getCurrentGrid());
                }
            });
        }).start();
    }

    public void newRandomGrid() {
        newRandomGrid(root, null);
        packWindow();
    }

    public void newGame() {
        Game.getInstance().resetGame();
        welcomeCtl.reset();
        ((BorderPane)root.getCenter()).setCenter(welcomePane);
        ((BorderPane)root.getCenter()).setBottom(null);
        packWindow();
    }

    public void reset() {
        Game.getInstance().resetGrid();

        printDebugLn("Resetting current grid");
        printDebugLn(Game.getInstance().getCurrentGrid());
    }

    public void exit() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Are you sure you want to quit?");
        alert.setHeaderText("Do you already feel prepared enough to pass your TOEIC?");
        alert.getDialogPane().getScene().getWindow().centerOnScreen();
        alert.initModality(Modality.WINDOW_MODAL);

        alert.showAndWait();
        ButtonType result = alert.getResult();

        if( result.getButtonData().isDefaultButton()) {
            printDebugLn("Exiting");
            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
        }
    }

    public void about() {
        String about = Main.TITLE+" is a Java implementation of the TV French game 'Slam!', made for a TELECOM Nancy 2A English project.";
        about += "\n\n";
        about += "March 2018\n\n";
        about += "Made by:\n";
        about += " - Laury de DONATO\n";
        about += " - Marie TUAUDEN\n";
        printDebugLn(about);
        Dialog d = new Dialog();
        Window dialogWindow = d.getDialogPane().getScene().getWindow();
        dialogWindow.setOnCloseRequest(event -> dialogWindow.hide());
        d.initOwner(root.getScene().getWindow());
        d.setTitle("About " + Main.TITLE);
        d.setHeaderText(d.getTitle());
        d.setContentText(about);
        d.show();
    }

    public void howTo() {
        WebView webview = new WebView();
        webview.getEngine().loadContent(HOW_TO_HTML);
        final Stage dialog = new Stage();
        dialog.initOwner(root.getScene().getWindow());
        dialog.setTitle("How to play " + Main.TITLE);
        Scene scene = new Scene(webview);
        dialog.setScene(scene);
        dialog.sizeToScene();
        dialog.show();
        dialog.getIcons().add(new Image(Main.class.getResourceAsStream("view/res/icon.png")));
    }

    public void setWelcomePane(BorderPane welcome, WelcomeCtl controller) {
        this.welcomePane = welcome;
        this.welcomeCtl = controller;
    }

    private final static String HOW_TO_HTML = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <style type=\"text/css\">\n" +
            "        * {font-family:sans-serif;color:#323232}\n" +
            "        body {background-color: #D6E6FF}\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>How to play Slam Learning</h1>\n" +
            "\n" +
            "<p>It is very simple to start playing Slam Learning:</p>\n" +
            "<ol>\n" +
            "    <li>Select one or more word decks you want to learn<br />\n" +
            "    <span style=\"font-size:small\">Alternatively and additionally, you can also select your own Words decks and questions decks as CSV files.</span></li>\n" +
            "    <li>You can start the game!</li>\n" +
            "</ol>\n" +
            "\n" +
            "<h2>Slam Learning basics</h2>\n" +
            "<p>The goal of the game is to completely reveal the generated grid. For this, you will have two repeated steps:</p>\n" +
            "<ol style=\"list-style-type: upper-alpha;\">\n" +
            "    <li><strong>Guess a letter</strong><br />\n" +
            "        <ul>\n" +
            "            <li>You need to answer a question to find some unrevealed letter in the grid</li>\n" +
            "            <li>If you answer is wrong, you will restart to step A.</li>\n" +
            "            <li>If you answer is correct, you will have the explanation and go to the next step.</li>\n" +
            "        </ul>\n" +
            "    </li>\n" +
            "    <li><strong>Pick and guess a word</strong><br />\n" +
            "        <ul>\n" +
            "            <li>You can now click on any word you like on the grid to select it.<br />Be careful not to select a letter common to two words: the game can't know the one you meant!</li>\n" +
            "            <li>Once a word is selected, you will have a definition to help you find the correct answer.</li>\n" +
            "            <li>If you guess right, the word will be revealed on the grid.</li>\n" +
            "            <li>Either way, you will restart to step A.</li>\n" +
            "        </ul>\n" +
            "    </li>\n" +
            "</ol>\n" +
            "</body>\n" +
            "</html>";
}

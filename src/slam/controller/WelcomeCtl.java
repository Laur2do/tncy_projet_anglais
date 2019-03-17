package slam.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import javafx.util.StringConverter;
import slam.Main;
import slam.model.Game;
import slam.model.Grid;
import slam.model.loader.DataLoader;
import slam.model.loader.InvalidWordFileException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WelcomeCtl {

    @FXML
    private BorderPane root;

    @FXML
    private ComboBox<File> themeSelector;

    @FXML
    private Button startRandomGame;

    private BorderPane centerBorderPane;
    private VBox questionPane;

    private boolean isSelecting;

    public WelcomeCtl() {
    }

    @FXML
    public void initialize() {
        File wordsDir = new File("data/words");
        File[] wordsFile = wordsDir.listFiles(WindowCtl.CSVFileFilter);
        if (wordsFile == null) {
            WindowCtl.showErrorAlert(wordsDir, new IOException("Can't read directory!"));
        } else {
            this.themeSelector.setConverter(new StringConverter<File>() {
                @Override
                public String toString(File f) {
                    return f.getName();
                }

                @Override
                public File fromString(String filename) {
                    return wordsDir.toPath().resolve(filename).toFile();
                }
            });
            this.themeSelector.getItems().clear();
            this.themeSelector.getItems().addAll(Arrays.asList(wordsFile));
        }
        loadQuestions();
    }

    public void loadWordFile() {
        if (this.isSelecting) {
            return;
        }
        this.isSelecting = true;
        File selectedWordFile = this.themeSelector.getSelectionModel().getSelectedItem();
        if (selectedWordFile == null) {
            return;
        }
        Pair<Integer, Collection<File>> result = loadWordFile(Collections.singleton(selectedWordFile));
        if (result.getValue().size() == 1) {
            this.themeSelector.getItems().remove(selectedWordFile);
            // Clearing selection after removal triggers a IndexOutOfBoundsException from the themeSelector's items ListChangeListener
            // It seems harmless and there doesn't seem to be an easy way to fix this.
            this.themeSelector.getSelectionModel().clearSelection();

            if (this.themeSelector.getItems().isEmpty()) {
                this.themeSelector.setDisable(false);
            }
            if (Game.getInstance().canStart()) {
                this.startRandomGame.setDisable(false);
            }
        }
        this.isSelecting = false;
    }

    public void setPanes(BorderPane centerBorderPane, VBox questionPane) {
        this.centerBorderPane = centerBorderPane;
        this.questionPane = questionPane;
    }

    public void reset() {
        initialize();
        this.isSelecting = false;
    }

    public void startRandomGame() {
        if (Game.getInstance().canStart()) {
            WindowCtl.newRandomGrid(this.root.getParent(), (Grid g) -> {
                if( g == null) {
                    return null;
                }
                try {
                    // Load the grid
                    FXMLLoader gridFXML = new FXMLLoader();
                    gridFXML.setLocation(Main.class.getResource("view/Grid.fxml"));
                    GridPane gridPane = gridFXML.load();

                    // Assemble grid & root's center pane
                    this.centerBorderPane.setCenter(gridPane);

                    GridCtl gridCtl = new GridCtl(gridPane);
                    gridCtl.updateGridPane();
                    QuestionCtl questionCtl = new QuestionCtl(gridCtl, this.questionPane);
                    questionCtl.setNewQuestion();

                    this.centerBorderPane.setBottom(this.questionPane);
                    this.questionPane.setVisible(true);
                    this.centerBorderPane.getCenter().setVisible(true);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                return null;
            });
        }
    }

    private Pair<Integer, Collection<File>> loadWordFile(Collection<File> selectedWordFiles) {
        Pair<Integer, Collection<File>> loadedWords = WindowCtl.loadWords(selectedWordFiles);
        int loadedFiles = loadedWords.getValue().size();

        if (loadedFiles > 0) {
            WindowCtl.showMessage(AlertType.CONFIRMATION,
                    "Word file(s) loaded!",
                    loadedWords.getKey() + " Words from the " + loadedFiles + " Word file(s) \n" + WindowCtl.listToPrettyString(loadedWords.getValue()) + "\n have been loaded successfully!");
        }
        return loadedWords;
    }

    public void loadCustomWordFile() {
        List<File> selectedWordFiles = WindowCtl.CSVFileSelector(root.getScene().getWindow());
        if (selectedWordFiles == null) {
            return;
        }
        ArrayList<File> correctWordFiles = new ArrayList<>();
        for(File f : selectedWordFiles) {
            try {
                DataLoader.loadWordFile(f.getAbsolutePath(), true);
                correctWordFiles.add(f);
            }catch(InvalidWordFileException iwfe) {
                WindowCtl.showPopup(AlertType.ERROR,
                        "Error in Word file",
                        iwfe.getMessage());
                System.err.println("Invalid Word file " + f);
                iwfe.printStackTrace();
            }
        }

        themeSelector.getItems().addAll(selectedWordFiles);
        WindowCtl.showMessage(AlertType.CONFIRMATION,
                "Word(s) file added to the Word decks list",
                "Words from the " + correctWordFiles.size() + " Word file(s) \n" + WindowCtl.listToPrettyString(correctWordFiles) + "\n can now be loaded!");
    }

    private static void loadQuestions() {
        Pair<Integer, Collection<File>> result = WindowCtl.loadAllDefaultQuestions();
        if (result.getValue().size() > 0) {
            WindowCtl.showMessage(AlertType.CONFIRMATION,
                    "Question file(s) loaded!",
                    result.getKey() + " Questions have been loaded successfully!");
        }
    }

    public void loadCustomQuestionFile() {
        WindowCtl.fileLoadQuestions(this.root.getScene().getWindow());
    }
}

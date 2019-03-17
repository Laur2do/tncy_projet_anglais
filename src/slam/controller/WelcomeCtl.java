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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WelcomeCtl {

    @FXML
    private BorderPane root;

    @FXML
    private ComboBox<File> deckSelector;

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
            deckSelector.setConverter(new StringConverter<File>() {
                @Override
                public String toString(File f) {
                    return f.getName();
                }

                @Override
                public File fromString(String filename) {
                    return wordsDir.toPath().resolve(filename).toFile();
                }
            });
            deckSelector.getItems().clear();
            deckSelector.getItems().addAll(Arrays.asList(wordsFile));
        }
        loadQuestions();
    }

    public void loadWordFile() {
        if (isSelecting) {
            return;
        }
        isSelecting = true;
        File selectedWordFile = deckSelector.getSelectionModel().getSelectedItem();
        if (selectedWordFile == null) {
            return;
        }
        Pair<Integer, Collection<File>> result = loadWordFile(Collections.singleton(selectedWordFile));
        if (result.getValue().size() == 1) {
            deckSelector.getItems().remove(selectedWordFile);
            // Clearing selection after removal triggers a IndexOutOfBoundsException from the deckSelector's items ListChangeListener
            // It seems harmless and there doesn't seem to be an easy way to fix this.
            deckSelector.getSelectionModel().clearSelection();

            if (deckSelector.getItems().isEmpty()) {
                deckSelector.setDisable(false);
            }
            if (Game.getInstance().canStart()) {
                startRandomGame.setDisable(false);
            }
        }
        isSelecting = false;
    }

    public void setPanes(BorderPane centerBorderPane, VBox questionPane) {
        this.centerBorderPane = centerBorderPane;
        this.questionPane = questionPane;
    }

    public void reset() {
        initialize();
        isSelecting = false;
    }

    public void startRandomGame() {
        if (Game.getInstance().canStart()) {
            WindowCtl.newRandomGrid(root, (Grid g) -> {
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
                    QuestionCtl questionCtl = new QuestionCtl(gridCtl, questionPane);
                    questionCtl.setNewQuestion();

                    this.centerBorderPane.setBottom(this.questionPane);
                    this.questionPane.setVisible(true);
                    this.centerBorderPane.getCenter().setVisible(true);
                    WindowCtl.showMessage(AlertType.INFORMATION, "Random grid generated!", "");
                    WindowCtl.packWindow();
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
        loadWordFile(selectedWordFiles);
    }

    public static void loadQuestions() {
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

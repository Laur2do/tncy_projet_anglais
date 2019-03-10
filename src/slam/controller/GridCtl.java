package slam.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import slam.model.*;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import static slam.Main.printDebugLn;


public class GridCtl implements Observer {
    private final GridPane gridPane;
    private Node questionPane;

    private QuestionCtl questionCtl;

    private boolean canGuessWord;

    private interface Lambda {
        void execute();
    }

    public GridCtl(GridPane gridPane) {
        this.gridPane = gridPane;
        Game.getInstance().addObserver(this);

        BorderPane centerBorderPane = ((BorderPane) this.gridPane.getParent());
        this.questionPane = centerBorderPane.getBottom();
    }

    private Label getLabel(int col, int row) {
        for (Node node : this.gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return (Label) node;
            }
        }
        return null;
    }

    private void showGuessWord(GridWord gw, Lambda l) {
        // Load the popup
        try {
            FXMLLoader popupFXML = new FXMLLoader();
            popupFXML.setLocation(getClass().getResource("../view/GuessWord.fxml"));
            VBox guessWordDialogContent = popupFXML.load();

            GuessWordCtl ctl = popupFXML.getController();
            ctl.setGridWord(gw);

            BorderPane centerBorderPane = ((BorderPane) this.gridPane.getParent());
            this.questionPane = centerBorderPane.getBottom();
            centerBorderPane.setBottom(guessWordDialogContent);

            TextField answer = (TextField) guessWordDialogContent.lookup("#answer");
            answer.setOnAction(event -> {
                ctl.validate();

                this.updateGridPane();
                centerBorderPane.setBottom(this.questionPane);
                l.execute();
                this.setEnableGuess(false, this.questionCtl);
                for (Node n : this.gridPane.getChildren()) {
                    n.getStyleClass().remove("cell-enabled");
                }
                if (Game.getInstance().getCurrentGrid().isRevealed()) {
                    this.questionCtl.showCongratulations();
                } else {
                    this.questionCtl.cleanMessage();
                    this.questionCtl.setNewQuestion();
                }

            });

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void setEnableGuess(boolean enable, QuestionCtl questionCtl) {
        this.canGuessWord = enable;
        this.questionCtl = questionCtl;
    }

    public void updateGridPane() {
        Grid currentGrid = Game.getInstance().getCurrentGrid();
        this.gridPane.getChildren().clear();

        for (final GridWord gw : currentGrid.getPlacedWords()) {
            boolean[] revealed = gw.getRevealedLetters();
            final Label[] cellLabels = new Label[revealed.length];
            for (int index = 0; index < gw.getLength(); index++) {
                // Current letter
                char letter = Cell.NOT_REVEALED_LETTER_CHAR;
                if (revealed[index]) {
                    letter = gw.getLetter(index);
                }

                int x = gw.getX(), y = gw.getY() + index;
                if (gw.getOrientation() == Orientation.HORIZONTAL) {
                    x = gw.getX() + index;
                    y = gw.getY();
                }

                // New Label or retrieve old one?
                Label cellLabel = getLabel(x, y);
                if (cellLabel == null) {
                    // New label
                    cellLabel = new Label(String.valueOf(letter));
                    this.gridPane.add(cellLabel, x, y);
                    cellLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                        if (this.canGuessWord && !gw.isRevealed()) {
                            this.canGuessWord = false;
                            showGuessWord(gw, () -> {
                                printDebugLn(currentGrid.toString());
                                for (Label label : cellLabels) {
                                    label.getStyleClass().remove("cell-focus");
                                }
                            });
                            for (Label label : cellLabels) {
                                label.getStyleClass().add("cell-focus");
                            }
                        }
                    });
                } else {
                    // If it already exists, ignore the click as we don't know which word the user wants to select
                    cellLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);
                    if (letter != Cell.NOT_REVEALED_LETTER_CHAR && cellLabel.getText().charAt(0) == Cell.NOT_REVEALED_LETTER_CHAR) {
                        // There is a conflict. If the letter is revealed in this world, we assume it should be revealed
                        cellLabel.setText(String.valueOf(letter));
                    }
                }
                cellLabels[index] = cellLabel;

                cellLabel.getStyleClass().add("cell");
                if( revealed[index] ) {
                    cellLabel.getStyleClass().add("cell-revealed");
                }
                cellLabel.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                    if (this.canGuessWord) {
                        for (Label label : cellLabels) {
                            label.getStyleClass().add("cell-hover");
                        }
                    }
                });
                cellLabel.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                    if (this.canGuessWord) {
                        for (Label label : cellLabels) {
                            label.getStyleClass().remove("cell-hover");
                        }
                    }
                });
                if(!gw.isRevealed() && gw.isAlreadyGuessed()) {
                    cellLabel.getStyleClass().add("cell-guessed");
                } else if (gw.isRevealed()){
                    cellLabel.getStyleClass().remove("cell-guessed");
                }
            }
        }
        if (this.canGuessWord) {
            for (Node n : this.gridPane.getChildren()) {
                n.getStyleClass().add("cell-enabled");
            }
        }
    }

    public void update(Observable obs, Object obj) {
        this.updateGridPane();
    }
}

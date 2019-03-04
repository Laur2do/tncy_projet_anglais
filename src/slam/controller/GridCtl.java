package slam.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import slam.model.*;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


public class GridCtl implements Observer {
	private GridPane gridPane;

	public GridCtl(GridPane gridPane) {
		this.gridPane = gridPane;
		Game.getInstance().addObserver(this);
		Game.getInstance().randomChangeCurrentGrid();
		System.out.println(Game.getInstance().getCurrentGrid());
	}

	private Label getLabel(int col, int row) {
		for (Node node : this.gridPane.getChildren()) {
			if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
				return (Label) node;
			}
		}
		return null;
	}

	private void showWordPopup(GridWord gw) {
		// Load the popup
		try {
			FXMLLoader popupFXML = new FXMLLoader();
			popupFXML.setLocation(getClass().getResource("../view/GuessWordPopup.fxml"));
			VBox guessWordDialogContent = popupFXML.load();

			GuessWordCtl ctl = popupFXML.getController();
			ctl.setGridWord(gw);

			Window parent = this.gridPane.getScene().getWindow();
			Stage dialog = new Stage();
			dialog.initModality(Modality.WINDOW_MODAL);

			dialog.setX(parent.getX()+parent.getWidth()/2-dialog.getWidth()/2);
			dialog.setY(parent.getY() + parent.getHeight()/2-dialog.getHeight()/2);

			dialog.setTitle("Guess the Word!");
			dialog.setScene(new Scene(guessWordDialogContent));

			dialog.show();

			dialog.addEventHandler(WindowEvent.ANY, event -> this.updateGridPane());
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
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
					cellLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> showWordPopup(gw));
				} else {
					// If it already exists, ignore the click as we don't know which word the user wants to select
					cellLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);
				}
				cellLabels[index] = cellLabel;

				cellLabel.getStyleClass().add("cell");
				cellLabel.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
					for (Label label : cellLabels) {
						label.getStyleClass().add("cell-hover");
					}
				});
				cellLabel.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
					for (Label label : cellLabels) {
						label.getStyleClass().remove("cell-hover");
					}
				});
			}
		}
	}

	public void update(Observable obs, Object obj) {
		this.updateGridPane();
	}
}

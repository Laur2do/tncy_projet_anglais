package slam.controller;

import java.util.Observable;
import java.util.Observer;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import slam.model.Cell;
import slam.model.Game;
import slam.model.Grid;


public class GridCtl implements Observer {
    private GridPane gridPane;

    public GridCtl(GridPane gridPane) {
        this.gridPane = gridPane;
        Game.getInstance().addObserver(this);
        Game.getInstance().randomChangeCurrentGrid();
        System.out.println(Game.getInstance().getCurrentGrid());
    }

    public void updateGridPane() {
        Grid currentGrid = Game.getInstance().getCurrentGrid();
        this.gridPane.getChildren().clear();
        for (int i = 0; i < currentGrid.getWidth(); i++) {
            Cell[] col = currentGrid.getColumn(i);
            for (int j = 0; j < col.length; j++) {
                if (col[j] != null) {
                    Label cell = new Label(col[j].toString());
                    cell.setPrefHeight(30);
                    cell.setPrefWidth(30);
                    cell.getStyleClass().add("cell");
                    this.gridPane.add(cell, i, j);
                }
            }
        }
    }

    public void update(Observable obs, Object obj) {
        this.updateGridPane();
    }
}

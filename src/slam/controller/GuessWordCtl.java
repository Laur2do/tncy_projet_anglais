package slam.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import slam.model.Cell;
import slam.model.GridWord;

import static slam.Main.DEBUG;

public class GuessWordCtl {

    private GridWord gridWord;

    @FXML
    private Label definition;

    @FXML
    private Label word;

    @FXML
    private TextField answer;

    public GuessWordCtl() {}

    public void setGridWord(GridWord gw) {
        this.gridWord = gw;
        StringBuilder sb = new StringBuilder();

        boolean[] revealedLetters = gw.getRevealedLetters();
        for (int i = 0; i < gw.getLength(); i++) {
            if (revealedLetters[i]) {
                sb.append(gw.getLetter(i));
            } else {
                sb.append(Cell.NOT_REVEALED_LETTER_CHAR);
            }
        }
        sb.append("\n");
        word.setText(sb.toString());

        definition.setText(gw.getDefinitions());
    }

    public void validate() {
        if (this.gridWord.getContent().equals(answer.getText().toUpperCase())) {
            if (DEBUG) {
                System.out.println("> " + this.answer.getText()+"\nCorrect!");
            }
            this.gridWord.reveal();
        } else if (DEBUG) {
            System.out.println("> " + this.answer.getText()+"\nIncorrect!");
        }
    }
}

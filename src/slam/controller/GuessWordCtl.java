package slam.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import slam.model.GridWord;

import java.util.function.UnaryOperator;

import static slam.Main.printDebugLn;

public class GuessWordCtl {

    private GridWord gridWord;

    @FXML
    private Label definition;

    @FXML
    private TextField answer;

    public GuessWordCtl() {
    }

    public void setGridWord(GridWord gw) {
        this.gridWord = gw;

        int wordLength = this.gridWord.getLength();
        this.definition.setText(gw.getDefinitions());

        printDebugLn("You picked " + gw.toString());

        this.answer.setPrefColumnCount(wordLength);
        this.answer.setMaxWidth(wordLength * this.answer.getFont().getSize());

        UnaryOperator<TextFormatter.Change> modifyChange = c -> {
            if (c.isContentChange()) {
                if (c.getControlNewText().length() > wordLength) {
                    // Trim the text to the first char
                    c.setText(c.getControlNewText().substring(0, wordLength));
                    // Set the range of the change from 0 to all the new added text
                    c.setRange(0, c.getControlText().length());
                }
            }
            return c;
        };
        this.answer.setTextFormatter(new TextFormatter(modifyChange));
        this.answer.requestFocus();
    }

    public void validate() {
        if (this.gridWord.validate(this.answer.getText())) {
            printDebugLn("> " + this.answer.getText() + "\nCorrect!");
            this.gridWord.reveal();
        } else {
            this.gridWord.setAlreadyGuessed();
            printDebugLn("> " + this.answer.getText() + "\nIncorrect!");
        }
    }
}

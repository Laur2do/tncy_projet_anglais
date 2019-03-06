package slam.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import slam.model.Game;
import slam.model.Question;

import java.util.Observable;
import java.util.Observer;
import java.util.function.UnaryOperator;

import static slam.Main.printdebugln;

public class QuestionCtl implements Observer {

    private Question currentQuestion;

    private GridCtl gridCtl;

    private VBox questionPane;
    private Label question;
    private TextField answer;
    private Label messageLabel;


    public QuestionCtl(GridCtl gridCtl, VBox questionPane) {
        Game.getInstance().addObserver(this);

        this.gridCtl = gridCtl;

        this.questionPane = questionPane;
        this.question = (Label) this.questionPane.lookup("#question");
        this.answer = (TextField) this.questionPane.lookup("#answer");
        this.messageLabel = (Label) this.questionPane.lookup("#message");

        UnaryOperator<TextFormatter.Change> modifyChange = c -> {
            if (c.isContentChange()) {
                if (c.getControlNewText().length() > 1) {
                    // Trim the text to the first char
                    c.setText(c.getControlNewText().substring(0, 1));
                    // Set the range of the change from 0 to all the new added text
                    c.setRange(0, c.getControlText().length());
                }
            }
            return c;
        };
        this.answer.setTextFormatter(new TextFormatter(modifyChange));
        this.answer.setOnAction(this::OKPressed);
    }

    public void setNewQuestion() {
        this.currentQuestion = Game.getInstance().getCurrentGrid().getRandomQuestionForRemainingLetters();
        if (this.currentQuestion != null) {
            this.question.setText(this.currentQuestion.getQuestion());
            this.questionPane.setVisible(true);
            this.answer.setVisible(true);
            printdebugln(this.currentQuestion.getQuestion());
        } else {
            this.answer.setText("");
            this.messageLabel.setText("");
            this.questionPane.setVisible(false);
        }
    }

    public void cleanMessage() {
        messageLabel.setText("");
    }

    public void displayError(String s) {
        printdebugln(s);
        printdebugln("Here is the question: \t" + this.currentQuestion);

        messageLabel.setText(s);
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("critical-error");
    }

    private void displayWrongAnswerMessage() {
        printdebugln("Wrong! Correct answer was: " + this.currentQuestion.getLetter());
        if (this.currentQuestion.getExplanation() != null) {
            printdebugln("\tbecause " + this.currentQuestion.getExplanation());
        }

        String contentText = "Wrong! The correct answer was '" + this.currentQuestion.getLetter() + "'";
        if (this.currentQuestion.getExplanation() != null) {
            contentText += ", because " + this.currentQuestion.getExplanation();
        }
        messageLabel.setText(contentText + ".");
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("error");
    }


    private void displayGoodAnswerMessage() {
        printdebugln("Correct!");
        if (this.currentQuestion.getExplanation() != null) {
            printdebugln("\tBecause " + this.currentQuestion.getExplanation());
        }
        printdebugln("You can now guess a word!");

        String contentText = "'" + this.currentQuestion.getLetter() + "' is correct'";
        if (this.currentQuestion.getExplanation() != null) {
            contentText += ", because " + this.currentQuestion.getExplanation();
        }
        messageLabel.setText(contentText + ". Click on a word to guess it!");
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("success");

        this.gridCtl.setEnableGuess(true, this);
    }


    private void OKPressed(ActionEvent event) {
        printdebugln(" > " + this.answer.getText());

        if (!Game.validLetter(this.answer.getText())) {
            displayError("Please enter a valid letter");
            return;
        }

        if (!this.currentQuestion.validate(this.answer.getText())) {
            displayWrongAnswerMessage();
            this.answer.setText("");
            this.setNewQuestion();
            return;
        } else {
            displayGoodAnswerMessage();
        }

        // The answer is good, we reveal the letter
        char c = Character.toUpperCase(this.answer.getText().charAt(0));
        Game.getInstance().getCurrentGrid().revealLetter(c);
        printdebugln(Game.getInstance().getCurrentGrid());
        this.gridCtl.updateGridPane();

        this.answer.setText("");
        this.answer.setVisible(false);
        this.question.setText("");
    }

    public void update(Observable obs, Object obj) {
        this.setNewQuestion();
        this.messageLabel.setText("");
    }
}

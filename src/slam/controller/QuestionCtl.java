package slam.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import slam.model.Game;
import slam.model.Question;

import java.util.Observable;
import java.util.Observer;

import static slam.Main.DEBUG;

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
        Button okButton = (Button) this.questionPane.lookup("#okButton");

        this.messageLabel = (Label) this.questionPane.lookup("#message");

        okButton.setOnAction(this::OKPressed);
        this.answer.setOnAction(this::OKPressed);
    }

    public void setNewQuestion() {
        this.currentQuestion = Game.getInstance().getCurrentGrid().getRandomQuestionForRemainingLetters();
        if (this.currentQuestion != null) {
            this.question.setText(this.currentQuestion.getQuestion());
            this.questionPane.setVisible(true);
        } else {
            this.answer.setText("");
            this.messageLabel.setText("");
            this.questionPane.setVisible(false);
        }
    }

    public void displayError(String s) {
        if (DEBUG) {
            System.err.println(s);
            System.out.print("Here is the question: \t" + this.currentQuestion);
        }

        messageLabel.setText(s);
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("critical-error");
    }

    private void displayWrongAnswerMessage() {
        if (DEBUG) {
            System.out.println("Wrong answer!");
            System.out.println("Correct answer was: " + this.currentQuestion.getLetter());

            if (this.currentQuestion.getExplanation() != null) {
                System.out.println("\tbecause " + this.currentQuestion.getExplanation());
            }
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
        if (DEBUG) {
            System.out.println("Correct!");
            if (this.currentQuestion.getExplanation() != null) {
                System.out.println("\tBecause " + this.currentQuestion.getExplanation());
            }
        }

        String contentText = "'" + this.currentQuestion.getLetter() + "' is correct'";
        if (this.currentQuestion.getExplanation() != null) {
            contentText += ", because " + this.currentQuestion.getExplanation();
        }
        messageLabel.setText(contentText + ".");
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("success");
    }


    private void OKPressed(ActionEvent event) {
        if( DEBUG) {
            System.out.println(" > "+this.answer.getText());
        }

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

        char c = Character.toUpperCase(this.answer.getText().charAt(0));
        Game.getInstance().getCurrentGrid().revealLetter(c);
        if( DEBUG) {
            System.out.println(Game.getInstance().getCurrentGrid());
        }

        this.gridCtl.updateGridPane();

        this.answer.setText("");
        this.setNewQuestion();
    }

    public void update(Observable obs, Object obj) {
        this.setNewQuestion();
        this.messageLabel.setText("");
    }
}

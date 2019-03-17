package slam.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import slam.model.Game;
import slam.model.Question;

import java.util.Observable;
import java.util.Observer;
import java.util.function.UnaryOperator;

import static slam.Main.printDebugLn;

public class QuestionCtl implements Observer {

    private Question currentQuestion;

    private final GridCtl gridCtl;

    private final VBox questionPane;
    private final Label question;
    private final TextField answer;
    private final Label messageLabel;
    private final Label messageDetail;


    public QuestionCtl(GridCtl gridCtl, VBox questionPane) {
        Game.getInstance().addObserver(this);

        this.gridCtl = gridCtl;

        this.questionPane = questionPane;
        this.question = (Label) this.questionPane.lookup("#question");
        this.answer = (TextField) this.questionPane.lookup("#answer");
        this.messageLabel = (Label) this.questionPane.lookup("#message");
        this.messageDetail = (Label) this.questionPane.lookup("#messageDetail");

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
        if( Game.getInstance().getCurrentGrid() == null) {
            this.messageLabel.setText("Please load data");
            this.questionPane.setVisible(true);
            this.question.setVisible(false);
            this.answer.setVisible(false);

            return;
        }

        this.question.setVisible(true);
        this.answer.setVisible(true);
        this.answer.setText("");
        this.messageLabel.setText("");

        this.currentQuestion = Game.getInstance().getCurrentGrid().getRandomQuestionForRemainingLetters();
        if (this.currentQuestion != null) {
            this.question.setText(this.currentQuestion.getQuestion());
            printDebugLn(this.currentQuestion.getQuestion());
        }
    }

    public void cleanMessage() {
        answer.setMaxHeight(Region.USE_COMPUTED_SIZE);
        answer.setMinHeight(Region.USE_COMPUTED_SIZE);

        messageLabel.setText("");

        messageDetail.setText("");
        messageDetail.setMaxHeight(0);
        messageDetail.setMinHeight(0);
    }
    private void displayWrongAnswerMessage() {
        printDebugLn("Wrong! Correct answer was: " + this.currentQuestion.getLetter());
        if (this.currentQuestion.getExplanation() != null) {
            printDebugLn("\tbecause " + this.currentQuestion.getExplanation());
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

        answer.setMaxHeight(0);
        answer.setMinHeight(0);

        printDebugLn("Correct!");
        if (this.currentQuestion.getExplanation() != null) {
            printDebugLn("\tBecause " + this.currentQuestion.getExplanation());
        }
        printDebugLn("You can now guess a word!");

        String contentText = "'" + this.currentQuestion.getLetter() + "' is correct'";
        if (this.currentQuestion.getExplanation() != null) {
            contentText += ", because " + this.currentQuestion.getExplanation();
        }
        messageLabel.setText(contentText);
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("success");

        messageDetail.setText("Click on a word to guess it!");
        messageDetail.setMaxHeight(Region.USE_COMPUTED_SIZE);
        messageDetail.setMinHeight(Region.USE_COMPUTED_SIZE);

        this.gridCtl.setEnableGuess(true, this);
    }


    public void showCongratulations() {
        messageLabel.setText("You guess the entire grid, congratulations!");
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("congratulations");
    }


    private void OKPressed(ActionEvent event) {
        printDebugLn(" > " + this.answer.getText());

        if (!Game.validLetter(this.answer.getText())) {
            printDebugLn("Please enter a valid letter");
            printDebugLn("Here is the question: \t" + this.currentQuestion);

            messageLabel.setText("Please enter a valid letter");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("critical-error");
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
        printDebugLn(Game.getInstance().getCurrentGrid());
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

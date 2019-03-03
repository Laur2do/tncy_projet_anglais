package slam.model;

public class Question {

    private String question;
    private char letter;
    private String explanation;

    public Question(String question, char letter, String explanation) {
        this.question = question;
        this.letter = letter;
        this.explanation = explanation;
    }

    public Question(String question, char letter) {
        this(question, letter, null);
    }

    public String getQuestion() {
        return this.question;
    }

    public char getLetter() {
        return this.letter;
    }

    public boolean validate(String answer) {
        return answer.toUpperCase().charAt(0) == this.letter;
    }

    public String getExplanation() {
        return this.explanation;
    }

    @Override
    public String toString() {
        return this.question;
    }
}

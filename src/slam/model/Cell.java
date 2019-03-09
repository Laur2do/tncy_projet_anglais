package slam.model;

import java.util.Observable;
import java.util.Observer;

public class Cell implements Observer {
    public static final char NO_LETTER_CHAR = '#';
    public static final char NOT_REVEALED_LETTER_CHAR = ' ';

    private final int wordIndex;
    private final GridWord word;
    private boolean revealed;

    public char getLetter() {
        return word.getLetter(wordIndex);
    }

    public Cell(GridWord w, int wordIndex) {
        this.word = w;
        this.wordIndex = wordIndex;
        this.revealed = false;
        w.addObserver(this);
    }

    public Cell(Cell c) {
        this.word = c.word;
        this.wordIndex = c.wordIndex;
        this.revealed = c.revealed;
        c.word.addObserver(this);
    }

    public GridWord getWord() {
        return this.word;
    }

    @Override
    public String toString() {
        if (revealed) {
            return String.valueOf(word.getLetter(wordIndex));
        } else {
            return String.valueOf(NOT_REVEALED_LETTER_CHAR);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == word) {
            revealed = word.getRevealedLetters()[wordIndex];
        } else {
            if (arg != null) {
                int index = (Integer) arg;
                GridWord gridWord = (GridWord) o;
                if (word.getLetter(wordIndex) == gridWord.getLetter(index)) {
                    if (index >= 0 && index < gridWord.getLength()) {
                        revealed = revealed || gridWord.getRevealedLetters()[index];
                        word.getRevealedLetters()[wordIndex] = revealed;
                    }
                }
            }
        }
    }
}

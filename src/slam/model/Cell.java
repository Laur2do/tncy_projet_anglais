package slam.model;

import java.util.Observable;
import java.util.Observer;

public class Cell implements Observer {
    public static final char NO_LETTER_CHAR = ' ';
    public static final char NOT_REVEALED_LETTER_CHAR = '*';

    private int wordIndex;
    private GridWord word;
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

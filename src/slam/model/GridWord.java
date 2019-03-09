package slam.model;

import java.util.Arrays;

public class GridWord extends Word {

    private final Orientation orientation;
    private final boolean[] revealedLetters;
    private boolean alreadyGuessed;

    private final int firstCharX;
    private final int firstCharY;

    public GridWord(Word word, Orientation orientation, int firstCharX, int firstCharY) {
        super(word);
        this.orientation = orientation;
        this.firstCharX = firstCharX;
        this.firstCharY = firstCharY;

        this.revealedLetters = new boolean[this.length];
        Arrays.fill(this.revealedLetters, false);
    }

    public boolean isAlreadyGuessed() {
        return alreadyGuessed;
    }

    public void setAlreadyGuessed() {
        this.alreadyGuessed = true;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getX() {
        return firstCharX;
    }

    public int getY() {
        return firstCharY;
    }

    public boolean[] getRevealedLetters() {
        return revealedLetters;
    }

    public boolean isRevealed() {
        for (boolean revealedLetter : revealedLetters) {
            if (!revealedLetter) {
                return false;
            }
        }
        return true;
    }

    public void revealLetter(char letter) {
        for (int i = 0; i < this.content.length(); i++) {
            if (this.content.charAt(i) == Character.toUpperCase(letter)) {
                this.revealedLetters[i] = true;
                setChanged();
                notifyObservers(i);
            }
        }
    }

    public void reveal() {
        for (int i = 0; i < this.content.length(); i++) {
            this.revealedLetters[i] = true;
            setChanged();
            notifyObservers(i);
        }
    }

    public void reset() {
        for (int i = 0; i < this.content.length(); i++) {
            this.revealedLetters[i] = false;
            setChanged();
            notifyObservers(i);
        }
    }

    public String getDefinitions() {
        StringBuilder sb = new StringBuilder();
        if(this.englishDefinition != null) {
            sb.append(this.englishDefinition);
        }
        if(this.frenchDefinition != null) {
            if(this.englishDefinition != null) {
                sb.append(" or, in french: '");
            }else {
                sb.append('\'');
            }
            sb.append(this.frenchDefinition);
            sb.append('\'');
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.content;
    }

}

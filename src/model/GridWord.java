package model;

import java.util.Arrays;

public class GridWord extends Word {

    private Orientation orientation;
    private boolean[] revealedLetters;

    private int firstCharX;
    private int firstCharY;

    public GridWord(Word word, Orientation orientation, int firstCharX, int firstCharY) {
        super(word);
        this.orientation = orientation;
        this.firstCharX = firstCharX;
        this.firstCharY = firstCharY;

        this.revealedLetters = new boolean[this.length];
        Arrays.fill(this.revealedLetters, false);
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


    public void revealLetter(int index) {
        assert (index > 0 && index < revealedLetters.length);
        this.revealedLetters[index] = true;
        setChanged();
        notifyObservers();
    }

    public void revealLetter(char letter) {
        for (int i = 0; i < this.content.length(); i++) {
            if (this.content.charAt(i) == Character.toUpperCase(letter)) {
                this.revealedLetters[i] = true;
            }
        }
        setChanged();
        notifyObservers();
    }

}

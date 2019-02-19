package model;

import java.util.Arrays;

public class GridWord extends Word{

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

    public void setRevealedLetters(boolean[] revealedLetters) {
        this.revealedLetters = revealedLetters;
    }

    //TODO : set revealed pour une ou plusieurs lettres (si un autre mot est découvert)


}

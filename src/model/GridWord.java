package model;

import java.util.Arrays;

public class GridWord extends Word{

    private Orientation orientation;
    private boolean[] revealedLetters;

    public GridWord(String word, String englishDefinition, String frenchDefinition, Orientation orientation) throws WordException{
        super(word, englishDefinition, frenchDefinition);
        this.orientation = orientation;
        this.revealedLetters = new boolean[this.length];
        Arrays.fill(this.revealedLetters, false);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public boolean[] getRevealedLetters() {
        return revealedLetters;
    }

    public void setRevealedLetters(boolean[] revealedLetters) {
        this.revealedLetters = revealedLetters;
    }

    //TODO : set revealed pour une ou plusieurs lettres (si un autre mot est découvert)


}

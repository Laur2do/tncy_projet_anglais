package model;

public class Cell {
    private char letter;

    public char getLetter() {
        return letter;
    }

    public Cell(char letter) {
        this.letter = letter;
    }

    public static final char NO_LETTER_CHAR = ' ';

    @Override
    public String toString() {
        return String.valueOf(letter);
    }
}

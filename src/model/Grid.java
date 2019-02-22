package model;

import java.util.ArrayList;

public class Grid {

    public static final int NUMBER_OF_WORDS = 10;
    private static final int MAX_GRID_WIDTH = 50;
    private static final int MAX_GRID_HEIGHT = 50;

    private Cell[][] grid;

    private ArrayList<GridWord> placedWords;

    private ArrayList<Word> wordsToPlace;

    public Grid() {
        this(MAX_GRID_WIDTH, MAX_GRID_HEIGHT);
    }

    public Grid(int width, int height) {
        grid = new Cell[width][height];
        placedWords = new ArrayList<>();
        wordsToPlace = new ArrayList<>();
    }

    @Override
    public String toString() {
        return gridToString(this.grid);
    }

    public int getWidth() {
        return grid[0].length;
    }

    public int getHeight() {
        return grid.length;
    }

    public String shortInfo() {
        return "Grid " + grid[0].length + "x" + grid.length + ", " + placedWords.size() + " words";
    }

    private static String gridToString(Cell[][] grid) {
        if (grid == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        sb.append("\t");
        for (int colName = 0; colName < grid.length; ++colName) {
            sb.append(colName);
            sb.append(' ');
        }
        sb.append('\n');

        for (int i = 0; i < grid.length; ++i) {
            sb.append(i);
            sb.append("\t");
            for (int j = 0; j < grid.length; ++j) {
                if (grid[j][i] == null) {
                    sb.append(Cell.NO_LETTER_CHAR);
                } else {
                    sb.append(grid[j][i].toString());
                }
                sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public boolean isRevealed() {
        for (GridWord gridWord : placedWords) {
            if (!gridWord.isRevealed()) {
                return false;
            }
        }
        return true;
    }

    public void revealLetter(char letter) {
        for (GridWord gridWord : placedWords) {
            gridWord.revealLetter(letter);
        }
    }

    public Question getRandomQuestionForRemainingLetters() {
        ArrayList<Character> remainingLetters = new ArrayList<>();
        for (GridWord gridWord : placedWords) {
            boolean[] revealedLetters = gridWord.getRevealedLetters();
            for (int i = 0; i < revealedLetters.length; i++) {
                if (!revealedLetters[i]) {
                    remainingLetters.add(gridWord.getLetter(i));
                }
            }
        }
        int index = (int)(Math.random()*remainingLetters.size());
        char letter = remainingLetters.get(index);
        return Game.getInstance().getRandomQuestionForLetter(letter);
    }

    public GridWord placeWord(Word w, Orientation direction, int firstCharX, int firstCharY) {
        int wordLength = w.getLength();
        if (direction == Orientation.VERTICAL && firstCharY + wordLength > grid.length ||
                direction == Orientation.HORIZONTAL && firstCharX + wordLength > grid[0].length) {
            return null;
        }
        GridWord gridWord = new GridWord(w, direction, firstCharX, firstCharY);
        for (int k = 0; k < wordLength; ++k) {
            if (direction == Orientation.VERTICAL) {
                if (grid[firstCharX][firstCharY + k] != null) {
                    if (grid[firstCharX][firstCharY + k].getLetter() == w.getLetter(k)) {
                        // The letter is already correct
                        continue;
                    } else {
                        return null;
                    }
                }
                grid[firstCharX][firstCharY + k] = new Cell(gridWord, k);
            } else {
                if (grid[firstCharX + k][firstCharY] != null) {
                    if (grid[firstCharX + k][firstCharY].getLetter() == w.getLetter(k)) {
                        // The letter is already correct
                        continue;
                    } else {
                        return null;
                    }
                }
                grid[firstCharX + k][firstCharY] = new Cell(gridWord, k);
            }
        }

        wordsToPlace.remove(w);
        placedWords.add(gridWord);

        return gridWord;
    }
}

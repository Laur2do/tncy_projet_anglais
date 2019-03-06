package slam.model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static slam.Main.printdebugln;

public class Grid {

    /**
     * The maximal number of words expected in a generated grid
     */
    private static final int MAX_WORDS_COUNT = 10;

    /**
     * The minimal number of words expected in a generated grid.
     * Algorithm will retry to place words randomly until this is matched.
     * TODO: adjust it according to the available words
     */
    private static final int MIN_WORDS_COUNT = 3;

    private static final int MAX_GRID_WIDTH = 50;
    private static final int MAX_GRID_HEIGHT = 50;

    private Cell[][] grid;

    private HashMap<Word, GridWord> placedWords;

    private ArrayList<Word> wordsToPlace;

    public Grid() {
        this(MAX_GRID_WIDTH, MAX_GRID_HEIGHT);
    }

    public Grid(int width, int height) {
        grid = new Cell[width][height];
        placedWords = new HashMap<>();
        wordsToPlace = new ArrayList<>();
    }

    @Override
    public String toString() {
        return gridToString(false);
    }

    public int getWidth() {
        return grid[0].length;
    }

    public int getHeight() {
        return grid.length;
    }

    public String shortInfo() {
        return "GridCtl " + grid[0].length + "x" + grid.length + ", " + placedWords.size() + " words";
    }

    public void reset() {
        for (GridWord word : placedWords.values()) {
            word.reset();
        }
    }

    /**
     * Creates a string representing the content of the grid, with hidden letters or not.
     * @param showUnrevealed Whether to show unrevealed letters or not
     * @return A String representing the grid
     */
    private String gridToString(boolean showUnrevealed) {
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
                    if( showUnrevealed ) {
                        sb.append(grid[j][i].getLetter());
                    } else {
                        sb.append(grid[j][i].toString());
                    }
                }
                sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public boolean isRevealed() {
        for (GridWord gridWord : placedWords.values()) {
            if (!gridWord.isRevealed()) {
                return false;
            }
        }
        return true;
    }

    public void revealLetter(char letter) {
        for (GridWord gridWord : placedWords.values()) {
            gridWord.revealLetter(letter);
        }
    }

    public Question getRandomQuestionForRemainingLetters() {
        ArrayList<Character> remainingLetters = new ArrayList<>();
        for (GridWord gridWord : placedWords.values()) {
            boolean[] revealedLetters = gridWord.getRevealedLetters();
            for (int i = 0; i < revealedLetters.length; i++) {
                if (!revealedLetters[i]) {
                    remainingLetters.add(gridWord.getLetter(i));
                }
            }
        }
        if (remainingLetters.isEmpty()) {
            return null;
        }
        int index = (int) (Math.random() * remainingLetters.size());
        char letter = remainingLetters.get(index);
        return Game.getInstance().getRandomQuestionForLetter(letter);
    }

    public ArrayList<GridWord> getNonRevealedWords() {
        ArrayList<GridWord> nonRevealedWords = new ArrayList<>();
        for (GridWord w : this.placedWords.values()) {
            if (!w.isRevealed()) {
                nonRevealedWords.add(w);
            }
        }
        return nonRevealedWords;
    }

    public Collection<GridWord> getPlacedWords() {
        return this.placedWords.values();
    }

    public GridWord placeWord(Word w, Orientation direction, int firstCharX, int firstCharY) {
        int wordLength = w.getLength();
        // Ensure the bounds of the grid are respected depending on the orientation
        if (direction == Orientation.VERTICAL && firstCharY + wordLength > grid.length ||
                direction == Orientation.HORIZONTAL && firstCharX + wordLength > grid[0].length) {
            return null;
        }
        GridWord gridWord = new GridWord(w, direction, firstCharX, firstCharY);

        // Placing each letter
        for (int k = 0; k < wordLength; ++k) {
            if (direction == Orientation.VERTICAL) {
                // If there isn't a defined Cell yet
                if (grid[firstCharX][firstCharY + k] == null) {
                    grid[firstCharX][firstCharY + k] = new Cell(gridWord, k);
                    continue;
                }

                if (grid[firstCharX][firstCharY + k].getLetter() != w.getLetter(k)) {
                    // The Cell is already set, but the letter is incompatible: early return
                    return null;
                }
                // The Cell has a matching letter, it should observe the new word
                gridWord.addObserver(grid[firstCharX][firstCharY + k]);

            } else {
                // If there isn't a defined Cell yet
                if (grid[firstCharX + k][firstCharY] == null) {
                    grid[firstCharX + k][firstCharY] = new Cell(gridWord, k);
                }

                gridWord.addObserver(grid[firstCharX + k][firstCharY]);
                if (grid[firstCharX + k][firstCharY].getLetter() != w.getLetter(k)) {
                    return null;
                }
                // The Cell has a matching letter, it should observe the new word
                gridWord.addObserver(grid[firstCharX + k][firstCharY]);
            }
        }

        // Word is placed correctly, we remove it from the words to place and save it in the placedWords
        wordsToPlace.remove(w);
        placedWords.put(w, gridWord);

        return gridWord;
    }

    /***************** METHODS FOR AUTOMATIC GRID GENERATION *************************/

    /**
     * Creates a copy of a grid (with new Cells) and return it.
     * @param grid the grid to copy
     * @return the copied grid
     */
    private static Cell[][] copyGrid(Cell[][] grid) {
        Cell[][] destgrid = new Cell[grid.length][grid.length];
        for (int i = 0; i < grid.length; ++i) {
            for (int j = 0; j < grid.length; ++j) {
                if (grid[i][j] != null) {
                    destgrid[i][j] = new Cell(grid[i][j]);
                }
            }
        }
        return destgrid;
    }

    /**
     * @return The longest word from remaining words to place
     */
    private Word getLongestWord() {
        Word longestWord = null;
        int maxLength = 0;
        for (Word w : wordsToPlace) {
            if (w.getLength() > maxLength) {
                maxLength += w.getLength();
                longestWord = w;
            }
        }
        return longestWord;
    }

    /**
     * Sets the grid of Cell to be big enough to contain the longest word
     * @see #getLongestWord()
     */
    private void resetGridToFitWords() {
        int maxLength = getLongestWord().getLength();
        maxLength *= 2;
        grid = new Cell[maxLength][maxLength];
    }

    /**
     * @return a random Orientation
     */
    private Orientation getRandomOrientation() {
        if (Math.random() > 0.5) {
            return Orientation.VERTICAL;
        } else {
            return Orientation.HORIZONTAL;
        }
    }

    /**
     * Removes a word from the placed words and add it to the words to place
     * @param w The word to "unplace"
     */
    private void unplaceWord(Word w) {
        wordsToPlace.add(w);
        placedWords.remove(w);
    }

    /**
     * Place a word randomly in the grid, independently of the grid content
     * @param w The word to "place"
     * @return The placed word or null
     */
    private GridWord placeWordRandomly(Word w) {
        Orientation direction = getRandomOrientation();
        int firstCharX, firstCharY;

        if (direction == Orientation.VERTICAL) {
            firstCharX = (int) (Math.random() * this.grid.length);
            firstCharY = (int) (Math.random() * (this.grid.length - w.getLength()));
        } else {
            firstCharX = (int) (Math.random() * (this.grid.length - w.getLength()));
            firstCharY = (int) (Math.random() * this.grid.length);
        }

        return placeWord(w, direction, firstCharX, firstCharY);
    }

    /**
     * Tries to place a random word across a given previous one, while matching already placed words.
     * Failure to do so means there is no way to place a word, meaning we could either stop here or "unplace" the previousWord.
     * @param previousWord The word across which to try to place a new word
     * @return true if a word can be placed successfully, false otherwise.
     * @see #placeWordRandomly(Word)
     * @see #unplaceWord(Word)
     */
    private boolean placeNextWords(GridWord previousWord) {
        if (wordsToPlace.isEmpty()) {
            return true;
        }

        boolean wordPlaced = false;

        while (!wordsToPlace.isEmpty()) {
            int randomIndex = (int) (Math.random() * (wordsToPlace.size() - 1));
            Word w = wordsToPlace.get(randomIndex);
            Pair<Integer, Integer> indexes = w.indexOfFirstCommonLetter(previousWord, 0);

            while (!wordPlaced && indexes != null) {
                int firstCharX, firstCharY;

                // Get the opposite direction of the previous word
                Orientation direction = Orientation.HORIZONTAL;
                if (previousWord.getOrientation() == Orientation.HORIZONTAL) {
                    direction = Orientation.VERTICAL;
                }

                if (direction == Orientation.VERTICAL) {
                    firstCharX = previousWord.getX() + indexes.getValue();
                    firstCharY = previousWord.getY() - indexes.getKey();
                } else {
                    firstCharX = previousWord.getX() - indexes.getKey();
                    firstCharY = previousWord.getY() + indexes.getValue();
                }
                if (firstCharX < 0 || firstCharY < 0) {
                    indexes = w.indexOfFirstCommonLetter(previousWord, indexes.getValue() + 1);
                    continue;
                }
                Cell[][] backupGrid = copyGrid(this.grid);
                printdebugln("Placing word '" + w + "' with word '" + previousWord + "' (on " + w.getLetter(indexes.getKey()) + ")(" + firstCharX + "," + firstCharY + ")");
                GridWord wig = placeWord(w, direction, firstCharX, firstCharY);

                if (wig == null) {
                    printdebugln("\t Can't place word, retrying");
                    indexes = w.indexOfFirstCommonLetter(previousWord, indexes.getValue() + 1);
                    this.grid = backupGrid;
                    continue;
                }
                printdebugln(gridToString(true));

                GridWord olderPreviousWord = previousWord;
                previousWord = wig;
                wordPlaced = placeNextWords(previousWord);
                if (!wordPlaced) {
                    unplaceWord(w);
                    previousWord = olderPreviousWord;
                    indexes = w.indexOfFirstCommonLetter(previousWord, indexes.getValue() + 1);
                    this.grid = backupGrid;

                    printdebugln("\t Previous word unplaced");
                    printdebugln(gridToString(true));
                }
            }
            if (indexes == null) {
                // Need to pick another word
                printdebugln("Incompatible word: '" + w + "'");
                printdebugln(gridToString(true));

                wordsToPlace.remove(w);
                break;
            }
        }

        if (!wordPlaced) {
            // Need to replace previous word
            printdebugln("Need to unplace previous word");
            printdebugln(gridToString(true));
            return false;
        }
        printdebugln("Done");
        return true;
    }

    public void compute() {
        boolean incorrect = true;
        while (incorrect) {
            ArrayList<Word> wordsList = new ArrayList<>(Game.getInstance().getWords().values());
            printdebugln("\n========================\nCreating new grid with: "+wordsList+"\n========================");
            for (int i = 0; i < Grid.MAX_WORDS_COUNT && wordsList.size() > 0; i++) {
                int randomIndex = (int) (Math.random() * wordsList.size());
                Word w = wordsList.remove(randomIndex);
                wordsToPlace.add(w);
            }
            resetGridToFitWords();
            placedWords = new HashMap<>();

            // Handle first word
            Word longestWord = getLongestWord();

            boolean allPlaced = false;
            while (!allPlaced) {
                Cell[][] backupGrid = copyGrid(this.grid);
                //WordInGrid firstWord = placeWordRandomly(this.grid, longestWord);
                GridWord firstWord = placeWordRandomly(longestWord);
                assert (firstWord != null);

                // Handle other wordsToPlace);
                if (!placeNextWords(firstWord)) {
                    unplaceWord(longestWord);
                    this.grid = backupGrid;
                } else {
                    allPlaced = true;
                }
            }
            /////////////////////////////////////////////////
            // Checking that the generated grid is correct //
            /////////////////////////////////////////////////

            // We have enough word
            incorrect = placedWords.size() < MIN_WORDS_COUNT;
            if( incorrect ) {
                continue;
            }

            // We don't have a word next to another one, in the same direction
            for (GridWord gw : this.placedWords.values()) {
                if (gw.getOrientation() == Orientation.HORIZONTAL) {
                    for (int i = gw.getX(); i < gw.getLength()+gw.getX() && i < this.grid.length; i++) {
                        if (gw.getY() > 0 && this.grid[i][gw.getY() - 1] != null) {
                            if (this.grid[i][gw.getY() - 1].getWord().getOrientation() == Orientation.HORIZONTAL) {
                                incorrect = true;
                                break;
                            }
                        }
                        if (gw.getY() < this.grid.length - 1 && this.grid[i][gw.getY() + 1] != null) {
                            if (this.grid[i][gw.getY() + 1].getWord().getOrientation() == Orientation.HORIZONTAL) {
                                incorrect = true;
                                break;
                            }
                        }
                    }
                } else {
                    for (int i = gw.getY(); i < gw.getLength(); i++) {
                        if (gw.getX() > 0 && this.grid[gw.getX() - 1][i] != null) {
                            if (this.grid[gw.getX() - 1][i].getWord().getOrientation() == Orientation.VERTICAL) {
                                incorrect = true;
                                break;
                            }
                        }
                        if (gw.getX() < this.grid.length - 1 && this.grid[gw.getX() + 1][i] != null) {
                            if (this.grid[gw.getX() + 1][i].getWord().getOrientation() == Orientation.VERTICAL) {
                                incorrect = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (incorrect) {
                printdebugln(gridToString(true));
                printdebugln("Resulting grid is incorrect, restarting with new words");
            }
        }
    }
}

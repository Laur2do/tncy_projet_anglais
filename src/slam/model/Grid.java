package slam.model;

import javafx.util.Pair;

import java.util.*;

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
    private static final int MIN_WORDS_COUNT = 4;

    private static final int MAX_GRID_WIDTH = 50;
    private static final int MAX_GRID_HEIGHT = 50;

    private Cell[][] grid;

    private HashMap<Word, GridWord> placedWords;

    private HashSet<Word> wordsToPlace;

    public Grid() {
        this(MAX_GRID_WIDTH, MAX_GRID_HEIGHT);
    }

    public Grid(int width, int height) {
        grid = new Cell[width][height];
        placedWords = new HashMap<>();
        wordsToPlace = new HashSet<>();
    }

    @Override
    public String toString() {
        return gridToString(false);
    }

    public int w() {
        return grid[0].length;
    }

    public int h() {
        return grid.length;
    }

    public String shortInfo() {
        return "GridCtl " + w() + "x" + h() + ", " + placedWords.size() + " words";
    }

    public void reset() {
        for (GridWord word : placedWords.values()) {
            word.reset();
        }
    }

    /**
     * Creates a string representing the content of the grid, with hidden letters or not.
     *
     * @param showUnrevealed Whether to show unrevealed letters or not
     * @return A String representing the grid
     */
    private String gridToString(boolean showUnrevealed) {
        if (grid == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        sb.append("\t");
        for (int colName = 0; colName < h(); ++colName) {
            sb.append(colName);
            sb.append(' ');
        }
        sb.append('\n');

        for (int i = 0; i < h(); ++i) {
            sb.append(i);
            sb.append("\t");
            for (int j = 0; j < w(); ++j) {
                if (grid[j][i] == null) {
                    sb.append(Cell.NO_LETTER_CHAR);
                } else {
                    if (showUnrevealed) {
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
            if( ! this.isRevealed()) {
                // Incoherent state: no question found but grid is not revealed
                System.err.println("No remaining unrevealed letter found for grid");
                System.err.println(gridToString(false));
                System.err.println(this.getNonRevealedWords());
                System.err.println(gridToString(true));
            }
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
        return do_placeWord(w, direction, firstCharX, firstCharY, true);
    }

    private GridWord internal_placeWord(Word w, Orientation direction, int firstCharX, int firstCharY) {
        return do_placeWord(w, direction, firstCharX, firstCharY, false);
    }

    private GridWord do_placeWord(Word w, Orientation direction, int firstCharX, int firstCharY, boolean ignoreWordsToPlace) {
        int wordLength = w.getLength();
        // Ensure the bounds of the grid are respected depending on the orientation
        if (firstCharX >= w() || firstCharY >= h() ||
                direction == Orientation.VERTICAL && firstCharY + wordLength > h() ||
                direction == Orientation.HORIZONTAL && firstCharX + wordLength > w()) {
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
        if (!this.wordsToPlace.contains(w) && !ignoreWordsToPlace) {
            throw new IllegalStateException("Error trying to place word " + w + " which is not to place: " + this.wordsToPlace);
        }
        if (this.placedWords.containsKey(w) && !ignoreWordsToPlace) {
            throw new IllegalStateException("Error trying to place word " + w + " which is already placed " + this.placedWords);
        }

        this.wordsToPlace.remove(w);
        this.placedWords.put(w, gridWord);

        return gridWord;
    }


    ///////////////////////////////////////////
    // METHODS FOR AUTOMATIC GRID GENERATION //
    ///////////////////////////////////////////

    /**
     * Creates a copy of a grid (with new Cells) and return it.
     *
     * @param grid the grid to copy
     * @return the copied grid
     */
    private static Cell[][] copyGrid(Cell[][] grid) {
        Cell[][] destgrid = new Cell[grid.length][grid[0].length];
        for (int i = 0; i < grid[0].length; ++i) {
            for (int j = 0; j < grid.length; ++j) {
                if (grid[j][i] != null) {
                    destgrid[j][i] = new Cell(grid[j][i]);
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
        for (Word w : this.wordsToPlace) {
            if (w.getLength() > maxLength) {
                maxLength += w.getLength();
                longestWord = w;
            }
        }
        return longestWord;
    }

    /**
     * Sets the grid of Cell to be big enough to contain the longest word
     *
     * @see #getLongestWord()
     */
    private void resetGridToFitWords() {
        int maxLength = getLongestWord().getLength();
        maxLength *= 2;
        this.grid = new Cell[maxLength][maxLength];
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
     *
     * @param w The word to "unplace"
     */
    private void unplaceWord(Word w) {
        if (this.wordsToPlace.contains(w)) {
            throw new IllegalStateException("Error trying to unplace word " + w + " which is already to placed: " + this.wordsToPlace);
        }
        if (!this.placedWords.containsKey(w)) {
            throw new IllegalStateException("Error trying to unplace word " + w + " which is not placed " + this.placedWords);
        }

        this.wordsToPlace.add(w);
        this.placedWords.remove(w);
    }

    /**
     * Place a word randomly in the grid, independently of the grid content
     *
     * @param w The word to "place"
     * @return The placed word or null
     */
    private GridWord placeWordRandomly(Word w) {
        Orientation direction = getRandomOrientation();
        int firstCharX, firstCharY;

        if (direction == Orientation.VERTICAL) {
            firstCharX = (int) (Math.random() * w());
            firstCharY = (int) (Math.random() * (h() - w.getLength()));
        } else {
            firstCharX = (int) (Math.random() * (w() - w.getLength()));
            firstCharY = (int) (Math.random() * h());
        }

        return internal_placeWord(w, direction, firstCharX, firstCharY);
    }

    private Pair<Pair<Word, GridWord>, Pair<Integer, Integer>> findCompatibleRemainingWord() {
        Pair<Integer, Integer> commonLetterIndexes = null;
        HashSet<Integer> triedWordIndexes = new HashSet<>(this.wordsToPlace.size());
        Word w = null;
        for (GridWord previousWord : this.placedWords.values()) {
            // As long as we didn't try everything unsuccessfully
            while (commonLetterIndexes == null && triedWordIndexes.size() != this.wordsToPlace.size()) {
                int randomIndex;
                // Find a non-already tried word
                do {
                    randomIndex = (int) Math.round(Math.random() * (this.wordsToPlace.size() - 1));
                } while (triedWordIndexes.contains(randomIndex));

                // Check if the word is correct
                w = (Word) this.wordsToPlace.toArray()[randomIndex];
                commonLetterIndexes = w.indexOfFirstCommonLetter(previousWord, 0);
                triedWordIndexes.add(randomIndex);
            }
            if (w != null && commonLetterIndexes != null) {
                return new Pair<>(new Pair<>(w, previousWord), commonLetterIndexes);
            }
        }
        return null;
    }

    /**
     * Tries to place a random word across a given previous one, while matching already placed words.
     * Failure to do so means there is no way to place a word, meaning we could either stop here or "unplace" the previousWord.
     *
     * @return true if a word can be placed successfully, false otherwise.
     * @see #placeWordRandomly(Word)
     * @see #unplaceWord(Word)
     */
    private boolean placeNextWords() {
        if (this.wordsToPlace.isEmpty()) {
            return true;
        }

        boolean wordPlaced = false;
        boolean incompatibleWord = false;

        Word w = null;

        // Place all remaining words while there isn't an incompatible one
        while (!this.wordsToPlace.isEmpty() && !incompatibleWord) {
            // Find a remaining word containing a common letter
            Pair<Pair<Word, GridWord>, Pair<Integer, Integer>> compatibleWord = findCompatibleRemainingWord();
            if (compatibleWord == null) {
                // There is no more compatible word
                return false;
            }
            Pair<Integer, Integer> commonLetterIndexes = compatibleWord.getValue();
            w = compatibleWord.getKey().getKey();
            GridWord previousWord = compatibleWord.getKey().getValue();

            wordPlaced = false;
            while (!wordPlaced && commonLetterIndexes != null) {
                int firstCharX, firstCharY;

                // Get the opposite direction of the previous word
                Orientation direction = Orientation.HORIZONTAL;
                if (previousWord.getOrientation() == Orientation.HORIZONTAL) {
                    direction = Orientation.VERTICAL;
                }

                if (direction == Orientation.VERTICAL) {
                    firstCharX = previousWord.getX() + commonLetterIndexes.getValue();
                    firstCharY = previousWord.getY() - commonLetterIndexes.getKey();
                } else {
                    firstCharX = previousWord.getX() - commonLetterIndexes.getKey();
                    firstCharY = previousWord.getY() + commonLetterIndexes.getValue();
                }
                if (firstCharX < 0 || firstCharY < 0 || firstCharX >= w() || firstCharY >= h()) {
                    commonLetterIndexes = w.indexOfFirstCommonLetter(previousWord, commonLetterIndexes.getValue() + 1);
                    continue;
                }
                Cell[][] backupGrid = copyGrid(this.grid);
                printdebugln("Placing word " + w + " with word " + previousWord + " (on " + w.getLetter(commonLetterIndexes.getKey()) + ")(" + firstCharX + "," + firstCharY + ")");
                GridWord gridWord = internal_placeWord(w, direction, firstCharX, firstCharY);

                if (gridWord == null) {
                    printdebugln("\t Can't place word, retrying");
                    printdebugln("\t Remaining to place: " + this.wordsToPlace);
                    commonLetterIndexes = w.indexOfFirstCommonLetter(previousWord, commonLetterIndexes.getValue() + 1);
                    this.grid = backupGrid;
                    continue;
                }
                printdebugln("\t Remaining to place: " + this.wordsToPlace);
                printdebugln(gridToString(true));

                GridWord olderPreviousWord = previousWord;
                previousWord = gridWord;
                wordPlaced = placeNextWords();
                if (!wordPlaced) {
                    unplaceWord(w);
                    previousWord = olderPreviousWord;
                    commonLetterIndexes = w.indexOfFirstCommonLetter(previousWord, commonLetterIndexes.getValue() + 1);
                    this.grid = backupGrid;

                    printdebugln("\t Previous word " + w + " unplaced");
                    printdebugln("\t Remaining to place: " + this.wordsToPlace);
                    printdebugln(gridToString(true));
                }
            }

            if (commonLetterIndexes == null) {
                // Selected word w can't be placed across previousWord Need to pick another word
                printdebugln("Incompatible word: " + w + " with " + previousWord);
                printdebugln(gridToString(true));
                incompatibleWord = true;
                printdebugln("\t Remaining to place: " + this.wordsToPlace);
            }
        }

        if (!wordPlaced) {
            // Need to replace previous word
            printdebugln("Impossible to place a word");
            return false;
        }
        printdebugln("Word " + w + " placed.");
        return true;
    }

    public void compute() {
        boolean incorrect = true;
        while (incorrect) {
            this.wordsToPlace.clear();
            this.placedWords.clear();

            ///////////////////////////////////////////
            // Generating the grid with random words //
            ///////////////////////////////////////////
            ArrayList<Word> wordsList = new ArrayList<>(Game.getInstance().getWords().values());
            for (int i = 0; i < Grid.MAX_WORDS_COUNT && wordsList.size() > 0; i++) {
                int randomIndex = (int) (Math.random() * wordsList.size());
                Word w = wordsList.remove(randomIndex);
                this.wordsToPlace.add(w);
            }
            printdebugln("\n========================\nCreating new grid with: " + this.wordsToPlace + "\n========================");
            resetGridToFitWords();


            // Handle first word
            Word longestWord = getLongestWord();

            boolean allPlaced = false;
            while (!allPlaced) {
                Cell[][] backupGrid = copyGrid(this.grid);
                GridWord firstWord = placeWordRandomly(longestWord);

                printdebugln("Placing first word " + longestWord + " (" + firstWord.getX() + "," + firstWord.getY() + ")");
                printdebugln(gridToString(true));

                // Handle other wordsToPlace);
                if (!placeNextWords()) {
                    this.placedWords = new HashMap<>();
                    wordsList = new ArrayList<>(Game.getInstance().getWords().values());
                    for (int i = 0; i < Grid.MAX_WORDS_COUNT && wordsList.size() > 0; i++) {
                        int randomIndex = (int) (Math.random() * wordsList.size());
                        Word w = wordsList.remove(randomIndex);
                        this.wordsToPlace.add(w);
                    }

                    this.grid = backupGrid;
                    printdebugln("\t First word unplaced");
                    printdebugln(gridToString(true));
                } else {
                    allPlaced = true;
                    printdebugln("\n========================\nGrid computed with: " + this.placedWords.keySet() + "\n========================");
                }
            }
            /////////////////////////////////////////////////
            // Checking that the generated grid is correct //
            /////////////////////////////////////////////////

            // We have enough word
            incorrect = placedWords.size() < MIN_WORDS_COUNT;
            if (incorrect) {
                printdebugln("Resulting grid is incorrect, restarting with new words");
                printdebugln(gridToString(true));
                continue;
            }

            // We don't have a word next to another one, in the same direction
            for (GridWord gw : this.placedWords.values()) {
                if (gw.getOrientation() == Orientation.HORIZONTAL) {
                    for (int i = gw.getX(); i < gw.getLength() + gw.getX() && i < w(); i++) {
                        if (gw.getY() > 0 && this.grid[i][gw.getY() - 1] != null) {
                            if (this.grid[i][gw.getY() - 1].getWord().getOrientation() == Orientation.HORIZONTAL) {
                                incorrect = true;
                                break;
                            }
                        }
                        if (gw.getY() < h() - 1 && this.grid[i][gw.getY() + 1] != null) {
                            if (this.grid[i][gw.getY() + 1].getWord().getOrientation() == Orientation.HORIZONTAL) {
                                incorrect = true;
                                break;
                            }
                        }
                    }
                } else {
                    for (int j = gw.getY(); j < gw.getLength() + gw.getY() && j < h(); j++) {
                        if (gw.getX() > 0 && this.grid[gw.getX() - 1][j] != null) {
                            if (this.grid[gw.getX() - 1][j].getWord().getOrientation() == Orientation.VERTICAL) {
                                incorrect = true;
                                break;
                            }
                        }
                        if (gw.getX() < w() - 1 && this.grid[gw.getX() + 1][j] != null) {
                            if (this.grid[gw.getX() + 1][j].getWord().getOrientation() == Orientation.VERTICAL) {
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

package slam.model.loader;

import slam.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.MissingResourceException;

public class DataLoader {

    /**
     * Loads a CSV file containing words definitions
     *
     * @param path Path to the file to load
     * @return The number of line correctly parsed
     * @throws InvalidWordFileException When the file is incorrect
     */
    public static int loadWordFile(String path) throws InvalidWordFileException {
        int lineNumber = 0;
        int loadedWords = 0;
        try {
            BufferedReader source_file = new BufferedReader(new FileReader(path));
            String line;

            // We kip the first line (it is the header)
            source_file.readLine();

            while ((line = source_file.readLine()) != null) {
                lineNumber++;

                String[] tabStrings = line.split(",");
                switch (tabStrings.length) {
                    case 0:
                    case 1:
                        throw new InvalidWordFileException(path, lineNumber, line);
                    case 2:
                        try {
                            Word w1 = new Word(tabStrings[0], tabStrings[1], null);
                            Game.getInstance().addWord(path, w1);
                            loadedWords++;
                        } catch (WordException e) {
                            System.err.println("Tried to create an invalid content with Word(" + tabStrings[0] + ", " + tabStrings[1] + ", null)");
                        }
                        break;
                    case 3:
                        try {
                            Word w2 = new Word(tabStrings[0], tabStrings[1], tabStrings[2]);
                            Game.getInstance().addWord(path, w2);
                            loadedWords++;
                        } catch (WordException e) {
                            System.err.println("Tried to create an invalid content with Word(" + tabStrings[0] + ", " + tabStrings[1] + ", " + tabStrings[2] + ")");
                        }
                }

            }
            source_file.close();
        } catch (IOException e) {
            System.err.println("Read error on file " + path);
            e.printStackTrace();
        }
        return loadedWords;
    }

    /**
     * Loads a CSV file containing a grid
     *
     * @param path Path to the file to load
     * @throws InvalidGridFileException When the file is incorrect
     */
    public static void loadGridFile(String path) throws InvalidGridFileException {
        HashMap<String, Word> gameWords = Game.getInstance().getWords();
        if (gameWords.size() == 0) {
            throw new MissingResourceException("Can't load GridCtl if words aren't loaded", "Game", "listOfWords");
        }
        Grid grid;
        try {
            BufferedReader source_file = new BufferedReader(new FileReader(path));
            String line;
            int lineNumber = 0;

            //The first line contains the header and the grid dimensions
            String[] info = source_file.readLine().split(",");
            if (info.length == 6) {
                grid = new Grid(Integer.valueOf(info[4]), Integer.valueOf(info[5]));
            } else {
                grid = new Grid();
            }

            while ((line = source_file.readLine()) != null) {
                lineNumber++;

                String[] tabStrings = line.split(",");

                if (tabStrings.length != 4) {
                    throw new InvalidGridFileException(path, lineNumber, line, "Each line in a GridCtl file must contain content,orientation,start_x,start_y");
                }
                tabStrings[0] = tabStrings[0].toUpperCase();
                tabStrings[1] = tabStrings[1].toUpperCase();
                if (!gameWords.containsKey(tabStrings[0])) {
                    throw new InvalidGridFileException(path, lineNumber, line, "Word is not known by the Game");
                }
                Orientation orientation = Orientation.VERTICAL;
                if (tabStrings[1].equals("HORIZONTAL")) {
                    orientation = Orientation.HORIZONTAL;
                } else if (!tabStrings[1].equals("VERTICAL")) {
                    throw new InvalidGridFileException(path, lineNumber, line, "Second item must be an orientation: horizontal or vertical");
                }
                try {
                    int startX = Integer.valueOf(tabStrings[2]),
                            startY = Integer.valueOf(tabStrings[3]);
                    if (startX < 0 || startY < 0) {
                        throw new InvalidGridFileException(path, lineNumber, line, "Third & fourth items must be positive integers.");
                    }
                    if (startX >= grid.w() || startY >= grid.h()) {
                        throw new InvalidGridFileException(path, lineNumber, line, "Third & fourth items must be lower than the grid's dimensions");
                    }
                    GridWord word = grid.placeWord(gameWords.get(tabStrings[0]), orientation, startX, startY);
                    if (word == null) {
                        throw new InvalidGridFileException(path, lineNumber, line, "Placing word is incompatible with already placed words");
                    }
                } catch (NumberFormatException nbe) {
                    throw new InvalidGridFileException(path, lineNumber, line, "Third & fourth items must be valid integers");
                }
            }

            Game.getInstance().addGrid(grid);

        } catch (IOException e) {
            System.err.println("Read error on file " + path);
            e.printStackTrace();
        }
    }

    /**
     * Loads a CSV file containing series of questions of which answers are letter
     *
     * @param path Path to the file to load
     * @return The number of line correctly parsed
     * @throws InvalidQuestionFileException When the file is incorrect
     */
    public static int loadQuestionFile(String path) throws InvalidQuestionFileException {
        int lineNumber = 0;
        try {
            BufferedReader source_file = new BufferedReader(new FileReader(path));
            String line;

            // We kip the first line (it is the header)
            source_file.readLine();

            while ((line = source_file.readLine()) != null) {
                lineNumber++;

                String[] tabStrings = line.split(",");

                if (tabStrings.length < 2) {
                    throw new InvalidQuestionFileException(path, lineNumber, line, "Each line in a Question file must contain a Question, an answer and an optional explanation");
                }

                if (!Game.validLetter(tabStrings[1])) {
                    throw new InvalidQuestionFileException(path, lineNumber, line, "Second item must be a letter");
                }
                char letter = tabStrings[1].toUpperCase().charAt(0);
                String explanation = null;
                if (tabStrings.length > 2 && tabStrings[2].length() > 1) {
                    explanation = tabStrings[2];
                }
                Game.getInstance().addQuestion(new Question(tabStrings[0], letter, explanation));
            }

        } catch (IOException e) {
            System.err.println("Read error on file " + path);
            e.printStackTrace();
        }

        return lineNumber;
    }


}

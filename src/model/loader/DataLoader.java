package model.loader;

import model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;

public class DataLoader {

    /**
     * Loads a CSV file containing words definitions
     * @param path Path to the file to load
     * @return The number of line correctly parsed
     * @throws InvalidWordFileException When the file is incorrect
     */
    public static int loadCSVFile(String path) throws InvalidWordFileException {
        int lineNumber = 0;
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
                            Game.getInstance().addWord(w1);
                        }catch(WordException e) {
                            System.err.println("Tried to create an invalid content with Word("+tabStrings[0]+", "+tabStrings[1]+", null)");
                        }
                        break;
                    case 3:
                        try {
                            Word w2 = new Word(tabStrings[0], tabStrings[1], tabStrings[2]);
                            Game.getInstance().addWord(w2);
                        }catch(WordException e) {
                            System.err.println("Tried to create an invalid content with Word("+tabStrings[0]+", "+tabStrings[1]+", "+tabStrings[2]+")");
                        }
                }

            }
            source_file.close();
        } catch (IOException e) {
            System.err.println("Read error on file "+path);
            e.printStackTrace();
        }
        return lineNumber;
    }

    /**
     * Loads a text file containing a grid
     * @param path Path to the file to load
     * @throws InvalidGridFileException When the file is incorrect
     */
    public static void loadGridFile(String path) throws InvalidGridFileException {
        HashMap<String, Word> gameWords = Game.getInstance().getWords();
        if(gameWords.size() == 0 ) {
            throw new MissingResourceException("Can't load Grid if words aren't loaded", "Game", "listOfWords");
        }
        Grid grid = new Grid();
        try {
            BufferedReader source_file = new BufferedReader(new FileReader(path));
            String line;
            int lineNumber = 0;

            // We kip the first line (it is the header)
            source_file.readLine();

            while ((line = source_file.readLine()) != null) {
                lineNumber++;

                String[] tabStrings = line.split(",");

                if(tabStrings.length != 4) {
                    throw new InvalidGridFileException(path, lineNumber, line, "Each line in a Grid file must contain content,orientation,startx,starty");
                }
                if(! gameWords.containsKey(tabStrings[0])) {
                    throw new InvalidGridFileException(path, lineNumber, line, "Word is not known by the Game");
                }
                Orientation orientation = Orientation.VERTICAL;
                if(tabStrings[1].toUpperCase().equals("HORIZONTAL")) {
                    orientation = Orientation.HORIZONTAL;
                } else if (! tabStrings[1].toUpperCase().equals("VERTICAL") ) {
                    throw new InvalidGridFileException(path, lineNumber, line, "Second item must be an orientation: horizontal or vertical");
                }
                try {
                    int startX = Integer.valueOf(tabStrings[2]),
                        startY = Integer.valueOf(tabStrings[3]);
                    if( startX < 0 || startY < 0) {
                        throw new InvalidGridFileException(path, lineNumber, line, "Third & fourth items must be positive integers.");
                    }
                    GridWord word = grid.placeWord(gameWords.get(tabStrings[0]), orientation, startX, startY);
                    if( word == null) {
                        throw new InvalidGridFileException(path, lineNumber, line, "Placing word is incompatible with already placed words");
                    }
                }catch(NumberFormatException nbe){
                    throw new InvalidGridFileException(path, lineNumber, line, "Third & fourth items must be valid integers");
                }
            }

        } catch (IOException e) {
            System.err.println("Read error on file "+path);
            e.printStackTrace();
        }
    }


}

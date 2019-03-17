package slam.model.loader;

import slam.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DataLoader {

    /**
     * Loads a CSV file containing words definitions
     *
     * @param path Path to the file to load
     * @return The number of line correctly parsed
     * @throws InvalidWordFileException When the file is incorrect
     */
    public static int loadWordFile(String path, boolean checkonly) throws InvalidWordFileException {
        int lineNumber = 1;
        int loadedWords = 0;
        try {
            BufferedReader source_file = new BufferedReader(new FileReader(path));
            String line;

            // We kip the first line (it is the header)
            source_file.readLine();

            while ((line = source_file.readLine()) != null) {
                lineNumber++;
                if( line.isEmpty()) {
                    continue;
                }

                String[] tabStrings = line.split(",");
                switch (tabStrings.length) {
                    case 0:
                    case 1:
                        throw new InvalidWordFileException(path, lineNumber, line);
                    case 2:
                        try {
                            Word w1 = new Word(tabStrings[0], tabStrings[1], null);
                            if( ! checkonly) {
                                Game.getInstance().addWord(new File(path).getName(), w1);
                            }
                            loadedWords++;
                        } catch (WordException e) {
                            System.err.println("Tried to create an invalid content with Word(" + tabStrings[0] + ", " + tabStrings[1] + ", null)");
                        }
                        break;
                    case 3:
                        try {
                            Word w2 = new Word(tabStrings[0], tabStrings[1], tabStrings[2]);
                            if( ! checkonly) {
                                Game.getInstance().addWord(new File(path).getName(), w2);
                            }
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
     * Loads a CSV file containing series of questions of which answers are letter
     *
     * @param path Path to the file to load
     * @return The number of line correctly parsed
     * @throws InvalidQuestionFileException When the file is incorrect
     */
    public static int loadQuestionFile(String path) throws InvalidQuestionFileException {
        int lineNumber = 1;
        try {
            BufferedReader source_file = new BufferedReader(new FileReader(path));
            String line;

            // We kip the first line (it is the header)
            source_file.readLine();

            while ((line = source_file.readLine()) != null) {
                lineNumber++;
                if( line.isEmpty()) {
                    continue;
                }

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

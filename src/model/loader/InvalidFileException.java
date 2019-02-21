package model.loader;

import java.text.ParseException;

public class InvalidFileException extends ParseException {

    public InvalidFileException(String filepath, int lineNumber, String lineContent) {
        super("Invalid file " + filepath + ":" + lineNumber + "\n\t" + lineContent, lineNumber);
    }

    public InvalidFileException(String filepath, int lineNumber, String lineContent, String comment) {
        super("Invalid file " + filepath + ":" + lineNumber + "\n\t" + lineContent + "\n\t\\-> " + comment, lineNumber);
    }
}

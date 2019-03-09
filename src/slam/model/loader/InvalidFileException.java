package slam.model.loader;

import java.text.ParseException;

public class InvalidFileException extends ParseException {

    protected InvalidFileException(String filePath, int lineNumber, String lineContent) {
        super("Invalid file " + filePath + ":" + lineNumber + "\n\t" + lineContent, lineNumber);
    }

    protected InvalidFileException(String filePath, int lineNumber, String lineContent, String comment) {
        super("Invalid file " + filePath + ":" + lineNumber + "\n\t" + lineContent + "\n\t\\-> " + comment, lineNumber);
    }
}

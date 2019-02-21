package model.loader;

public class InvalidWordFileException extends InvalidFileException {
    public InvalidWordFileException(String filepath, int lineNumber, String lineContent) {
        super(filepath, lineNumber, lineContent);
    }

    public InvalidWordFileException(String filepath, int lineNumber, String lineContent, String comment) {
        super(filepath, lineNumber, lineContent, comment);
    }
}

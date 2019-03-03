package slam.model.loader;

public class InvalidGridFileException extends InvalidFileException {
    public InvalidGridFileException(String filepath, int lineNumber, String lineContent, String comment) {
        super(filepath, lineNumber, lineContent, comment);
    }
}

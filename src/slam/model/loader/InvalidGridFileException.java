package slam.model.loader;

public class InvalidGridFileException extends InvalidFileException {
    public InvalidGridFileException(String filePath, int lineNumber, String lineContent, String comment) {
        super(filePath, lineNumber, lineContent, comment);
    }
}

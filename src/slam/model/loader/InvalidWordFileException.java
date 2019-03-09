package slam.model.loader;

public class InvalidWordFileException extends InvalidFileException {
    public InvalidWordFileException(String filePath, int lineNumber, String lineContent) {
        super(filePath, lineNumber, lineContent);
    }
}

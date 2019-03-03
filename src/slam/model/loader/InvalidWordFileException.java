package slam.model.loader;

public class InvalidWordFileException extends InvalidFileException {
    public InvalidWordFileException(String filepath, int lineNumber, String lineContent) {
        super(filepath, lineNumber, lineContent);
    }
}

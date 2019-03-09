package slam.model.loader;

public class InvalidQuestionFileException extends InvalidFileException {
    public InvalidQuestionFileException(String filePath, int lineNumber, String lineContent, String comment) {
        super(filePath, lineNumber, lineContent, comment);
    }
}

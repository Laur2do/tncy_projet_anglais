package slam.model.loader;

public class InvalidQuestionFileException extends InvalidFileException {
    public InvalidQuestionFileException(String filepath, int lineNumber, String lineContent) {
        super(filepath, lineNumber, lineContent);
    }

    public InvalidQuestionFileException(String filepath, int lineNumber, String lineContent, String comment) {
        super(filepath, lineNumber, lineContent, comment);
    }
}

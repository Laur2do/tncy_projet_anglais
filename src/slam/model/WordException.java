package slam.model;

public class WordException extends Exception {

    public WordException(String word) {
        super("The content \" " + word + "\" doesn't have any definition, it will not be used for the game");
    }
}

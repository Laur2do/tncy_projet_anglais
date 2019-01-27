package model;

public class WordException extends Exception {

    public WordException(String word){
        super("The word \" " + word + "\" doesn't have any definition, it will not be used for the game");
    }
}

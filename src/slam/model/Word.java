package slam.model;

import java.util.Observable;

public class Word extends Observable {

    protected int length;
    protected String content;
    protected String englishDefinition;
    protected String frenchDefinition;

    /**
     * Word constructor. At least one definition must be known, otherwise the content cannot be used to play
     *
     * @param content           the content itself
     * @param englishDefinition english definition of the content, if unknown, let the case empty in the sheet
     * @param frenchDefinition  french definition of the content, if unknown, let the case empty in the sheet
     */
    public Word(String content, String englishDefinition, String frenchDefinition) throws WordException {
        init(content, englishDefinition, frenchDefinition);
    }

    public static boolean validWord(String s) {
        for(char c : s.toUpperCase().toCharArray()) {
            if( ! Game.validLetter(c) ){
                return false;
            }
        }
        return true;
    }

    private void init(String word, String englishDefinition, String frenchDefinition) throws WordException {
        if( ! validWord(word) ) {
            throw new WordException(word);
        }
        this.content = word.toUpperCase();

        this.length = this.content.length();

        if ((englishDefinition == null || englishDefinition.isEmpty()) && (frenchDefinition == null || frenchDefinition.isEmpty())) {
            throw new WordException(this.content);
        }
        if (englishDefinition == null || englishDefinition.isEmpty()) {
            this.englishDefinition = null;
        } else {
            this.englishDefinition = englishDefinition;
        }
        if (frenchDefinition == null || frenchDefinition.isEmpty()) {
            this.frenchDefinition = null;
        } else {
            this.frenchDefinition = frenchDefinition;
        }
    }

    public Word(Word content) {
        try {
            init(content.content, content.englishDefinition, content.frenchDefinition);
        } catch (WordException e) {
            System.err.println("Creating a Word from a Word caused an exception");
            System.exit(-1);
        }
    }


    public int getLength() {
        return length;
    }

    public String getContent() {
        return content;
    }

    public char getLetter(int i) {
        return content.charAt(i);
    }


    public String getEnglishDefinition() {
        return englishDefinition;
    }

    public String getFrenchDefinition() {
        return frenchDefinition;
    }

    @Override
    public String toString() {
        return (this.content + ", " + this.length + ", " + this.englishDefinition + ", " + this.frenchDefinition);
    }

}

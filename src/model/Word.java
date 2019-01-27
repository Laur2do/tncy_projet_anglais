package model;

import java.util.Arrays;

public class Word{

    protected int length;
    protected String word;
    protected String englishDefinition;
    protected String frenchDefinition;

    /**
     * Word constructor. At least one definition must be known, otherwise the word cannot be used to play
     * @param word the word itself
     * @param englishDefinition english definition of the word, if unknown, let the case empty in the sheet
     * @param frenchDefinition french definition of the word, if unknown, let the case empty in the sheet
     */
    public Word(String word, String englishDefinition, String frenchDefinition) throws WordException{
        this.word = word;
        this.length = this.word.length();

        if (englishDefinition.isEmpty() && frenchDefinition.isEmpty()){
            throw new WordException(this.word);
        }
        if (englishDefinition.isEmpty()){
            this.englishDefinition = null;
        } else {
            this.englishDefinition = englishDefinition;
        }
        if (frenchDefinition.isEmpty()){
            this.frenchDefinition = null;
        } else {
            this.frenchDefinition = frenchDefinition;
        }
    }

    public int getLength() {
        return length;
    }

    public String getWord() {
        return word;
    }

    public String getEnglishDefinition() {
        return englishDefinition;
    }


    public String getFrenchDefinition() {
        return frenchDefinition;
    }

    public String toString(){
        return (this.word + ", " + this.length + ", " + this.englishDefinition + ", " + this.frenchDefinition);
    }

}

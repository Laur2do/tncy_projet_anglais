package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    private static Game instance;
    private ArrayList<Grid> listOfGrids;
    private ArrayList<Word> listofWords;

    private Game() {
        this.listOfGrids = new ArrayList<>();
        this.listofWords = new ArrayList<>();

    }

    public static Game getInstance(){
        if (instance == null){
            instance = new Game();
        }
        return instance;
    }

    public ArrayList<Grid> getListOfGrids() {
        return listOfGrids;
    }

    public void addGrid(Grid grid){
        this.listOfGrids.add(grid);
    }

   public void addWord(Word word){
        this.listofWords.add(word);
   }

    public ArrayList<Word> getListofWords() {
        return listofWords;
    }

    public void loadData(String path){
        try {
            DataLoader.loadCSVFile(path);
        } catch (Exception e){}


    }


}

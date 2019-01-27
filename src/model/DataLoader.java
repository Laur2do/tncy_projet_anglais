package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataLoader {

    public static void loadCSVFile(String path) throws Exception{
        try {
            BufferedReader source_file = new BufferedReader(new FileReader(path));
            String line;

            while((line = source_file.readLine())!= null) {

                String[] tabChaine = line.split(",");
                switch (tabChaine.length){
                    case 0 :
                    case 1 :
                        throw new Exception();
                    case 2 :
                        Word w1 = new Word(tabChaine[0], tabChaine[1], null);
                        Game.getInstance().addWord(w1);
                        break;
                    case 3 :
                        Word w2 = new Word(tabChaine[0], tabChaine[1], tabChaine[2]);
                        Game.getInstance().addWord(w2);
                }

            }
            source_file.close();
        }
        catch (IOException e) {
            System.out.println("Le fichier est introuvable !");
        }
    }



}

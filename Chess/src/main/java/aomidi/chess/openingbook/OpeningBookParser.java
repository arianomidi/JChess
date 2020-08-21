package aomidi.chess.openingbook;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Arrays;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files


public class OpeningBookParser {

    private static OpeningBook openingBook = new OpeningBook();

    public static void parseFile(){
        try {
            File openingsFile = new File("./resources/openings.cvs");
            Scanner myReader = new Scanner(openingsFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                if (data.length() > 0 && data.charAt(0) == '-') {
                    addToOpeningBook(data);
//                    System.out.println(data);
                }

            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void addToOpeningBook(String opening){
        List<String> moves = Arrays.asList(opening.split(" "));

        // first index is a dash
        for (int i = 1; i < moves.size(); i++){
            openingBook.addNode(moves.get(i));
        }

        openingBook.resetCurMove();
    }

    public static OpeningBook getOpeningBook(){
        return  openingBook;
    }

    public static void main(String[] args) {
        OpeningBookParser.parseFile();
    }

}

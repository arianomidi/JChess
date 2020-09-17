package aomidi.chess.model.pgn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PGNHandler {

    public static void writePGNFile(PGNHolder pgnHolder){
        try {
            File pgnFile = pgnHolder.getPGNFile();

            if (!pgnFile.getName().toLowerCase().endsWith(".pgn")) {
                pgnFile = new File(pgnFile.getParentFile(), pgnFile.getName() + ".pgn");
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(pgnFile));
            writer.write(pgnHolder.getPGNText());
            writer.close();
        } catch (IOException e){
            e.getMessage();
        }
    }


}

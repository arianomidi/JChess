package aomidi.chess.ai.openingbook;

import com.github.bhlangonijr.chesslib.move.Move;
import org.graalvm.compiler.nodes.calc.IntegerDivRemNode;

import java.util.Arrays;
import java.util.List;

public class OpeningBookEncoder {

    // --- ENCODING --- //

    public static String encodeOpeningBook(OpeningBook openingBook){
        return encode(openingBook.getRoot());
    }

    private static String getNodeText(MoveNode node){
        if (node.getMove() == null)
            return "";
        return "[" + node.getMove().toString() + "|" + node.getOpeningName() + "|" + node.getWeight() + "]";
    }

    private static String encode(MoveNode root){
        String nodeText = getNodeText(root) + "{";

        for (MoveNode child : root.getMovesList()){
            nodeText += encode(child);
        }

        return nodeText + "}";
    }

    // --- DECODING --- //

    public static OpeningBook decodeOpeningBook(String encodedOpeningBook){
        OpeningBook openingBook = new OpeningBook();

        StringBuilder nodeData = new StringBuilder();

        for (int i=1; i < encodedOpeningBook.length() - 1; i++){
            if (encodedOpeningBook.charAt(i) == '{'){
                MoveNode node = decodeNode(nodeData.toString(), openingBook.getCur_move());
                openingBook.addNode(node);

                nodeData = new StringBuilder();
            } else if (encodedOpeningBook.charAt(i) == '}'){
                openingBook.undoMove();
            } else {
                nodeData.append(encodedOpeningBook.charAt(i));
            }
        }

        return openingBook;
    }

    private static MoveNode decodeNode(String nodeString, MoveNode parent){
        nodeString = nodeString.substring(1, nodeString.length() - 1);
        List<String> nodeData = Arrays.asList(nodeString.split("\\|"));

        return new MoveNode(new Move(nodeData.get(0)), nodeData.get(1),  Integer.parseInt(nodeData.get(2)), parent);
    }

}

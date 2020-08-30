package aomidi.chess.ai.openingbook;

import java.util.ArrayList;

public class MoveNode {
    private String move;
    private MoveNode parent;
    private ArrayList<MoveNode> movesList;

    public MoveNode(String move, MoveNode parent){
        this.move = move;
        this.parent = parent;
        this.movesList = new ArrayList<>();
    }

    public void addToMoveList(MoveNode node){
        movesList.add(node);
    }

    public MoveNode addToMoveList(String move){
        MoveNode node = getMoveNode(move);

        if (node == null) {
            node = new MoveNode(move, this);
            movesList.add(node);
        }


        return node;
    }

    public MoveNode getNodeFromMoveList(String move){
        for (MoveNode node : movesList){
            if (node.getMove().compareTo(move) == 0){
                return node;
            }
        }
        return null;
    }

    public boolean moveListHas(String move){
        for (MoveNode node : movesList){
            if (node.getMove().compareTo(move) == 0){
                return true;
            }
        }
        return false;
    }

    public MoveNode getMoveNode(String move){
        for (MoveNode node : movesList){
            if (node.getMove().compareTo(move) == 0){
                return node;
            }
        }
        return null;
    }

    public String getMove() {
        return move;
    }

    public MoveNode getParent() {
        return parent;
    }

    public ArrayList<MoveNode> getMovesList() {
        return movesList;
    }

}

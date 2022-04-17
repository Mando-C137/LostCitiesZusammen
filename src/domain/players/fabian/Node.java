package domain.players.fabian;

import domain.players.fabian.domain.MoveSet;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private final Node father;
    private final List<Node> children;

    private double winCount = 0;
    private int visitCount = 0;
    private int availabilityCount = 0;

    // Coefficient c for exploration term.
    private final double CONSTANT_C = 0.7;

    // Which move was executed to reach this node.
    private final MoveSet move;

    // Was the last move executed by the player.
    private final boolean ownMove;

    // Which moves were with the current determinzation legal.
    private List<MoveSet> currentLegalMoves;

    // root constructor
    public Node() {
        this.father = null;
        this.children = new ArrayList<>();
        this.move = null;
        this.ownMove = false;
    }

    // child constructor
    public Node(Node father, MoveSet move, boolean ownMove) {
        this.father = father;
        this.move = move;
        this.children = new ArrayList<>();
        this.ownMove = ownMove;
    }

    public Node expand(MoveSet newMove) {
        Node child = new Node(this, newMove, !this.ownMove);
        this.children.add(child);

        return child;
    }

    public void fusionWithOther(Node other) {
        List<Node> otherChildren = other.getChildren();
        for (int i = 0; i < otherChildren.size(); i++) {
            boolean found = false;
            for (int j = 0; j < this.children.size(); j++) {
                if (this.children.get(j).getMove().equals(otherChildren.get(i).getMove())) {
                    found = true;
                    this.children.get(j).updateVisit(otherChildren.get(i).getVisitCount());
                    this.children.get(j).updateAvailability(otherChildren.get(i).getAvailabilityCount());
                    this.children.get(j).updateWin(otherChildren.get(i).getWin());
                    break;
                }
            }
            if (!found) {
                this.children.add(otherChildren.get(i));
            }
        }
    }

    /**
     * Checks if one of the available moves lead to an unexplored node.
     */
    public MoveSet searchUnexploredNode(List<MoveSet> legalMoves) {
        for (MoveSet move : legalMoves) {
            boolean found = false;
            for (Node child : this.children) {
                if (child.getMove().equals(move)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return move;
            }
        }

        return null;
    }

    /**
     * Determines the "legal child" with the best UCT.
     */
    public Node determineBestUCTChild(List<MoveSet> legalMoves) {
        int bestUCT = -1;
        double currentUCT = -100.0;
        for (MoveSet move : legalMoves) {
            for (int i = 0; i < this.children.size(); i++) {
                if (this.children.get(i).getMove().equals(move)) {
                    if (this.children.get(i).calculateUCT() > currentUCT) {
                        bestUCT = i;
                        currentUCT = this.children.get(i).calculateUCT();
                    }
                    // It is easier to increase the availability counts here instead during the backpropagation.
                    //this.children.get(i).increaseAvailability();
                    break;
                }
            }
        }

        return this.children.get(bestUCT);
    }

    /**
     * Determines the node with the most visits and therefore the chosen move.
     */
    public MoveSet determineMoveSet() {
        int node = 0;
        int mostVisits = this.children.get(0).getVisitCount();
        for (int i = 1; i < this.children.size(); i++) {
            if (this.children.get(i).getVisitCount() > mostVisits) {
                node = i;
                mostVisits = this.children.get(i).getVisitCount();
            }
        }

        return this.children.get(node).getMove();
    }

    /**
     * Calculates the UCT of itself.
     */
    public double calculateUCT() {
        if (this.ownMove) {
            // UCT by player's view
            return (this.winCount / (double)this.visitCount) + this.CONSTANT_C * Math.sqrt(Math.log(this.availabilityCount)/(double)this.visitCount);
        }

        // UCT by opponent's view
        return 1.0 - (this.winCount / (double)this.visitCount) + this.CONSTANT_C * Math.sqrt(Math.log(this.availabilityCount)/this.visitCount);
    }

    /**
     * Updates the visitCount and the winCount.
     */
    public void update(double reward) {
        this.visitCount++;
        this.winCount += reward;
    }

    public void updateAvailabilityChildren() {
        for (Node child : this.children) {
            for (MoveSet move : this.currentLegalMoves) {
                if (child.getMove().equals(move)) {
                    child.updateAvailability(1);
                    break;
                }
            }
        }
    }

    public void setCurrentLegalMoves(List<MoveSet> legalMoves) {
        this.currentLegalMoves = legalMoves;
    }

    public void updateVisit(int visit) {
        this.visitCount += visit;
    }

    public void updateWin(double win) {
        this.winCount += win;
    }

    public void updateAvailability(int availability) {
        this.availabilityCount += availability;
    }

    public double getWin() {
        return this.winCount;
    }

    public int getAvailabilityCount() {
        return this.availabilityCount;
    }

    public int getVisitCount() {
        return this.visitCount;
    }

    public MoveSet getMove() {
        return this.move;
    }

    public Node getFather() {
        return this.father;
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public void getCorrectVisited() {
        int visited = 0;
        for (Node child : children) {
            visited += child.getVisitCount();
        }

        System.out.println("Visit Count: " + visited);
        System.out.println("Availability Count: " + children.get(0).getAvailabilityCount());
    }
}

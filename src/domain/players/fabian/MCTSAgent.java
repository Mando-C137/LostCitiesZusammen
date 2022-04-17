package domain.players.fabian;

import domain.players.fabian.domain.Card;
import domain.players.fabian.domain.GameState;
import domain.players.fabian.domain.MoveSet;

import java.util.List;

public class MCTSAgent {

    private Node root;

    private GameState state;
    private int player;
    private List<Card> knownOpponentCards;

    public MCTSAgent(GameState state, int player, List<Card> knownOpponentCards) {
        this.state = state;
        this.player = player;
        this.knownOpponentCards = knownOpponentCards;

        this.root = new Node();
    }

    public void runThrough() {
        // Determinization
        StateCopy copy = new StateCopy(this.state, this.player, this.knownOpponentCards);
        Node currentNode = this.root;
        boolean selectionCompleted = false;
        MoveSet selectedMove = null;

        // Descending and Selection
        while (true) {
            List<MoveSet> possibleMoves = copy.getPossibleMoves();
            if (possibleMoves.size() == 0) {
                // no node exists to that move OR it is a terminal node
                selectionCompleted = true;
            } else {
                currentNode.setCurrentLegalMoves(possibleMoves);
                MoveSet move = currentNode.searchUnexploredNode(possibleMoves);
                if (move == null) {
                    // Look for move with best UCT and descend
                    currentNode = currentNode.determineBestUCTChild(possibleMoves);
                } else {
                    // Not listed move was found
                    selectionCompleted = true;
                    selectedMove = move;
                }
            }

            // Expansion
            if (selectionCompleted) {
                if (selectedMove != null) {
                    currentNode = currentNode.expand(selectedMove);
                } else if (copy.getRoundState() != 4) {
                    // no node exists
                    selectedMove = copy.executeRandomMove();
                    currentNode = currentNode.expand(selectedMove);
                }
            } else {
                copy.executeMove(currentNode.getMove());
                continue;
            }

            // Simulation
            double reward = copy.simulateGame();

            // Backpropagation
            while (currentNode.getFather() != null) {
                currentNode.update(reward);
                currentNode = currentNode.getFather();
                currentNode.updateAvailabilityChildren();
            }

            break;
        }
    }

    public Node getRoot() {
        return this.root;
    }
}

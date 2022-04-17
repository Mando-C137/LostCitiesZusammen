package domain.players.fabian.domain;

public class MoveSet {

    private PlayMove playMove;
    private TakeMove takeMove;

    public MoveSet(PlayMove playMove, TakeMove takeMove) {
        this.playMove = playMove;
        this.takeMove = takeMove;
    }

    public PlayMove getPlayMove() {
        return this.playMove;
    }

    public TakeMove getTakeMove() {
        return this.takeMove;
    }

    public boolean equals(MoveSet other) {
        if (this.playMove.getTarget() == other.getPlayMove().getTarget()
                && this.playMove.getCardObject().equals(other.getPlayMove().getCardObject())
                && this.takeMove.getTarget() == other.getTakeMove().getTarget()) {
            return true;
        }

        return false;
    }
}

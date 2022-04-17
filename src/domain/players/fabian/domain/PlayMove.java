package domain.players.fabian.domain;

public class PlayMove{

    private final int player; // 1 or 2
    private final int card;   // 1-8
    private final Card cardObject;
    private final int target; // field = 1; discard = 2

    public PlayMove(int player, int card, Card cardObject, int target) {
        this.player = player;
        this.card = card;
        this.cardObject = cardObject;
        this.target = target;
    }

    public int getPlayer() {
        return this.player;
    }

    public int getCard() {
        return this.card;
    }

    public Card getCardObject() {
        return this.cardObject;
    }

    public int getTarget() {
        return this.target;
    }

    public String toString() {
        String text = "Player " + player + " played " + this.cardObject.toString() + " to ";

        if (target == 1) text += "their expedition cards.";
        else text += "the discard pile";

        return text;
    }
}
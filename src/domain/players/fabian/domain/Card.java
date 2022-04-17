package domain.players.fabian.domain;

public class Card {

    /**
     * 0 - Red
     * 1 - Green
     * 2 - Blue
     * 3 - White
     * 4 - Yellow
     */
    private int color;
    private int value;

    public Card(int color, int value) {
        this.color = color;
        this.value = value;
    }

    public int getColor() {return this.color;}

    public int getValue() {return this.value;}

    public boolean equals(Card other) {
        if (this.color == other.getColor() && this.value == other.getValue()) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        String text = "";
        switch (this.color) {
            case 0:
                text += "Red";
                break;
            case 1:
                text += "Green";
                break;
            case 2:
                text += "Blue";
                break;
            case 3:
                text += "White";
                break;
            case 4:
                text += "Yellow";
                break;
        }

        return text+value;
    }

}

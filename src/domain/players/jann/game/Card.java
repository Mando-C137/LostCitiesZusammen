package domain.players.jann.game;

public class Card implements Cloneable{

  boolean coinCard;
  int color; // 0=yellow; 1=blue; 2=white, 3=green, 4=red
  int value; // 0==coinCard

  public Card(int color,int value){
    this.color = color;
    this.value = value;
    this.coinCard = (value<2);
  }

  @Override
  public boolean equals(Object o) {
    Card c = (Card) o;
    if(value!=c.getValue()) {
      return false;
    }
    if(color!=c.getColor()) {
      return false;
    }
    return true;
  }

  public String getShort(){
    String s = "";
    s += this.getColorChar() + " " + this.getValue();
    return s;
  }

  public char getColorChar(){
    switch (color) {
      case 1:
        return 'Y';
      case 2:
        return 'B';
      case 3:
        return 'W';
      case 4:
        return 'G';
      case 5:
        return 'R';
    }
    return 'X';
  }

  /** Get methods */
  public int getColor() {
    return color;
  }

  public int getValue() {
    return value;
  }

  public boolean isCoinCard() {
    return coinCard;
  }

  @Override
  public Card clone(){
    return new Card(color,value);
  }

  @Override
  public String toString() {
    String colorName = "";
    switch(this.color) {
      case 0:
        colorName = "Y";
        break;
      case 1:
        colorName = "B";
        break;
      case 2:
        colorName = "W";
        break;
      case 3:
        colorName = "G";
        break;
      case 4:
        colorName = "R";
        break;
    }
    if(coinCard) return  "{" + "CC" + "," + colorName + '}';
    return "{" + colorName + "," + value + '}';
  }
}

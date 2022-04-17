package domain.players.jann.game;

/**
 * @author Jann Winter
 * This class is used to transfer information between player.Player and game.Session.
 */

public class Move {

  int cardIndex;
  boolean onExp;
  int drawFrom;

  /**
   * This constructor creates a game.Move with information on what card to place, where to place it
   * and where to draw the next card from.
   * @param card index of the placed card
   * @param exp bool indicating placement on expedition, if not on discard pile
   * @param from where new card should be drawn from 0=drawStack,1=yellowDiscard,...
   */
  public Move(int card,boolean exp,int from){
    cardIndex = card;
    onExp = exp;
    drawFrom = from;
  }

  /**
   * This constructor allows to create an instance with nothing set.
   */
  public Move(){
  }

  /** Set Methods */

  public void setCardIndex(int cardIndex) {
    this.cardIndex = cardIndex;
  }

  public void setDrawFrom(int drawFrom) {
    this.drawFrom = drawFrom;
  }

  public void setOnExp(boolean onExp) {
    this.onExp = onExp;
  }

  /** Get methods */

  public int getCardIndex() {
    return cardIndex;
  }

  public boolean isOnExp() {
    return onExp;
  }

  public int getDrawFrom() {
    return drawFrom;
  }

  @Override
  public String toString() {
    return "game.Move{" +
        "cardIndex=" + cardIndex +
        ", onExp=" + onExp +
        ", drawFrom=" + drawFrom +
        '}';
  }

  @Override
  public boolean equals(Object o){
    Move m = (Move) o;
    if(cardIndex!=m.cardIndex)return false;
    if(onExp!=m.onExp)return false;
    if(drawFrom!=m.drawFrom)return false;
    return true;
  }
}

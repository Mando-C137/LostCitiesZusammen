package domain.players.jann.player;

import domain.players.jann.game.Card;
import domain.players.jann.game.Move;
import domain.players.jann.game.Session;
import java.util.Stack;

public abstract class Player {

  boolean isCheating = false;
  boolean imP1;
  boolean hasMemory = false;

  public Player(){}

  public Player(Player player){
    isCheating = player.isCheating;
    imP1 = player.imP1;
  }

  /**
   * This method will be called to prompt the player to make a move.
   * Specific implementation may vary.
   * @return the move the player choses
   */
  public abstract Move makeMove(Card[] myHand, Stack<Card>[] myExp,Stack<Card>[] oppExp,Stack<Card>[] discardPile,boolean turn,int turnCounter);

  /**
   * This method returns the top card of any stack containing cards.
   * @param stackOfCards is the stack of cards to peek from
   * @return the top card of the stack without removing it from stack
   */
  public static Card getTopCard(Stack<Card> stackOfCards){
    if(stackOfCards.empty()) return null;
    return stackOfCards.peek();
  }

  /**
   * Determines whether game.Card can be placed on the Expedition.
   * @param stack is the expedition to place on
   * @param card is the card to place
   * @param color is the color of the expedition
   * @return true if valid move
   */
  public static boolean addCardPossible(Stack<Card> stack, Card card,int color){
    if(card.getColor()!=color) {
      return false;
    } else {
      if(card.isCoinCard()){
        if(stack.size()==0 || stack.peek().isCoinCard()) {
          return true;
        }
      } else if(!card.isCoinCard()){
        if(stack.size()!=0) {
          int newValue = card.getValue();
          int oldValue = stack.peek().getValue();
          if(newValue>oldValue || stack.peek().isCoinCard()) {
            return true;
          } else {
            return false;
          }
        } else if (stack.size()==0) {
          return true;
        }
      }
    }
    return false;
  }

  public static void printGameBoard(Stack<Card>[] myExp,Stack<Card>[] oppExp,Stack<Card>[] discardPile,int[] scores){
    StringBuilder sb = new StringBuilder("GameBoard:\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tPlayer1 -> "
        + scores[0] + " : Player2 -> " + scores[1] +"\nOpponent Expeditions:\t");
    int count = 0;
    String[] colors ={"Y->","B->","W->","G->","R->"};
    for(Stack<Card> c : oppExp){
      sb.append("\n");
      sb.append(colors[count++]);
      sb.append(c);
    }
    sb.append("\nDiscard Pile:\t\t\t");
    count = 0;
    for(Stack<Card> c: discardPile){
      sb.append(colors[count++] + "[");
      if(!c.isEmpty() && c.peek()!=null) sb.append(c.peek());
      sb.append("]\t\t\t\t");
    }
    sb.append("\nMy expeditions:\t\t\t\t");
    count = 0;
    for(Stack<Card> c : myExp){
      sb.append("\n");
      sb.append(colors[count++]);
      sb.append(c);
    }
    System.out.println(sb);
  }

  public boolean hasMemory(){
    return this.hasMemory;
  }

  /** Get methods*/
  public boolean isCheating() {
    return isCheating;
  }

  /** Set methods*/
  public abstract Move makeMove(Session session);

  public void setImP1(boolean imP1) {
    this.imP1 = imP1;
  }
}

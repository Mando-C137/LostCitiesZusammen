package domain.players.jann.player;

import domain.players.jann.game.Card;
import domain.players.jann.game.Move;
import domain.players.jann.game.Session;
import java.util.Stack;

public class RandomPlayer extends Player{

  public RandomPlayer(){}

  public RandomPlayer(Player player){super(player);}

  @Override
  public Move makeMove(Card[] myHand, Stack<Card>[] myExp, Stack<Card>[] oppExp, Stack<Card>[] discardPile,boolean turn) {
    boolean onExp;
    int random = (int) (Math.random() * 8);
    Card c = myHand[random];
    int color = c.getColor();
    if(addCardPossible(myExp[color],c,color)) {
      onExp = true;
    } else {
      onExp = false;
    }
    /*int[] from = getPossibleDrawsInt();
    int random2 = (int) (Math.random() * from.length);
    random2 = from[random2];*/
    int random2 = (int) (Math.random() * 10);
    if(random2<5) {
      while(random2<5 && getTopCard(discardPile[random2])==null) {
        random2  = (int) (Math.random() * 10);
      }
    }
    if(random2>4) {
      random2=0;
    } else {
      random2++;
    }
    return new Move(random,onExp,random2);
  }

  @Override
  public Move makeMove(Session session) {
    return null;
  }
}

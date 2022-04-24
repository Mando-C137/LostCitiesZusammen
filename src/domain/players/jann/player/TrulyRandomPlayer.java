package domain.players.jann.player;

import domain.players.jann.game.Card;
import domain.players.jann.game.Move;
import domain.players.jann.game.Session;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class TrulyRandomPlayer extends Player{

  @Override
  public Move makeMove(Card[] myHand, Stack<Card>[] myExp, Stack<Card>[] oppExp,
      Stack<Card>[] discardPile, boolean turn, int turnCounter) {
    ArrayList<Move> moves = getPossibleMoves(myHand,myExp,discardPile);
    Random random = new Random();
    Move m = moves.get(random.nextInt(moves.size()));
    return m;
  }

  @Override
  public Move makeMove(Session session) {
    return null;
  }

  public static ArrayList<Move> getPossibleMoves(Card[] hand, Stack<Card>[] myExp,Stack<Card>[] discardPile){
    ArrayList<Move> moves = new ArrayList<>();
    //Determine where draws are possible
    int[] possibleDraws = getPossibleDrawsInt(discardPile);
    int length = possibleDraws.length;
    Stack<Card>[] expeditions = myExp;
    boolean expMovePossible = false;
    for(int i = 0;i<8;i++) {
      int color = hand[i].getColor();
      expMovePossible = Session.addCardPossible(expeditions[color],hand[i],color);
      //If put on discardPile only allow other colors from discard pile
      for(int j = 0;j<length;j++){
        if(expMovePossible){
          moves.add(new Move(i,true,possibleDraws[j]));
        }
        if(color+1!=possibleDraws[j]){
          moves.add(new Move(i,false,possibleDraws[j]));
        }
      }
    }
    return moves;
  }

  public static int[] getPossibleDrawsInt(Stack<Card>[] discardPile){
    ArrayList<Integer> possibleDraws = new ArrayList<>();
    possibleDraws.add(0);
    if (!discardPile[0].isEmpty()) {
      possibleDraws.add(1);
    }
    if (!discardPile[1].isEmpty()) {
      possibleDraws.add(2);
    }
    if (!discardPile[2].isEmpty()) {
      possibleDraws.add(3);
    }
    if (!discardPile[3].isEmpty()) {
      possibleDraws.add(4);
    }
    if (!discardPile[4].isEmpty()) {
      possibleDraws.add(5);
    }
    return possibleDraws.stream().mapToInt(Integer::intValue).toArray();
  }
}

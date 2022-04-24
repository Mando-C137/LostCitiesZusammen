package domain.players.jann.player;

import domain.players.jann.game.Card;
import domain.players.jann.game.Move;
import domain.players.jann.game.Session;
import java.util.Scanner;
import java.util.Stack;

public class HumanPlayer extends Player {

  public HumanPlayer(){
    super();
  isCheating=true;}

  @Override
  public Move makeMove(Card[] myHand, Stack<Card>[] myExp,Stack<Card>[] oppExp,Stack<Card>[] discardPile,boolean turn,int turnCounter) {
    return null;
  }

  public void printHand(Card[] myHand){
    StringBuilder sb = new StringBuilder("My cards: ");
    int counter = 1;
    for(Card c: myHand){
      sb.append("\tCard " + counter++);
      sb.append(c);
      sb.append(", \t");
      if(counter==5) sb.append("\n\t\t\t\t\t");
    }
    System.out.println(sb);
  }

  @Override
  public Move makeMove(Session session) {
    int cardIndex;
    boolean onExp;
    int drawFrom;
    int player = (session.isTurn())?0:1;
    int oppPlayer = (session.isTurn())?1:0;
    int[] scores = session.calcPoints();
    Stack<Card>[] myExp = session.getExpeditions()[player];
    Stack<Card>[] oppExp = session.getExpeditions()[oppPlayer];
    Scanner scanner = new Scanner(System.in);
    printDivider();
    printGameBoard(myExp,oppExp, session.getDiscardPile(),scores);
    printHand(session.getHandAtTurn());
    printDivider();
    System.out.println("What card do you want to place? [1-8]");
    cardIndex = Integer.parseInt(scanner.nextLine()) - 1;
    System.out.println("Where do you want to place " + session.getHandAtTurn()[cardIndex]
        + "[E=Expedition, D=DiscardPile]");
    String expOrDisc = scanner.nextLine();
    onExp = (expOrDisc.equals("E") || expOrDisc.equals("P"));
    System.out.println("Where do you want to draw from? [0=drawStack, 1=yellow, 2=blue, 3=white, 4=green, 5=red]");
    drawFrom = scanner.nextInt();
    return new Move(cardIndex,onExp,drawFrom);
  }

  private void printDivider(){
    System.out.println("__________________________________________________________________________");
  }
}

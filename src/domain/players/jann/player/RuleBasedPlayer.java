package domain.players.jann.player;

import domain.players.jann.game.Card;
import domain.players.jann.game.Move;
import domain.players.jann.game.Session;
import java.util.ArrayList;
import java.util.Stack;

public class RuleBasedPlayer extends Player {

  /**
   * playColor tells for each color if agents pursues to play this expedition
   * {YELLOW,BLUE,WHITE,GREEN,RED}
   */
  private boolean[] playColor = {false, false, false, false, false};

  public RuleBasedPlayer(){
    super();
  }

  @Override
  public Move makeMove(Card[] myHand,Stack<Card>[] myExp, Stack<Card>[] oppExp, Stack<Card>[] discardPile,boolean turn,int turnCounter) {
    int index = -1;
    boolean onExp;
    int drawFrom = -1;
    /**Placing*/
    index = obviousPlacement(myHand,myExp);
    onExp = true;
    if (index == -1) {
      index = startExpedition(myHand,myExp);
      onExp = true;
      //if(index!=-1) System.out.println("Start Expedition Rule");
    }
    /*if (index == -1) {
      index = dropCardNoOneNeeds(myExp,oppExp,discardPile);
      onExp = false;
      //if(index!=-1) System.out.println("Drop game.Card No One Needs Rule");
    }*/
    if (index == -1) {
      int[] indicesCardsOpponentWants = dontGiveOpponentCardNeeded(myHand,oppExp);
      boolean placed = false;
      int maxTry = 16;
      while (!placed && maxTry > 0) {
        index = (int) (Math.random() * 8);
        Card c = myHand[index];
        int color = c.getColor();
        if (Player.addCardPossible(myExp[color],c,color)) {
          index = makeSureLowestCardPlayed(myHand,index,myExp);
          //System.out.println("Random game.Card on Expedition");
          onExp = true;
          placed = true;
        } else {
          if (!isInArray(index, indicesCardsOpponentWants)) {
            onExp = false;
            placed = true;
          } else {
            //System.out.println("Don't Discard Rule");
            maxTry--;
          }
        }
      }
    }
    /**Drawing*/
    drawFrom = drawUsefulCard(myExp,discardPile);
    //if(drawFrom!=-1) System.out.println("Draw Useful game.Card Rule");
    if(drawFrom==-1) drawFrom = 0;
    return new Move(index,onExp,drawFrom);
  }

  @Override
  public Move makeMove(Session session) {
    return null;
  }

  public int startExpedition(Card[] myHand,Stack<Card>[] myExp){
    //decideColorsToPlay(myHand);
    int[] pointsPerColor = {0,0,0,0,0};
    for(Card c : myHand){
      if(!c.isCoinCard()) pointsPerColor[c.getColor()] += c.getValue();
    }
    for(int i = 0;i<5;i++){
      if(pointsPerColor[i]>20){
        if(Player.getTopCard(myExp[i])==null) return findLowestOfColor(i,myHand);
      }
    }
    return -1;
  }

  public void decideColorsToPlay(Card[] myHand){
    int[] pointsPerColor = {0,0,0,0,0};
    for(Card c : myHand){
      if(!c.isCoinCard()) pointsPerColor[c.getColor()] += c.getValue();
    }
    for(int i = 0;i<5;i++) {
      if(pointsPerColor[i]>=20) playColor[i] = true;
    }
  }

  public int dropCardNoOneNeeds(Card[] myHand,Stack<Card>[] myExp,Stack<Card>[] oppExp,Stack<Card>[] discardPile){
    int cardColor;
    int cardValue;
    int topExpCardValue;
    int topExpCardOppValue;
    if(getNumberOfCardsLeft(myExp,oppExp,discardPile)<30) return -1;
    for(int i = 0;i<8;i++) {
      cardColor = myHand[i].getColor();
      cardValue = myHand[i].getValue();
      if (Player.getTopCard(myExp[cardColor]) != null
          && Player.getTopCard(oppExp[cardColor]) != null) {
        topExpCardValue = Player.getTopCard(myExp[cardColor]).getValue();
        topExpCardOppValue = Player.getTopCard(myExp[cardColor]).getValue();
        if (cardValue < topExpCardValue && cardValue < topExpCardOppValue
            && topExpCardValue<11 && topExpCardOppValue<11)
          return i;
      }
    }
    return -1;
  }

  public int findLowestOfColor(int color,Card[] myHand){
    int indexOfCard = -1;
    for(int i = 0;i<8;i++){
      if(myHand[i].getColor()==color) {
        if(indexOfCard != -1) {
          if(myHand[i].getValue()<myHand[indexOfCard].getValue() || myHand[i].isCoinCard()){
            indexOfCard = i;
          }
        } else {
          indexOfCard = i;
        }
      }
    }
    return indexOfCard;
  }

  public int obviousPlacement(Card[] myHand,Stack<Card>[] myExp){
    Card[] topExpCards = new Card[5];
    int count = 0;
    for(Stack<Card> exp : myExp){
      topExpCards[count++] = Player.getTopCard(exp);
    }
    for(int i = 0;i<8;i++){
      if(topExpCards[myHand[i].getColor()]!=null) {
        if (myHand[i].getValue() == topExpCards[myHand[i].getColor()].getValue() + 1) {
          return i;
        }
      }
    }
    return -1;
  }

  public int drawUsefulCard(Stack<Card>[] myExp,Stack<Card>[] discardPile){
    Card[] topDiscard = new Card[5];
    for(int i = 0;i<5;i++) {
      topDiscard[i] = Player.getTopCard(discardPile[i]);
    }
    Card[] topExp = new Card[5];
    for(int i = 0;i<5;i++) {
      topExp[i] = Player.getTopCard(myExp[i]);
    }
    if(topExp[0]!=null && topDiscard[0]!=null && topExp[0].getValue()<topDiscard[0].getValue() && !topDiscard[0].isCoinCard()) return 1;
    if(topExp[1]!=null && topDiscard[1]!=null && topExp[1].getValue()<topDiscard[1].getValue() && !topDiscard[1].isCoinCard()) return 2;
    if(topExp[2]!=null && topDiscard[2]!=null && topExp[2].getValue()<topDiscard[2].getValue() && !topDiscard[2].isCoinCard()) return 3;
    if(topExp[3]!=null && topDiscard[3]!=null && topExp[3].getValue()<topDiscard[3].getValue() && !topDiscard[3].isCoinCard()) return 4;
    if(topExp[4]!=null && topDiscard[4]!=null && topExp[4].getValue()<topDiscard[4].getValue() && !topDiscard[4].isCoinCard()) return 5;
    return -1;
  }

  public int getNumberOfCardsLeft(Stack<Card>[] myExp,Stack<Card>[] oppExp,Stack<Card>[]discardPile){
    int totalCards = 54;
    for(Stack<Card> e : myExp) totalCards -= e.size();
    for(Stack<Card> e : oppExp) totalCards -= e.size();
    for(Stack<Card> d : discardPile) totalCards -= d.size();
    return  totalCards;
  }

  public int[] dontGiveOpponentCardNeeded(Card[] myHand,Stack<Card>[] oppExp){
    ArrayList<Integer> indices = new ArrayList<>();
    for(int i = 0;i<8;i++){
      Card c = Player.getTopCard(oppExp[myHand[i].getColor()]);
      if(c==null) indices.add(i);
      if(c!=null && myHand[i].getValue()>c.getValue()) indices.add(i);
    }
    return indices.stream().mapToInt(Integer::intValue).toArray();
  }

  public static boolean isInArray(int number,int[] array){
    for(int i : array){
      if(number==i) return true;
    }
    return false;
  }

  public int makeSureLowestCardPlayed(Card[] myHand,int index,Stack<Card>[] myExp){
    Card chosen = myHand[index];
    if(chosen.isCoinCard()) return index;
    for(int i = 0;i<8;i++){
      Card current = myHand[i];
      if(!current.isCoinCard()) {
        if (current.getColor() == chosen.getColor() && current.getValue()< chosen.getValue()
            && Player.addCardPossible(myExp[chosen.getColor()],current, chosen.getColor())) {
          index = i;
          chosen = myHand[index];
        }
      }
    }
    return index;
  }
}
package domain.players.jann.game;

import domain.players.jann.player.Player;
import domain.players.jann.player.RandomPlayer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class Session {

  private Player[] players;
  private Card[][] playercards;
  private Stack<Card> drawStack;
  private Stack<Card>[] discardPile; //discardPile[0]=yellow stack etc.
  private Stack<Card>[][] expeditions; //expedtions[1][4]=player2 red exp etc.
  private boolean turn;

  public Session(Player p1,Player p2){
    initDrawStack();
    discardPile = new Stack[]{new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()};
    expeditions = new Stack[][]{{new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()},
        {new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()}};
    initPlayers(p1,p2);
    playercards = new Card[2][8];
    initPlayerCards();
    turn = true;
  }


  public Session(Player p1,Player p2,Stack<Card> drawStack){
    this.drawStack = drawStack;
    discardPile = new Stack[]{new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()};
    expeditions = new Stack[][]{{new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()},
        {new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()}};
    initPlayers(p1,p2);
    turn = true;
  }

  /**
   * Constructor responsible for creation of determinizations
   * @param playerHands
   * @param drawStack
   * @param discardPile
   */
  public Session(Card[][] playerHands,Stack<Card> drawStack,Stack<Card>[] discardPile,Stack<Card>[][] expeditions,boolean turn){
    players = new Player[]{new RandomPlayer(),new RandomPlayer()};
    playercards = playerHands;
    this.drawStack = drawStack;
    this.discardPile = discardPile;
    this.expeditions = expeditions;
    this.turn = turn;
  }

  /**
   * This constructor is used for deep copying.
   * @param copy is the Session to copy
   */
  public Session(Session copy){
    players = new Player[]{new RandomPlayer(copy.players[0]),new RandomPlayer(copy.players[1])};
    playercards = new Card[2][5];
    for(int i = 0;i<8;i++){
      playercards[0][i] = copy.playercards[0][i].clone();
      playercards[1][i] = copy.playercards[1][i].clone();
    }
    drawStack = (Stack<Card>) copy.drawStack.clone();
    discardPile = new Stack[]{new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()};
    expeditions = new Stack[][]{{new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()},
        {new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()}};
    for(int i = 0;i<5;i++){
      discardPile[i] = (Stack<Card>) copy.discardPile[i].clone();
      expeditions[0][i] = (Stack<Card>)copy.expeditions[0][i].clone();
      expeditions[1][i] = (Stack<Card>)copy.expeditions[1][i].clone();
    }
    turn = copy.isTurn();
  }

  /**
   * Executes game and returns winner.
   * @return true if player 1 is winner
   */
  public int[] playGame(){
    long start = System.currentTimeMillis();
    long end;
    int countTurns = 0;
    while(!drawStack.empty()){
      end = System.currentTimeMillis();
      int atTurn = (turn)?0:1;
      int notAtTurn = (turn)?1:0;
      Move made;
      if(players[atTurn].isCheating()){
        made = players[atTurn].makeMove(this);
      } else {
        made = players[atTurn].makeMove(playercards[atTurn],expeditions[atTurn], expeditions[notAtTurn], discardPile,turn);
      }
      /*if(atTurn==0) System.out.println(this + "\nCards: " + playercards[0][0] + playercards[0][1]
          + playercards[0][2] + playercards[0][3] + playercards[0][4] + playercards[0][5]
          + playercards[0][6] + playercards[0][7] + "\n");*/
      executeMove(made);
      turn = !turn;
      countTurns++;
      //System.out.println("Turn Nr. "+ countTurns + ";\tPlayer Nr." + atTurn + ";\tCards left -->" + getCardsLeft());
    }
    System.out.println("Turns: " + countTurns);
    return calcPoints();
  }

  public int[] simGame(){
    long start = System.currentTimeMillis();
    long end;
    while(!drawStack.empty()){
      end = System.currentTimeMillis();
      int atTurn = (turn)?0:1;
      int notAtTurn = (turn)?1:0;
      Move made = players[atTurn].makeMove(playercards[atTurn],expeditions[atTurn],expeditions[notAtTurn],discardPile,turn);
      executeMove(made);
      turn = !turn;
    }
    return calcPoints();
  }

  public int[] cheatGame(){
    while(!drawStack.empty()){
      int atTurn = (turn)?0:1;
      int notAtTurn = (turn)?1:0;
      Move made;
      if(players[atTurn].isCheating()){
        made = players[atTurn].makeMove(this);
      } else {
        made = players[atTurn].makeMove(playercards[atTurn],expeditions[atTurn], expeditions[notAtTurn], discardPile,turn);
      }
      executeMove(made);
      turn = !turn;
    }
    return calcPoints();
  }

  /**
   * This method turns move into change in game and indicates success.
   * @param move is the move proposed by the player
   * @return whether the move was legal
   */
  public boolean executeMove(Move move){
    int player = (turn)?0:1;
    int cardIndex = move.getCardIndex();
    boolean onExp = move.isOnExp();
    int drawFrom = move.getDrawFrom();
    if(onExp) {
      putOnExpedition(cardIndex,player);
    } else {
      putOnDiscardPile(cardIndex,player);
    }
    if(drawFrom==0){
      drawFromStack(cardIndex,player);
    } else {
      drawFromDiscardPile(drawFrom,cardIndex,player);
    }
    return true;//subject to change @TODO
  }

  private boolean putOnExpedition(int indexOfCard,int player){
    Card card = playercards[player][indexOfCard];
    expeditions[player][card.getColor()].add(card);
    return true;//subject to change @TODO
  }

  private boolean putOnDiscardPile(int indexOfCard,int player){
    Card card = playercards[player][indexOfCard];
    discardPile[card.getColor()].add(card);
    return true;//subject to change @TODO
  }

  private boolean drawFromStack(int index,int player){
    Card card = drawStack.pop();
    playercards[player][index] = card;
    return true;//subject to change @TODO
  }

  private boolean drawFromDiscardPile(int drawFrom,int index,int player){
    Card card = discardPile[drawFrom-1].pop();
    playercards[player][index] = card;
    return true;//subject to change @TODO
  }

  /**
   * This method is called to calculate the player's scores.
   */
  public int[] calcPoints(){
    int scoreForPlayer1 = calcPointsPlayer(0);
    int scoreForPlayer2 = calcPointsPlayer(1);
    return new int[]{scoreForPlayer1,scoreForPlayer2};
  }

  /**
   * Method calculates points a player achieved
   * @param player is the number of player in 0 for first and 1 for second
   * @return points player made
   */
  public int calcPointsPlayer(int player){
    int totalScore = 0;
    for(Stack<Card> exp : expeditions[player]){
      totalScore += calcPointsExpedition(exp);
    }
    return totalScore;
  }

  /**
   * Calculates the worth of the expedition
   * @return Points the expedition is worth
   */
  public int calcPointsExpedition(Stack<Card> expedition){
    int result = 0;
    int coincardCount = 1;
    if(expedition.size()!=0) {
      result -= 20;
    } else return 0;
    for(Card c : expedition){
      if(c.isCoinCard()) {
        coincardCount++;
      } else {
        result += c.getValue();
      }
    }
    result *= coincardCount;
    if(expedition.size()>7){
      result += 20;
    }
    return result;
  }

  /**
   * This method sets the player array and deals starting cards to players.
   * @param p1 is the first player
   * @param p2 is the second player
   */
  private void initPlayers(Player p1,Player p2){
    p1.setImP1(true);
    p2.setImP1(false);
    players = new Player[2];
    players[0] = p1;
    players[1] = p2;
  }

  /**
   * This method creates 60 cards, shuffles them and turns it into a stack for this session.
   */
  private void initDrawStack(){
    Card[] wholeSetOfCards = new Card[60];
    int count = 0;
    for(int i = 0;i<3;i++){
      wholeSetOfCards[count++] = new Card(0,i-1);
      wholeSetOfCards[count++] = new Card(1,i-1);
      wholeSetOfCards[count++] = new Card(2,i-1);
      wholeSetOfCards[count++] = new Card(3,i-1);
      wholeSetOfCards[count++] = new Card(4,i-1);
    }
    for(int i = 2;i<11;i++){
      wholeSetOfCards[count++] = new Card(0,i);
      wholeSetOfCards[count++] = new Card(1,i);
      wholeSetOfCards[count++] = new Card(2,i);
      wholeSetOfCards[count++] = new Card(3,i);
      wholeSetOfCards[count++] = new Card(4,i);
    }
    shuffleArray(wholeSetOfCards);
    drawStack = cardArrayToStack(wholeSetOfCards);
  }

  private void initPlayerCards() {
    for(int i = 0;i<8;i++){
      playercards[0][i] = drawStack.pop();
      playercards[1][i] = drawStack.pop();
    }
  }

  /**
   * This method takes an array of Cards and shuffles the order.
   * It implements the Fisher-Yates shuffle
   * @param cardArray Cards, which should be shuffled.
   * @return Returns array wit the same Cards, but in different order.
   */
  private static void shuffleArray(Card[] cardArray) {
    // If running on Java 6 or older, use `new Random()` on RHS here
    Random rnd = ThreadLocalRandom.current();
    for (int i = cardArray.length - 1; i > 0; i--) {
      int index = rnd.nextInt(i + 1);
      // Simple swap
      Card c = cardArray[index];
      cardArray[index] = cardArray[i];
      cardArray[i] = c;
    }
  }

  /**
   * This method turn an array of cards into a stack of cards.
   * @param cards Array of cards.
   * @return Stack with cards of the given array.
   */
  private static Stack<Card> cardArrayToStack(Card[] cards){
    Stack<Card> result = new Stack<Card>();
    for(Card c : cards) {
      result.push(c);
    }
    return result;
  }

  private String topOfStacksToString(Stack<Card>[] stacks){
    StringBuilder sb = new StringBuilder("{");
    for(Stack<Card> color : stacks){
      sb.append(Player.getTopCard(color));
      sb.append(", ");
    }
    sb.append("\b}");
    return String.valueOf(sb);
  }

  private String stacksToString(Stack<Card>[] stacks){
    StringBuilder sb = new StringBuilder("{");
    for(Stack<Card> color : stacks){
      for(Card c : color){
        sb.append(c);
        sb.append(", ");
      }
      sb.append("\t");
    }
    sb.append("\b\b}");
    return String.valueOf(sb);
  }

  public Card[] getHandAtTurn(){ return (turn)?playercards[0]:playercards[1]; }

  public int getCardsLeft(){
    return drawStack.size();
  }

  public boolean isTurn(){
    return turn;
  }

  public void switchTurn(){
    turn = !turn;
  }

  public boolean isOver(){
    if(drawStack.isEmpty()) return true;
    return false;
  }

  public void setPlayer(Player p1,Player p2){
    players[0] = p1;
    players[1] = p2;
  }

  public Card[] getPlayerHand(boolean player){
    return (player)?playercards[0]:playercards[1];
  }

  public Stack<Card>[] getPlayerExpeditions(boolean player){
    return (player)?expeditions[0]:expeditions[1];
  }

  @Override
  public String toString() {
    int[] scores = calcPoints();
    return "Session{" +
        "\tCards left: " + drawStack.size() +
        "\nExpeditions Player 1 " + stacksToString(expeditions[0]) +
        "\nDiscardPile " + topOfStacksToString(discardPile) +
        "\nExpeditions Player 2 " + stacksToString(expeditions[1]) +
        "\nScore Player 1: " + scores[0] +
        "\tScore Player 2. " + scores[1];
  }

  /**
   * This method creates a list with all states reachable through one player move.
   * @return list of Sessions which could result from the player's move
   */
  public ArrayList<Session> getPossibleStates(){
    ArrayList<Session> possibleStates = new ArrayList<>();
    Card [] hand = getHandAtTurn(); // Cards that can be placed
    int[] possibleDraws = getPossibleDrawsInt();
    int possibleDrawsLength = possibleDraws.length;// Cards that could be drawn
    int playerAtTurn = (turn)?0:1;
    for(int i = 0;i<8;i++) {
      int color = hand[i].getColor();
      for(int j = 0;j<possibleDrawsLength;j++) {
        for(int k = 0;k<2;k++){ // k=0 bedeutet Karte auf exp gelegt
          Session addState = null;
          if(k==1) { // Karte wird auf discardPile gelegt
            if(j==0) {
              addState = new Session(this);
              addState.putOnDiscardPile(i,playerAtTurn);
              addState.drawFromStack(i,playerAtTurn);
            } else {
              if(color!=possibleDraws[j]-1) {
                addState = new Session(this);
                addState.putOnDiscardPile(i,playerAtTurn);
                addState.drawFromDiscardPile(possibleDraws[j],i,playerAtTurn);
              }
            }
            if(addState!=null) {
              addState.switchTurn();
              possibleStates.add(addState);
            }
          } else { // Karte wird auf exp gelegt
            if(addCardPossible(expeditions[playerAtTurn][color],hand[i],color)) {
              if (j == 0) {
                addState = new Session(this);
                addState.putOnExpedition(i, playerAtTurn);
                addState.drawFromStack(i,playerAtTurn);
              } else {
                addState = new Session(this);
                addState.putOnExpedition(i, playerAtTurn);
                addState.drawFromDiscardPile(possibleDraws[j], i, playerAtTurn);
              }
              if(addState!=null) {
                addState.switchTurn();
                possibleStates.add(addState);
              }
            }
          }
        }
      }
    }
    return possibleStates;
  }

  public int compareTo(Session other){
    for(int i = 0;i<8;i++){
      if(playercards[0][i].equals(other.playercards[0][i])) return -1;
    }
    return 1;
  }

  public int[] getPossibleDrawsInt(){
    ArrayList<Integer> possibleDraws = new ArrayList<>();
    if(!drawStack.isEmpty())possibleDraws.add(0);
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

  public Card[][] getPlayercards() {
    return playercards;
  }

  public Stack<Card>[] getDiscardPile() {
    return discardPile;
  }

  public Stack<Card>[][] getExpeditions() {
    return expeditions;
  }
}

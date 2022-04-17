package domain.players.jann.montecarlo;

import domain.players.jann.game.Card;
import domain.players.jann.game.Move;
import domain.players.jann.game.Session;
import domain.players.jann.player.Player;
import domain.players.jann.player.RandomPlayer;
import domain.players.jann.player.RuleBasedPlayer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import javafx.util.Pair;

public class MonteCarloIS {

  public static Move ismcts(InformationIS initialIS,int time,boolean heavyPlayout,double explorationConstant,int rewardStrategy){
    int counterIterations = 0;
    long start = System.currentTimeMillis();
    NodeIS root = new NodeIS(); //create root(empty node at start)
    Pair<NodeIS, Session> nodeAndDet = null; //
    int player = (initialIS.isImP1())?1:2;
    while(System.currentTimeMillis()-start<time){
    /*while(time>0){
      time--;
      if(time == 1000){
        System.out.println("DEBUG");
      }*/
      Session determinization = initialIS.createDeterminization();
      nodeAndDet = select(root,determinization,explorationConstant);
      if(movesFromDetNotInNodeChildren(nodeAndDet.getKey(),determinization).size()!=0) {
        nodeAndDet = expand(nodeAndDet.getKey(),determinization);
      }
      int winner = simulate(nodeAndDet.getValue(),heavyPlayout,rewardStrategy,player);
      int reward = (winner==player)?1:0;
      backPropagate(reward,nodeAndDet.getKey());
      counterIterations++;
    }
    NodeIS bestChild = root.getBestChild();
    //System.out.println("Iterations made: " + counterIterations);
    return bestChild.getIncomingAction(); //TODO: return action with highest visit count
  }

  public static Pair<NodeIS,Session> select(NodeIS node,Session determinization,double explorationConstant){
    while(!determinization.isOver() && movesFromDetNotInNodeChildren(node,determinization).size()==0){
      node = UCB(node,determinization,explorationConstant);
      determinization.executeMove(node.incomingAction);//TODO: probably should be a deep copy
    }
    return new Pair<>(node,determinization);
  }

  /**
   * Method to get best child available with given determinization.
   * @param node Current node in tree
   * @param determinization is the current determinization
   * @param explorationConstant constant
   * @return child with highest UCT value
   */
  public static NodeIS UCB(NodeIS node,Session determinization,double explorationConstant){
    double max = -1;
    for(NodeIS child : node.getChildrenConformWith(determinization)){//only children conform with determinization
      double ucbValue = (double) child.totalReward/child.visitCount +
          explorationConstant*Math.sqrt(Math.log(child.availabilityCount)/child.visitCount);
      if(ucbValue>max){
        max = ucbValue;
        node = child;
      }
    }
    return node;
  }

  public static Pair<NodeIS,Session> expand(NodeIS node,Session determinization){
    ArrayList<Move> possible = movesFromDetNotInNodeChildren(node,determinization);
    Random random = new Random();
    Move move = possible.get(random.nextInt(possible.size())); //Select random move from compatible moves
    NodeIS child = new NodeIS(move);
    node = node.addChild(move); //TODO: probably should be a deep copy
    determinization.executeMove(move); // implements move to make and applies change to det
    return new Pair<>(node,determinization);
  }

  public static int simulate(Session determinization,boolean heavy,int rewardStrategy,int player){
    Player one;
    Player two;
    if(heavy){
      one = new RuleBasedPlayer();
      two = new RuleBasedPlayer();
    } else {
      one = new RandomPlayer();
      two = new RandomPlayer();
    }
    determinization.setPlayer(one,two);
    int[] scores = determinization.simGame();
    if(rewardStrategy==0) {
      int winner = (scores[0]>scores[1])?1:2;
      return (winner==player)?1:0;
    }
    if(rewardStrategy==1){
      if(player==1) return scores[0]-scores[1];
      return scores[1]-scores[0];
    }
    return 0;
  }

  public static boolean backPropagate(int reward,NodeIS node){
    while(node!=null){
      node.incVisitCount();
      node.incTotalReward(reward);
      node.incAvailabilityCount();
      if(node.parent==null) return true;
      for(NodeIS n : node.getSiblings()){
        n.incAvailabilityCount();
      }
      node = node.getParent();
    }
    return true;
  }

  public static ArrayList<Move> movesFromDetNotInNodeChildren(NodeIS node, Session determinization){
    ArrayList<Move> allActions = getPossibleMoves(determinization);
    allActions.removeAll(node.getMovesFromChildren());
    return  allActions;
  }

  public static ArrayList<Move> getPossibleMoves(Session game){
    ArrayList<Move> moves = new ArrayList<>();
    //Determine where draws are possible
    int[] possibleDraws = game.getPossibleDrawsInt();
    int length = possibleDraws.length;
    Card[] hand = game.getHandAtTurn();//Get hand of player at turn
    Stack<Card>[] expeditions = game.getPlayerExpeditions(game.isTurn());
    boolean expMovePossible = false;
    for(int i = 0;i<8;i++) {
      int color = hand[i].getColor();
      expMovePossible = Session.addCardPossible(expeditions[color],hand[i],color);
      //If put on discardPile only allow other colors from discard pile
      for(int j = 0;j<length;j++){
        if(expMovePossible){
          moves.add(new Move(i,true,possibleDraws[j]));
        }
        if(color!=possibleDraws[j]){
          moves.add(new Move(i,false,possibleDraws[j]));
        }
      }
    }
    return moves;
  }

}

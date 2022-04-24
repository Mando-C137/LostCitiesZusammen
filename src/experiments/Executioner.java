package experiments;

import java.time.LocalTime;
import domain.main.Game;
import domain.strategies.SimpleStrategy;

public class Executioner {

  private Game game;


  private Executioner() {

    this.experiment(10);
  }


  public static void main(String[] args) {
    Executioner exe = new Executioner();
  }



  void experiment(int num) {


    ExperimentInfo info = new ExperimentInfo();



    LocalTime in_six_hours = LocalTime.now().plusHours(8);

    int i = 0;
    for (; LocalTime.now().isBefore(in_six_hours); i++) {
      game = Game.twoWithoutStrategies();
      game.getPlayers().get(1).setStrategy(new SimpleStrategy(game.getPlayers().get(1)));

      // for (AiPlayer p : game.getPlayers()) {
      // p.setStrategy(new SecondRandomStrategy(p));
      // }

      game.gameFlow();
      int diff = game.calculateDiff(0);

      info.diff += diff;
      if (diff > 0) {
        info.wins++;
      } else if (diff < 0) {
        info.losses++;
      } else {
        info.draws++;
      }
      info.numberOfGames++;


      if (i % 10 == 0) {
        info.printInfo();
      }

      if (true) {
        break;
      }

    }

    info.printInfo();


    System.out.println("IS_MCTS vs simple, iterationen10, simulations-stategie: simple, C=0.7");


  }



}



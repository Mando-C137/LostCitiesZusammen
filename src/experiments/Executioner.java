package experiments;

import java.time.LocalDateTime;
import domain.main.Game;
import domain.players.fabian.FabianISMCTSStrategy;
import domain.players.paul.ismcts.InformationSetStrategy;

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

    System.out.println("start");

    LocalDateTime in_six_hours = LocalDateTime.now().plusHours(9);

    int i = 0;
    for (; LocalDateTime.now().isBefore(in_six_hours); i++) {
      game = Game.twoWithoutStrategies();
      game.getPlayers().get(0).setStrategy(new InformationSetStrategy(game.getPlayers().get(0)));
      game.getPlayers().get(1).setStrategy(new FabianISMCTSStrategy(game.getPlayers().get(1)));
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



    }

    info.printInfo();


    System.out.println("ich vs fabian, iterationen10, simulations-stategie: simple, C=0.7");


  }



}



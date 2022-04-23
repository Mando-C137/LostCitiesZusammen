package domain.players.paul;

import domain.main.Game;
import domain.players.AiPlayer;
import domain.strategies.RandomStrategy;

public class Utils {


  public static Game determinizeGame(AiPlayer p) {

    Game determinized = Game.determinizeGame(p);
    determinized.getPlayers().forEach(con -> con.setStrategy(new RandomStrategy(con)));
    return determinized;
  }

}

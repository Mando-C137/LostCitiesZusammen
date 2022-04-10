package test;

import domain.Game;

public class Tests {
  public static void main(String[] args) {
    Game game = new Game();


    // TODO hier dann
    // game.getPlayers.get(0).setStrategy(....)
    while (!game.getGameEnd()) {

      game.externalRound(game.getPlayerWithTurn());

      System.out.println(game);


    }

  }
}

package domain.exceptions;

import domain.cards.AbstractCard;
import domain.cards.Stapel;
import domain.players.AbstractPlayer;

public class GameException extends RuntimeException {


  /**
   * 
   */
  private static final long serialVersionUID = 3445668052383879602L;

  public GameException(String s) {
    super(s);
  }


  public static class IllegalPlayException extends GameException {
    public IllegalPlayException(AbstractCard a, Stapel s) {
      super(a + " darf nicht auf " + s + "gelegt werden");
    }

  }


  public static class DoNotOwnException extends GameException {
    public DoNotOwnException(AbstractPlayer s, AbstractCard a) {
      super("Spieler " + s + " hat die Karte " + a + "gar nicht");
    }
  }

  public static class EmptyStapelException extends GameException {
    public EmptyStapelException(Stapel s) {
      super("Stapel" + s + "ist bereits leer");
    }
  }

  public static class SameCardException extends GameException {
    public SameCardException() {
      super("Man darf nicht die selbe karten ziehen, die man gerade ablegte");
    }
  }


}



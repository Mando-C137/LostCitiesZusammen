package domain;

import domain.cards.AbstractCard;
import domain.cards.Stapel;

/**
 * Repräsentation eines Spielzuges
 *
 */
public class PlayOption {

  /**
   * Die zu spielende Karte
   */
  private final AbstractCard abstractCard;

  /**
   * Der Stapel, auf der die Karte gelegt werden soll.
   */
  private final Stapel stapel;

  /**
   * lediglich setzen der Felder
   * 
   * @param stapel
   * @param abstractCard
   */
  public PlayOption(Stapel stapel, AbstractCard abstractCard) {
    this.stapel = stapel;
    this.abstractCard = abstractCard;
  }



  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PlayOption other = (PlayOption) obj;
    if (abstractCard == null) {
      if (other.abstractCard != null)
        return false;
    } else if (!abstractCard.equals(other.abstractCard))
      return false;
    if (stapel != other.stapel)
      return false;
    return true;
  }



  @Override
  public String toString() {
    return "Play: " + this.abstractCard + " auf " + this.stapel;
  }

  /**
   * Getter für den Stapel, auf den abgelegt wurde.
   * 
   * @return
   */
  public Stapel getStapel() {
    return this.stapel;
  }

  /**
   * Getter für die Karte, die abgelegt wurde
   * 
   * @return
   */
  public AbstractCard getCard() {
    return this.abstractCard;
  }

}

package domain.cards;

/**
 * Eine Karte ist entweder eine Nummerkarte, eine Wettkarke oder eine NoCard also ein Platzhalter
 * 
 * @author paulh
 *
 */
public abstract class AbstractCard implements Comparable<AbstractCard> {

  /**
   * Jede Karte hat eine Farbe
   */
  private final Color color;

  /**
   * lediglich setzen der Farbe
   * 
   * @param color
   */
  public AbstractCard(Color color) {
    this.color = color;
  }

  public abstract int getValue();

  /**
   * 
   * @return true, falls es sich um eine Nummerkarte handelt
   */
  public abstract boolean isNumber();

  /**
   * Getter für die Farbe
   * 
   * @return
   */
  public Color getColor() {
    return this.color;
  }



}



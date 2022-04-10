package domain.cards;

/**
 * Eine Nummerkarte ist einer der zwei Varianten, die eine Karte sein kann. Sie besteht aus einer
 * Farbe (Color enum) und einem Wert (int)
 * 
 * @author paulh
 *
 */
public class NumberCard extends AbstractCard {

  /**
   * Der Wert für die Karte
   */
  final int val;

  /**
   * Setzen der Eigenschaften der Karte
   * 
   * @param c
   * @param val
   */
  public NumberCard(Color c, int val) {
    super(c);
    this.val = val;
  }

  @Override
  public boolean isNumber() {

    return true;
  }

  /**
   * Der erste Buchstabe der Farbe und der Wert.
   */
  @Override
  public String toString() {
    return " " + this.getColor().toString().substring(0, 1) + "" + this.val;

  }

  /**
   * Getter für den Wert der Karte.
   */
  public int getValue() {
    return this.val;
  }

  @Override
  public int compareTo(AbstractCard o) {

    if (!o.isNumber()) {

      if (this.val == 2) {
        return 1;
      }

      return this.getValue();
    } else {
      NumberCard numCard = (NumberCard) o;
      return this.getValue() - numCard.getValue();
    }


  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (this == null || o == null) {
      return false;
    }

    if (o.getClass() != this.getClass()) {
      return false;
    }


    AbstractCard a = (AbstractCard) o;

    if (!a.isNumber()) {
      return false;
    }

    if (!a.getColor().equals(this.getColor())) {
      return false;
    }

    NumberCard num = (NumberCard) a;

    return num.val == this.val;


  }



}

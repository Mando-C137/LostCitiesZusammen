package userinterface;

import domain.cards.AbstractCard;
import javafx.scene.Cursor;
import javafx.scene.control.Label;

public class CardLabel extends Label {

  AbstractCard abs;

  boolean move;

  public CardLabel(AbstractCard abs) {
    super(abs.toString());
    this.abs = abs;



  }

  public AbstractCard getCard() {
    return this.abs;
  }

  public void setMove(boolean glows) {

    this.move = glows;
    if (glows)
      this.setCursor(Cursor.HAND);

  }

  public boolean isMove() {

    return move;
  }



}

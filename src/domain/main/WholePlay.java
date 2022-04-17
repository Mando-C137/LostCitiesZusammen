package domain.main;

import domain.cards.Stapel;

public class WholePlay {
  AblagePlay playOption;
  Stapel drawStapel;

  public WholePlay(AblagePlay p, Stapel drawStapel) {
    this.playOption = p;
    this.drawStapel = drawStapel;
  }

  public AblagePlay getOption() {
    return this.playOption;
  }

  public Stapel getStapel() {
    return this.drawStapel;
  }

  @Override
  public String toString() {
    return playOption.getCard() + "auf" + playOption.getStapel() + " ziehen von " + this.drawStapel;
  }



  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }


    if (obj == null) {
      return false;
    }

    if (obj.getClass() != this.getClass()) {
      return false;
    }

    WholePlay cast = (WholePlay) obj;

    return this.playOption.equals(cast.getOption()) && this.drawStapel == cast.drawStapel;
  }



}

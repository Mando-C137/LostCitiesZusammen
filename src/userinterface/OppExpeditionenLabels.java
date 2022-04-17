package userinterface;

import domain.players.AbstractPlayer;

public class OppExpeditionenLabels extends MyExpeditionenLabels {



  public OppExpeditionenLabels(GameScene scene, AbstractPlayer abs) {
    super(abs, scene);
    this.MYHEIGHT = 105;
    this.myInc = -1;
    this.alignment = "bottom-center";
  }

  @Override
  protected void updateHand() {

  }

}

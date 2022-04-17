package userinterface;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import domain.cards.AbstractCard;
import domain.cards.Color;
import domain.cards.NoCard;
import domain.cards.Stapel;
import domain.players.AbstractPlayer;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public class MyExpeditionenLabels {

  private Map<Color, Stack<AbstractCard>> gameEx;

  private Map<Color, Stack<CardLabel>> uiEx;

  private AbstractPlayer abs;

  private GameScene scene;

  private List<CardLabel> hand;

  protected int MYHEIGHT = 265;

  protected int myInc = +1;

  protected String alignment = "top-center";

  private EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {

    @Override
    public void handle(MouseEvent arg0) {

      CardLabel cl = (CardLabel) arg0.getSource();
      scene.setPlayStapel(Stapel.toExpedition(cl.getCard().getColor()));
      scene.executePlay();

    }
  };

  public MyExpeditionenLabels(AbstractPlayer abs, GameScene scene) {
    this.abs = abs;
    this.gameEx = abs.getExpeditionen();
    this.uiEx = new HashMap<Color, Stack<CardLabel>>();
    this.scene = scene;
    this.hand = new LinkedList<CardLabel>();
    update();

  }

  void update() {

    for (Stack<CardLabel> cl : uiEx.values()) {
      scene.getChildren().removeAll(cl);
    }

    uiEx.clear();
    int i = 0;
    for (Color c : Color.orderedColors) {
      this.uiEx.put(c, new Stack<CardLabel>());
      if (this.gameEx.get(c).isEmpty()) {
        CardLabel cl = new CardLabel(new NoCard(c));
        cl.setPrefHeight(70);
        cl.setPrefWidth(50);
        cl.setLayoutX(50 + (i) * 65);
        cl.setLayoutY(MYHEIGHT);
        cl.setStyle("-fx-background-color: " + GameScene.ColorToString(c) + "; -fx-alignment: "
            + alignment + ";");
        cl.setOpacity(0.8);
        this.uiEx.get(c).add(cl);
      } else {
        int inc = 0;
        for (AbstractCard a : this.gameEx.get(c)) {
          CardLabel cl = new CardLabel(a);
          this.uiEx.get(c).add(cl);
          cl.setPrefHeight(70);
          cl.setPrefWidth(50);
          cl.setLayoutX(50 + (i) * 65);
          cl.setLayoutY(MYHEIGHT + inc++ * 16 * this.myInc);
          cl.setStyle(this.borderExpedition(cl));
        }

      }

      i++;
    }


    for (Stack<CardLabel> cl : uiEx.values()) {
      scene.getChildren().addAll(cl);
    }

    updateHand();
    updatePositions();

  }

  private String drawStyle(Color c) {
    return "-fx-background-color: " + GameScene.ColorToString(c) + "; -fx-alignment: " + alignment
        + "; " + GameScene.borderStyle;
  }

  private String handStyle(CardLabel cl) {
    return "-fx-background-color: " + GameScene.ColorToString(cl.getCard().getColor())
        + "; -fx-alignment: " + alignment + ";";
  }

  private String playStyleExpedition(CardLabel cl) {
    return defaultExpedition(cl) + AblageLabels.borderStyle;
  }

  private String defaultExpedition(CardLabel cl) {
    return "-fx-background-color: " + GameScene.ColorToString(cl.getCard().getColor())
        + "; -fx-alignment: " + alignment + "; -fx-background-radius:12; -fx-font-size: 12; ";
  }

  private String borderExpedition(CardLabel cl) {
    return this.defaultExpedition(cl) + "-fx-border-color: darkgrey; " + "-fx-border-width: 2;"
        + "-fx-border-radius: 2";
  }

  /**
   * 
   */
  protected void updateHand() {
    scene.getChildren().removeAll(this.hand);
    this.hand.clear();
    int i;
    i = 0;
    for (AbstractCard abs : this.abs.getHandKarten()) {
      CardLabel add = new CardLabel(abs);
      add.setPrefHeight(70);
      add.setPrefWidth(50);
      add.setLayoutX(50 + (i++) * 65);
      add.setLayoutY(600);
      add.setStyle(handStyle(add));
      this.hand.add(add);
    }

    scene.getChildren().addAll(this.hand);
  }

  public void updateListeners(boolean playMode, boolean drawMode) {

    if (playMode) {
      for (CardLabel cl : this.hand) {
        cl.setCursor(Cursor.HAND);
        cl.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

          @Override
          public void handle(MouseEvent arg0) {

            if (scene.getSelectedCard() == null) {
              scene.setSelectedCard(cl.getCard());
              scene.setDrawMode(true);
            } else if (cl.getCard().equals(scene.getSelectedCard())) {
              scene.setSelectedCard(null);
              scene.setDrawMode(false);
            } else {
              scene.setSelectedCard(cl.getCard());
              scene.setDrawMode(false);
            }
            updatePositions();
            updateStapels();
            scene.updateStapelListeners();

          }
        });

      }
    }


  }


  private void updateStapels() {
    List<Color> ls =
        this.abs.getDrawSet().stream().map(st -> st.getColor()).collect(Collectors.toList());
    for (Color c : ls) {
      if (this.uiEx.get(c) != null)
        this.uiEx.get(c).stream().forEach(a -> a.setStyle(drawStyle(a.getCard().getColor())));
    }

  }



  private void updatePositions() {
    for (CardLabel cl : this.hand) {
      cl.setLayoutY(440);
      if (cl.getCard().equals(scene.getSelectedCard())) {
        cl.setLayoutY(450);
      }
    }

  }

  public void activatePlay(List<Stapel> s) {

    if (scene.getPlayMode()) {

      for (Color st : this.uiEx.keySet()) {
        CardLabel cl = this.uiEx.get(st).peek();

        if (s.contains(Stapel.toExpedition(st))) {
          cl.setCursor(Cursor.HAND);
          cl.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
          cl.setStyle(this.playStyleExpedition(cl));
        } else {
          cl.setCursor(Cursor.DEFAULT);
          cl.setStyle(this.borderExpedition(cl));
        }

      }
    }

  }

}

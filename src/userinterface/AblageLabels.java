package userinterface;

import java.util.HashMap;
import java.util.List;
import domain.cards.Color;
import domain.cards.NoCard;
import domain.cards.Stapel;
import domain.main.Game;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public class AblageLabels {

  public static final String borderStyle =
      "-fx-border-color: black; -fx-border-radius: 7; -fx-border-width: 3; -fx-border-insets: 0.2;  -fx-background-radius: 15";

  private HashMap<Stapel, CardLabel> ablagen;

  private CardLabel nachZiehStapel;

  private GameScene scene;

  private Game game;

  static final int y = 185;

  private EventHandler<MouseEvent> playHandler = new EventHandler<MouseEvent>() {

    @Override
    public void handle(MouseEvent arg0) {
      CardLabel cl = (CardLabel) arg0.getSource();
      scene.setPlayStapel(Stapel.toMiddle(cl.getCard().getColor()));
      scene.executePlay();
    }
  };

  private EventHandler<MouseEvent> drawHandler = new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent arg0) {

      CardLabel cl = (CardLabel) arg0.getSource();
      if (cl.equals(nachZiehStapel)) {
        scene.setDrawStapel(Stapel.NACHZIEHSTAPEL);
      } else {
        scene.setDrawStapel(Stapel.toMiddle(cl.getCard().getColor()));
      }
      scene.executeDraw();
    }
  };



  public AblageLabels(Game g, GameScene scene) {
    this.game = g;
    ablagen = new HashMap<Stapel, CardLabel>();
    this.scene = scene;
  }

  public void update() {

    for (CardLabel cl : ablagen.values()) {
      scene.getChildren().remove(cl);
    }
    ablagen.clear();


    int i = 0;
    for (Color c : Color.orderedColors) {

      CardLabel cl;

      if (this.game.peekAblageStapel(c).isPresent()) {
        cl = new CardLabel(this.game.peekAblageStapel(c).get());
      } else {
        cl = new CardLabel(new NoCard(c));
        cl.setOpacity(0.8);
      }
      cl.setLayoutY(y);
      cl.setLayoutX(50 + i * 65);
      cl.setPrefHeight(70);
      cl.setPrefWidth(50);
      cl.setStyle("-fx-background-color: " + GameScene.ColorToString(cl.getCard().getColor())
          + "; -fx-alignment: top-center; -fx-background-radius: 12; ");
      ablagen.put(Stapel.toMiddle(c), cl);
      i++;
    }

    for (CardLabel cl : ablagen.values()) {
      if (cl != null)
        scene.getChildren().add(cl);
    }

    updateNachZiehStapel();



  }


  private void updateNachZiehStapel() {
    scene.getChildren().remove(nachZiehStapel);
    nachZiehStapel = new CardLabel(game.peekNachziehStapel().orElse(new NoCard(Color.RED)));
    nachZiehStapel.setText(String.valueOf(game.getRemainingCards()));
    nachZiehStapel.setLayoutX(385);
    nachZiehStapel.setLayoutY(y);
    nachZiehStapel.setPrefHeight(70);
    nachZiehStapel.setPrefWidth(50);
    nachZiehStapel.setStyle(
        "-fx-background-color: darkgrey" + "; -fx-font-size: 20 ;  -fx-alignment: center;");
    scene.getChildren().add(nachZiehStapel);

    if (scene.getDrawMode()) {
      nachZiehStapel.setStyle("-fx-background-color: darkgrey" + "; -fx-alignment: center;"
          + "-fx-border-color: white; -fx-border-radius: 7; -fx-border-width: 3; "
          + "-fx-border-insets: 0.2;  -fx-background-radius: 15; -fx-font-size: 20 ;");
      nachZiehStapel.addEventHandler(MouseEvent.MOUSE_CLICKED, drawHandler);
      nachZiehStapel.setCursor(Cursor.HAND);
    }



  }

  public HashMap<Stapel, CardLabel> getMap() {
    return this.ablagen;
  }

  public void updateAblageStapel(List<Stapel> play) {


    if (scene.getPlayMode() && play != null) {

      for (Stapel st : this.ablagen.keySet()) {
        CardLabel cl = this.ablagen.get(st);

        if (play.contains(st)) {
          cl.setCursor(Cursor.HAND);
          cl.setOnMouseClicked(playHandler);
          cl.setStyle(cl.getStyle().replace(borderStyle, "") + borderStyle);

        } else {
          cl.setCursor(Cursor.DEFAULT);
          cl.setStyle(cl.getStyle().replace(borderStyle, ""));
          cl.setOnMouseClicked(null);

        }

      }

    }

  }



  public void activateDraw() {

    List<Stapel> ls = this.scene.getMyPlayer().getDrawSet();
    ls.remove(Stapel.NACHZIEHSTAPEL);

    for (Stapel st : ls) {
      CardLabel cl = this.ablagen.get(st);
      cl.setStyle("-fx-background-color: " + GameScene.ColorToString(cl.getCard().getColor())
          + " ; -fx-alignment: top-center;"
          + "-fx-border-color: black; -fx-border-radius: 7; -fx-border-width: 3; "
          + "-fx-border-insets: 0.2;  -fx-background-radius: 15;");
      cl.addEventHandler(MouseEvent.MOUSE_CLICKED, drawHandler);
      cl.setCursor(Cursor.HAND);
    }



  }
}

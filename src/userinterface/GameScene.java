package userinterface;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import domain.cards.AbstractCard;
import domain.cards.Color;
import domain.cards.Stapel;
import domain.main.AblagePlay;
import domain.main.Game;
import domain.players.AbstractPlayer;
import domain.strategies.RandomStrategy;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class GameScene extends AnchorPane {

  static String borderStyle = "; -fx-stroke: black; -fx-stroke-width: 5;";

  private AbstractCard selectedCard = null;

  private Stapel playStapel = null;

  private Stapel drawStapel = null;

  private Game game;

  private AbstractPlayer myPlayer;

  private AbstractPlayer opp;

  private OppExpeditionenLabels oppEx;

  private MyExpeditionenLabels myEx;

  private AblageLabels middle;

  private boolean playMode;

  private boolean drawMode;

  private Label scoreBoard;

  private static final int height = 110;

  public GameScene(Game theGame) {

    super();
    this.setPrefHeight(600);
    this.setPrefWidth(600);
    initStaepels();
    this.game = theGame;
    this.drawMode = false;
    this.playMode = false;
    this.myPlayer = game.getPlayers().get(1);
    this.opp = game.getPlayers().get(0);
    this.middle = new AblageLabels(game, this);
    this.myEx = new MyExpeditionenLabels(myPlayer, this);
    this.oppEx = new OppExpeditionenLabels(this, opp);

    update();
    addButton();

  }

  private void addButton() {
    Button b = new Button("OPPONENT");
    this.getChildren().add(b);
    b.setLayoutX(20);
    b.setLayoutY(15);

    GameScene temp = this;
    b.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {

        // ProgressIndicator progress = new ProgressIndicator();
        // temp.getChildren().add(progress);
        // progress.setLayoutX(30);
        // progress.setLayoutY(30);
        game.externalRound(opp);

        playMode = true;
        drawMode = false;
        update();
        temp.getChildren().remove(b);

      }
    });


  }

  private void updateCards() {
    this.oppEx.update();
    this.myEx.update();
    this.myEx.updateListeners(this.playMode, this.drawMode);
  }

  private void update() {

    this.middle.update();
    this.updateCards();
    if (drawMode) {
      middle.activateDraw();
    }

    updateScores();
    System.out.println(this.opp.getHandKarten());
  }



  private void addNewGameButton() {

    Button newGameButton = new Button("Start new Game");
    newGameButton.setLayoutX(200);
    newGameButton.setLayoutY(15);


    newGameButton.setOnAction(event -> {

      this.getChildren().removeAll(this.getChildren());


      initStaepels();
      this.game = Game.twoWithoutStrategies();
      this.game.getPlayers().forEach(con -> con.setStrategy(new RandomStrategy(con)));
      this.drawMode = false;
      this.playMode = false;
      this.myPlayer = game.getPlayers().get(1);
      this.opp = game.getPlayers().get(0);
      this.middle = new AblageLabels(game, this);
      this.myEx = new MyExpeditionenLabels(myPlayer, this);
      this.oppEx = new OppExpeditionenLabels(this, opp);

      update();
      addButton();


    });

    this.getChildren().add(newGameButton);

  }

  private void initStaepels() {
    Group recs = new Group();
    Rectangle all = new Rectangle(600, 600);
    all.setFill(Paint.valueOf("#ffd7ac"));
    recs.getChildren().add(all);
    for (int i = 0; i < 4; i++) {
      Rectangle r = new Rectangle(325, 60);
      r.setLayoutX(44);
      r.setLayoutY(height + i * 80);
      recs.getChildren().add(r);
      if (i == 3) {
        r.setWidth(515);
        r.setLayoutY(445);
      }
      r.setFill(Paint.valueOf("#8f4d14"));
    }

    this.getChildren().add(recs);

  }

  static String ColorToString(Color c) {
    switch (c) {
      case BLUE:
        return "deepskyblue";
      case GREEN:
        return "palegreen;";
      case RED:
        return "tomato;";
      case WHITE:
        return "white";
      case YELLOW:
        return "yellow";
      default:
        return "orange";
    }
  }

  /**
   * drawStapel
   */
  void executePlay() {
    game.externalPlay(new AblagePlay(playStapel, selectedCard), myPlayer);
    this.playMode = false;
    this.drawMode = true;
    playStapel = null;
    selectedCard = null;
    update();


  }

  void executeDraw() {
    game.externalDraw(drawStapel, myPlayer);

    playStapel = null;
    selectedCard = null;
    drawStapel = null;
    if (!game.getGameEnd()) {
      this.game.externalRound(opp);
      this.playMode = true;
      this.drawMode = false;
    } else {
      this.playMode = false;
      this.drawMode = false;
    }

    if (game.getGameEnd()) {
      this.playMode = false;
      this.drawMode = false;
      this.addNewGameButton();
    }
    update();
  }

  void setSelectedCard(AbstractCard abs) {
    this.selectedCard = abs;
  }

  AbstractCard getSelectedCard() {
    return this.selectedCard;
  }

  public void setDrawMode(boolean b) {
    this.drawMode = b;
  }

  public boolean getDrawMode() {
    return this.drawMode;
  }

  public void updateStapelListeners() {

    if (this.selectedCard != null) {
      List<Stapel> s = new LinkedList<Stapel>();
      s.add(Stapel.toMiddle(selectedCard.getColor()));
      this.middle.updateAblageStapel(s);

      s = this.myPlayer.getPlaySet().stream()
          .filter(p -> p.getCard().equals(selectedCard) && p.getStapel().isExpedition())
          .map(p -> p.getStapel()).collect(Collectors.toList());

      this.myEx.activatePlay(s);
      return;
    }

    this.middle.updateAblageStapel(new LinkedList<Stapel>());
    this.myEx.activatePlay(new LinkedList<Stapel>());

  }

  public boolean getPlayMode() {
    return this.playMode;
  }

  public void setPlayStapel(Stapel st) {
    this.playStapel = st;
  }

  public void setDrawStapel(Stapel s) {
    this.drawStapel = s;
  }

  public AbstractPlayer getMyPlayer() {
    return this.myPlayer;
  }

  public void updateScores() {
    String scores = this.game.calculateScores();

    if (this.scoreBoard == null || !this.getChildren().contains(scoreBoard)) {
      this.scoreBoard = new Label();
      this.scoreBoard.setLayoutX(380);
      this.scoreBoard.setLayoutY(10);
      this.scoreBoard.setPrefHeight(90);
      this.scoreBoard.setMaxWidth(200);
      this.scoreBoard.setStyle(
          "-fx-alignment: center-left; -fx-background-color: burlywood ; -fx-font-size: 15; "
              + "fx-font-family: Calibri;" + "-fx-border-width: 2; " + "-fx-border-color: brown; ");
      this.scoreBoard.setPrefWidth(200);
      this.getChildren().add(scoreBoard);
    }

    this.scoreBoard.setText(scores);
  }

}

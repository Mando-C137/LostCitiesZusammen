package experiments;

public class ExperimentInfo {


  public int diff;
  public int wins;
  public int losses;
  public int draws;
  public double start;
  public int numberOfGames;

  public ExperimentInfo() {
    this.start = System.currentTimeMillis();
    this.diff = 0;
    this.wins = 0;
    this.losses = 0;
    this.draws = 0;
    this.numberOfGames = 0;
  }

  public void printInfo() {
    System.out.println("diff = " + this.diff);
    System.out.println("Wins = " + this.wins);
    System.out.println("losses = " + this.losses);
    System.out.println("Draws = " + this.draws);
    System.out.println("OverallGames  " + this.numberOfGames);
    System.out
        .println("Overalltime " + (double) (System.currentTimeMillis() - this.start) / 1000 + "s");

  }

  public void applyGame(int diff2) {
    this.numberOfGames++;
    this.diff += diff2;
    if (diff2 > 0) {
      this.wins++;
    } else if (diff2 == 0) {
      this.losses++;
    } else {
      this.draws++;
    }
  }



}

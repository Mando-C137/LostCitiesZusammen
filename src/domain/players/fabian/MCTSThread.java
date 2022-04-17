package domain.players.fabian;

public class MCTSThread extends Thread {

    private MCTSAgent agent;
    private boolean running;
    private boolean finished = false;

    public MCTSThread(MCTSAgent agent) {
        this.agent = agent;
    }

    public void run() {
        this.running = true;
        while (this.running) {
            this.agent.runThrough();
        }
        this.finished = true;
    }

    public void stopRun() {
        this.running = false;
    }

    public boolean isFinished() {
        return  this.finished;
    }
}

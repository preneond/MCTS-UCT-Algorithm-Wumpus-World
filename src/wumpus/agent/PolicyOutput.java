package wumpus.agent;

public class PolicyOutput {
    private UCTNode n;
    private double reward;

    public PolicyOutput(UCTNode n, double reward) {
        this.n = n;
        this.reward = reward;
    }

    public UCTNode getNode() {
        return n;
    }

    public double getReward() {
        return reward;
    }
}

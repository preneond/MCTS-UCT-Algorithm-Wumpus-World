package wumpus.agent;

import wumpus.world.Action;
import wumpus.world.Outcome;
import wumpus.world.WorldModel;
import wumpus.world.WorldState;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class UCTNode {
    WorldState state;
    UCTNode parent;
    Action incomingAction;
    List<List<UCTNode>> childs;
    LinkedList<Action> untriedActions;
    // number of visits
    double N;
    // node value estimate
    double Q;
    private Random random = new Random();

    private final int numActions = Action.values().length;
    private double reward;

    UCTNode(WorldState state, Action incomingAction, double reward, UCTNode parent) {
        this.incomingAction = incomingAction;
        this.state = state;
        this.reward = reward;
        N = 0;
        Q = 0.0;
        this.parent = parent;
        untriedActions = new LinkedList<>(WorldModel.getActions(state));
        childs = new ArrayList<>(numActions);
        for (int i = 0; i < numActions; i++) {
            childs.add(new ArrayList<>());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UCTNode uctNode = (UCTNode) o;
        return state.equals(uctNode.state);
    }

    @Override
    public String toString() {
        return "UCTNode{state=" + state + '}';
    }

    Action bestChild(double c) {
        double maxVal = -Integer.MAX_VALUE;
        Action maxAction = null;

        for (int i = 0; i < numActions; i++) {
            double val = 0;
            int actionVisits = 0;
            for (UCTNode child : childs.get(i)) {
                val += child.Q / child.N;
                actionVisits += child.N;
            }

            val += (c * sqrt((2.0 * log(N)) / actionVisits));

            if (val > maxVal) {
                maxAction = Action.values()[i];
                maxVal = val;
            }
        }

        return maxAction;
    }

    double rollout() {
        WorldState rolloutState = state;
        double reward = this.reward;
        double depth = 0;
        if (reward != -1 || WorldModel.isTerminal(state)) {
            return reward;
        }

        int maxDepth = 30; //Integer.MAX_VALUE; //10;
        while (!WorldModel.isTerminal(rolloutState) && depth < maxDepth) {
            List<Action> actionList = new ArrayList<>(WorldModel.getActions(rolloutState));
            int size = actionList.size();
            Action a = actionList.get(random.nextInt(size));
            Outcome o = WorldModel.performAction(rolloutState, a, random);
            rolloutState = o.state;
            reward += o.reward;
            depth++;
            if (o.reward > 0) break;
        }
        return reward/depth;
    }

    boolean isTerminal() {
        return WorldModel.isTerminal(state);
    }

    boolean isFullyExpanded() {
        return untriedActions.isEmpty();

    }

    void backpropagate(double reward) {
        N++;
        Q += reward;
        if (parent != null) {
            parent.backpropagate(reward);
        }
    }

    UCTNode expand() {
        Action a = untriedActions.pop();
        Outcome o = WorldModel.performAction(state, a, random);
        UCTNode childNode = new UCTNode(o.state, a, o.reward, this);
        childs.get(a.ordinal()).add(childNode);
        return childNode;
    }

    public UCTNode getChildForOutcome(Outcome o, Action a) {
        for (UCTNode n : childs.get(a.ordinal())) {
            if (o.state == n.state) {
                return n;
            }
        }
        UCTNode childNode = new UCTNode(o.state, a,o.reward, this);
        childs.get(a.ordinal()).add(childNode);
        return childNode;
    }
}

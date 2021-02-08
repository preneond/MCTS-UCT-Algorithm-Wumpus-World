package wumpus.agent;

import wumpus.world.*;
import wumpus.world.WorldState;

import static java.lang.Math.sqrt;

import java.util.*;


public class Agent implements WorldModel.Agent {
    // exploration term
    private static double Cp = 1 / sqrt(2);
    private static int timeLimitMs = 1400;
    private Random rnd = new Random();

    /**
     * This method is called when the simulation engine requests the next action.
     * You are given a state of the world that consists of position of agent, positions of wumpuses, and the map of world.
     * <p>
     * The top-left corner has coordinates (0,0).
     * <p>
     * You can check the current position of agent through state.getAgent(), the positions of all
     * wumpuses can be obtained via state.getWumpuses() and the map of world through state.getMap().
     * <p>
     * <p>
     * You can check whether there is an obstacle on a particular cell of the map
     * by querying state.getMap()[x][y] == CellContent.OBSTACLE.
     * <p>
     * There is one gold on the map. You can query whether a position contains gold by
     * querying state.getMap()[x][y] == CellContent.GOLD.
     * <p>
     * Further, there are several pits on the map. You can query whether a position contains pit by
     * querying state.getMap()[x][y] == CellContent.PIT.
     *
     * @return action to perform in the next step
     */
    public Action nextStep(WorldState state) {
        return uctSearch(state);
    }

    private Action uctSearch(WorldState s0) {
        UCTNode v0 = new UCTNode(s0, null, 0, null);
        UCTNode v1;
        double reward;
        int i = 0;
        long startTime = Calendar.getInstance().getTimeInMillis();
        while (Calendar.getInstance().getTimeInMillis() - startTime < timeLimitMs) {
//            if (i % 1000 == 0) {
//                System.out.println("Iteration: " + i);
//            }
            v1 = treePolicy(v0);
            reward = v1.rollout();
            v1.backpropagate(reward);
//            printStateEstimates(v0);
            i++;
        }
        return v0.bestChild(0);
    }

    private UCTNode treePolicy(UCTNode v) {
        while (!v.isTerminal()) {
            if (!v.isFullyExpanded()) {
                return v.expand();
            } else {
                Action a = v.bestChild(Cp);
                Outcome o = WorldModel.performAction(v.state, a, rnd);
                v = v.getChildForOutcome(o, a);
            }
        }
        return v;
    }

    private void printStateEstimates(UCTNode v0) {
        LinkedList<UCTNode> q = new LinkedList<>();
        Set<UCTNode> closedList = new HashSet<>();
        q.push(v0);

        while (!q.isEmpty()) {
            UCTNode n = q.pop();
            if (closedList.contains(n)) continue;
            closedList.add(n);
            if (n.Q / n.N > 0) {
                System.out.println(n.state.getAgent() + ": " + n.Q / n.N);
            }
            for (int i = 0; i < 4; i++) {
                for (UCTNode n2 : n.childs.get(i)) {
                    q.push(n2);
                }
            }
        }
    }
}

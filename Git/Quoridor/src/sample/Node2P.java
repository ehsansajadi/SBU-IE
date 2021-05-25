package sample;

import java.util.ArrayList;

public class Node2P {

    Node2P childNode;
    Player ai, enemy;
    int score;
    Move lastMove, nextMove;
    ArrayList<Brick> priorityBricks = new ArrayList();

    Node2P(Player p0, Player p1, Move lastMove) { //it's p0's turn , p1 is waiting
        this.ai = p0;
        this.enemy = p1;
        this.lastMove = lastMove;
    }

}

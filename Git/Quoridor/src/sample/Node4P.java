package sample;

public class Node4P {
    Node4P childNode;
    Player ai, p1, p2, p3;
    //int scoreAI, scoreP1, scoreP2, scoreP3;
    int score;
    Move lastMove, nextMove;

    Node4P(Player p0, Player p1, Player p2, Player p3, Move lastMove) { //it's p0's turn , p1 is waiting
        this.ai = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.lastMove = lastMove;
    }
}

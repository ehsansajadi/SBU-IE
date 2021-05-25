package sample;

public class Move {
    boolean changePos;
    Direction direction;
    Brick firstBrick, secondBrick;

    Move(Direction direction){
        changePos = true;
        this.direction = direction;
    }

    Move(Brick firstBrick, Brick secondBrick){
        changePos = false;
        this.firstBrick = firstBrick;
        this.secondBrick = secondBrick;
    }

    boolean Equals(Move move){
        if(this.changePos == move.changePos){
            if(changePos && this.direction == move.direction){
                return true;
            }else
                return false;
        }else
            return false;
    }
}

enum Direction{
    up,down,left,right,upUp,downDown,leftLeft,rightRight,upLeft,upRight,downLeft,downRight;
}
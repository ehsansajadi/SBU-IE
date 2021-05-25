package sample;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


class Wall extends Rectangle {

    Wall(Coordinates c, int width, int height, Color color) {
        super(width, height, color);
        setTranslateX(c.x);
        setTranslateY(c.y);
    }
}

public class Brick extends Rectangle {
    boolean isHorizontal;
    boolean isSolid = false;
    Coordinates coordinates;
    int row, column;

    Brick(Coordinates c, int width, int height, boolean isHorizontal, int row, int column) {
        super(width, height, Color.DARKGRAY);
        this.isHorizontal = isHorizontal;
        coordinates = c;
        this.row = row;
        this.column = column;
        setTranslateX(c.x);
        setTranslateY(c.y);
    }

    /*Brick(boolean isHorizontal, int row, int column){
        this.isHorizontal = isHorizontal;
        this.row = row;
        this.column = column;
    }*/

    Brick(Brick brick){
        this.isHorizontal = brick.isHorizontal;
        this.row = brick.row;
        this.column = brick.column;
        this.isSolid = brick.isSolid;
    }

    Brick() {
    }

    boolean isValidWall(Brick brick) {
        if (this.isSolid || brick.isSolid)
            return false;
        if (this.isHorizontal) {
            return this.row == brick.row && (this.column == (brick.column + 1) || (this.column + 1) == brick.column);
        } else {
            return this.column == brick.column && (this.row == (brick.row + 1) || (this.row + 1) == brick.row);
        }
    }

    boolean isValidJoint(Brick brick, boolean[][] emptyJoints) {
        if (this.isHorizontal) {
            if (this.column == brick.column + 1)
                return emptyJoints[this.row][brick.column];
            else if (this.column + 1 == brick.column)
                return emptyJoints[this.row][this.column];
            else
                return false;
        } else {
            if (this.row == brick.row + 1)
                return emptyJoints[brick.row][this.column];
            else if (this.row + 1 == brick.row)
                return emptyJoints[this.row][this.column];
            else
                return false;
        }
    }
}


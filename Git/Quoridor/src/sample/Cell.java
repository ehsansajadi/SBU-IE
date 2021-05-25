package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.util.LinkedList;
import java.util.Queue;

public class Cell extends Rectangle {
    Coordinates coordinates;
    boolean isFilled = false, topBlocked = false, bottomBlocked = false, rightBlocked = false, leftBlocked = false;
    int row, column;

    Cell(Coordinates c, int width, Color color, int row, int column) {
        super(width, width, color);
        this.coordinates = c;
        this.row = row;
        this.column = column;
        setTranslateX(c.x);
        setTranslateY(c.y);
    }

    Cell(Cell cell) {
        this.row = cell.row;
        this.column = cell.column;
        this.topBlocked = cell.topBlocked;
        this.bottomBlocked = cell.bottomBlocked;
        this.rightBlocked = cell.rightBlocked;
        this.leftBlocked = cell.leftBlocked;
        this.isFilled = cell.isFilled;
    }

    boolean isEqual(Cell cell) {
        return cell.row == this.row && cell.column == this.column;
    }

    public boolean isValidCell(Cell[][] cells, Cell playerCell) {
        Cell topCell = null, bottomCell = null, rightCell = null, leftCell = null;
        if (playerCell.row != 0 && cells[playerCell.row - 1][playerCell.column] != null)
            topCell = cells[playerCell.row - 1][playerCell.column];
        if (playerCell.row != 8 && cells[ playerCell.row + 1][playerCell.column] != null)
            bottomCell = cells[playerCell.row + 1][playerCell.column];
        if (playerCell.column != 8 && cells[playerCell.row][playerCell.column + 1] != null)
            rightCell = cells[playerCell.row][playerCell.column + 1];
        if (playerCell.column != 0 && cells[playerCell.row][playerCell.column - 1] != null)
            leftCell = cells[playerCell.row][playerCell.column - 1];

        if (!playerCell.topBlocked && topCell != null) {
            if (!topCell.isFilled) {
                if (topCell.isEqual(this))
                    return true;
            } else {
                if (topCell.row != 0 && !topCell.topBlocked && cells[topCell.row - 1][topCell.column] != null &&
                        !cells[topCell.row - 1][topCell.column].isFilled) {
                    if (cells[topCell.row - 1][topCell.column].isEqual(this))
                        return true;
                } else if (topCell.row != 0 && !topCell.topBlocked && cells[topCell.row - 1][topCell.column] != null &&
                        cells[topCell.row - 1][topCell.column].isFilled) {
                    if (topCell.column != 0 && cells[topCell.row][topCell.column - 1] != null &&
                            !cells[topCell.row][topCell.column - 1].isFilled &&
                            !topCell.leftBlocked) {
                        if (cells[topCell.row][topCell.column - 1].isEqual(this))
                            return true;
                    }
                    if (topCell.column != 8 && cells[topCell.row][topCell.column + 1] != null &&
                            !cells[topCell.row][topCell.column + 1].isFilled &&
                            !topCell.rightBlocked) {
                        if (cells[topCell.row][topCell.column + 1].isEqual(this))
                            return true;
                    }
                } else if (topCell.topBlocked) {
                    if (topCell.column != 0 && cells[topCell.row][topCell.column - 1] != null &&
                            !cells[topCell.row][topCell.column - 1].isFilled &&
                            !topCell.leftBlocked) {
                        if (cells[topCell.row][topCell.column - 1].isEqual(this))
                            return true;
                    }
                    if (topCell.column != 8 && cells[topCell.row][topCell.column + 1] != null &&
                            !cells[topCell.row][topCell.column + 1].isFilled &&
                            !topCell.rightBlocked) {
                        if (cells[topCell.row][topCell.column + 1].isEqual(this))
                            return true;
                    }
                }
            }
        }
        if (!playerCell.bottomBlocked && bottomCell != null) {
            if (!bottomCell.isFilled) {
                if (bottomCell.isEqual(this))
                    return true;
            } else {
                if (bottomCell.row != 8 && !bottomCell.bottomBlocked && cells[bottomCell.row + 1][bottomCell.column] != null &&
                        !cells[bottomCell.row + 1][bottomCell.column].isFilled) {
                    if (cells[bottomCell.row + 1][bottomCell.column].isEqual(this))
                        return true;
                } else if (bottomCell.row != 8 && !bottomCell.bottomBlocked && cells[bottomCell.row + 1][bottomCell.column] != null &&
                        cells[bottomCell.row + 1][bottomCell.column].isFilled) {
                    if (bottomCell.column != 0 && cells[bottomCell.row][bottomCell.column - 1] != null &&
                            !cells[bottomCell.row][bottomCell.column - 1].isFilled &&
                            !bottomCell.leftBlocked) {
                        if (cells[bottomCell.row][bottomCell.column - 1].isEqual(this))
                            return true;
                    }
                    if (bottomCell.column != 8 && cells[bottomCell.row][bottomCell.column + 1] != null &&
                            !cells[bottomCell.row][bottomCell.column + 1].isFilled &&
                            !bottomCell.rightBlocked) {
                        if (cells[bottomCell.row][bottomCell.column + 1].isEqual(this))
                            return true;
                    }
                } else if (bottomCell.bottomBlocked) {
                    if (bottomCell.column != 0 && cells[bottomCell.row][bottomCell.column - 1] != null &&
                            !cells[bottomCell.row][bottomCell.column - 1].isFilled &&
                            !bottomCell.leftBlocked) {
                        if (cells[bottomCell.row][bottomCell.column - 1].isEqual(this))
                            return true;
                    }
                    if (bottomCell.column != 8 && cells[bottomCell.row][bottomCell.column + 1] != null &&
                            !cells[bottomCell.row][bottomCell.column + 1].isFilled &&
                            !bottomCell.rightBlocked) {
                        if (cells[bottomCell.row][bottomCell.column + 1].isEqual(this))
                            return true;
                    }
                }
            }
        }
        if (!playerCell.rightBlocked && rightCell != null) {
            if (!rightCell.isFilled) {
                if (rightCell.isEqual(this))
                    return true;
            } else {
                if (rightCell.column != 8 && !rightCell.rightBlocked && cells[rightCell.row][rightCell.column + 1] != null &&
                        !cells[rightCell.row][rightCell.column + 1].isFilled) {
                    if (cells[rightCell.row][rightCell.column + 1].isEqual(this))
                        return true;
                } else if (rightCell.column != 8 && !rightCell.rightBlocked && cells[rightCell.row][rightCell.column + 1] != null &&
                        cells[rightCell.row][rightCell.column + 1].isFilled) {
                    if (rightCell.row != 0 && cells[rightCell.row - 1][rightCell.column] != null &&
                            !cells[rightCell.row - 1][rightCell.column].isFilled &&
                            !rightCell.topBlocked) {
                        if (cells[rightCell.row - 1][rightCell.column].isEqual(this))
                            return true;
                    }
                    if (rightCell.row != 8 && cells[rightCell.row + 1][rightCell.column] != null &&
                            !cells[rightCell.row + 1][rightCell.column].isFilled &&
                            !rightCell.bottomBlocked) {
                        if (cells[rightCell.row + 1][rightCell.column].isEqual(this))
                            return true;
                    }
                } else if (rightCell.rightBlocked) {
                    if (rightCell.row != 0 && cells[rightCell.row - 1][rightCell.column] != null &&
                            !cells[rightCell.row - 1][rightCell.column].isFilled &&
                            !rightCell.topBlocked) {
                        if (cells[rightCell.row - 1][rightCell.column].isEqual(this))
                            return true;
                    }
                    if (rightCell.row != 8 && cells[rightCell.row + 1][rightCell.column] != null &&
                            !cells[rightCell.row + 1][rightCell.column].isFilled &&
                            !rightCell.bottomBlocked) {
                        if (cells[rightCell.row + 1][rightCell.column].isEqual(this))
                            return true;
                    }
                }
            }
        }
        if (!playerCell.leftBlocked && leftCell != null) {
            if (!leftCell.isFilled) {
                return leftCell.isEqual(this);
            } else {
                if (leftCell.column != 0 && !leftCell.leftBlocked && cells[leftCell.row][leftCell.column - 1] != null &&
                        !cells[leftCell.row][leftCell.column - 1].isFilled) {
                    return cells[leftCell.row][leftCell.column - 1].isEqual(this);
                } else if (leftCell.column != 0 && !leftCell.leftBlocked && cells[leftCell.row][leftCell.column - 1] != null &&
                        cells[leftCell.row][leftCell.column - 1].isFilled) {
                    if (leftCell.row != 0 && cells[leftCell.row - 1][leftCell.column] != null &&
                            !cells[leftCell.row - 1][leftCell.column].isFilled && !leftCell.topBlocked) {
                        return cells[leftCell.row - 1][leftCell.column].isEqual(this);
                    }
                    if (leftCell.row != 8 && cells[leftCell.row + 1][leftCell.column] != null &&
                            !cells[leftCell.row + 1][leftCell.column].isFilled && !leftCell.bottomBlocked) {
                        return cells[leftCell.row + 1][leftCell.column].isEqual(this);
                    }
                } else if (leftCell.leftBlocked) {
                    if (leftCell.row != 0 && cells[leftCell.row - 1][leftCell.column] != null &&
                            !cells[leftCell.row - 1][leftCell.column].isFilled && !leftCell.topBlocked) {
                        return cells[leftCell.row - 1][leftCell.column].isEqual(this);
                    }
                    if (leftCell.row != 8 && cells[leftCell.row + 1][leftCell.column] != null &&
                            !cells[leftCell.row + 1][leftCell.column].isFilled && !leftCell.bottomBlocked) {
                        return cells[leftCell.row + 1][leftCell.column].isEqual(this);
                    }
                }
            }
        }
        return false;
    }

    public static boolean canReachDestination2P(Cell[][] cells, boolean[][] searchedCells, Player p0, Player p1) {
        resetSeachedCells(searchedCells);
        //int t1 = bestCostPath(cells, searchedCells, p0.currentCell, true, 0);
        int t1 = bestCostPath(cells, searchedCells,p0.currentCell.row,p0.currentCell.column,true, 0);
        resetSeachedCells(searchedCells);
        int t2 = bestCostPath(cells, searchedCells,p1.currentCell.row,p1.currentCell.column,true, 8);

        return (t1 != -1 && t2 != -1);
    }

    public static boolean canReachDestination4P(Cell[][] cells, boolean[][] searchedCells, Player p0, Player p1,
                                         Player p2, Player p3) {
        resetSeachedCells(searchedCells);
        int t0 = bestCostPath(cells, searchedCells,p0.currentCell.row,p0.currentCell.column,true, 0);
        resetSeachedCells(searchedCells);
        int t1 = bestCostPath(cells, searchedCells,p1.currentCell.row,p1.currentCell.column,true, 8);
        resetSeachedCells(searchedCells);
        int t2 = bestCostPath(cells, searchedCells,p2.currentCell.row,p2.currentCell.column,false, 0);
        resetSeachedCells(searchedCells);
        int t3 = bestCostPath(cells, searchedCells,p3.currentCell.row,p3.currentCell.column,false, 8);
        return (t0 != -1 && t1 != -1 && t2 != -1 && t3 != -1);
        /*boolean t1 = searchPath(cells, searchedCells, p0.currentCell, true, 0);
        resetSeachedCells(searchedCells);
        boolean t2 = searchPath(cells, searchedCells, p1.currentCell, true, 8);
        resetSeachedCells(searchedCells);
        boolean t3 = searchPath(cells, searchedCells, p2.currentCell, false, 8);
        resetSeachedCells(searchedCells);
        boolean t4 = searchPath(cells, searchedCells, p3.currentCell, false, 0);
        return t1 && t2 && t3 && t4;
        */
    }

    //for new searches
    public static void resetSeachedCells(boolean[][] searchedCells) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                searchedCells[i][j] = false;
            }
        }
    }

    static int bestCostPath(Cell[][] cells, boolean[][] searchedCells, int row, int column, boolean isRow, int goalLine){
        ModifiedCell firstModifiedCell = new ModifiedCell(row, column, null, 0);
        Queue<ModifiedCell> cellQueue = new LinkedList<>();
        cellQueue.add(firstModifiedCell);
        while (!cellQueue.isEmpty()) {
            ModifiedCell modifiedCell = cellQueue.remove();
            if(isWin(modifiedCell, isRow, goalLine))
                return modifiedCell.cost;
            if(!searchedCells[modifiedCell.row][modifiedCell.column]){
                searchedCells[modifiedCell.row][modifiedCell.column] = true;
                if(isRow){
                    if(modifiedCell.row > goalLine){
                        if (modifiedCell.row > 0){
                            if(!cells[modifiedCell.row][modifiedCell.column].topBlocked &&
                                    !searchedCells[modifiedCell.row - 1][modifiedCell.column])
                                cellQueue.add(new ModifiedCell(modifiedCell.row - 1, modifiedCell.column,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                        if(modifiedCell.column > 0){
                            if(!cells[modifiedCell.row][modifiedCell.column].leftBlocked &&
                                    !searchedCells[modifiedCell.row][modifiedCell.column - 1])
                                cellQueue.add(new ModifiedCell(modifiedCell.row, modifiedCell.column - 1,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                        if(modifiedCell.column < 8){
                            if(!cells[modifiedCell.row][modifiedCell.column].rightBlocked &&
                                    !searchedCells[modifiedCell.row][modifiedCell.column + 1])
                                cellQueue.add(new ModifiedCell(modifiedCell.row, modifiedCell.column + 1,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                        if(modifiedCell.row < 8){
                            if(!cells[modifiedCell.row][modifiedCell.column].bottomBlocked &&
                                    !searchedCells[modifiedCell.row + 1][modifiedCell.column])
                                cellQueue.add(new ModifiedCell(modifiedCell.row + 1, modifiedCell.column,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                    }else{
                        if(modifiedCell.row < 8){
                            if(!cells[modifiedCell.row][modifiedCell.column].bottomBlocked &&
                                    !searchedCells[modifiedCell.row + 1][modifiedCell.column])
                                cellQueue.add(new ModifiedCell(modifiedCell.row + 1, modifiedCell.column,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                        if(modifiedCell.column > 0){
                            if(!cells[modifiedCell.row][modifiedCell.column].leftBlocked &&
                                    !searchedCells[modifiedCell.row][modifiedCell.column - 1])
                                cellQueue.add(new ModifiedCell(modifiedCell.row, modifiedCell.column - 1,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                        if(modifiedCell.column < 8){
                            if(!cells[modifiedCell.row][modifiedCell.column].rightBlocked &&
                                    !searchedCells[modifiedCell.row][modifiedCell.column + 1])
                                cellQueue.add(new ModifiedCell(modifiedCell.row, modifiedCell.column + 1,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                        if (modifiedCell.row > 0){
                            if(!cells[modifiedCell.row][modifiedCell.column].topBlocked &&
                                    !searchedCells[modifiedCell.row - 1][modifiedCell.column])
                                cellQueue.add(new ModifiedCell(modifiedCell.row - 1, modifiedCell.column,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                    }
                }else{
                    if(modifiedCell.column > goalLine){
                        if(modifiedCell.column > 0){
                            if(!cells[modifiedCell.row][modifiedCell.column].leftBlocked &&
                                    !searchedCells[modifiedCell.row][modifiedCell.column - 1])
                                cellQueue.add(new ModifiedCell(modifiedCell.row, modifiedCell.column - 1,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                        if (modifiedCell.row > 0){
                            if(!cells[modifiedCell.row][modifiedCell.column].topBlocked &&
                                    !searchedCells[modifiedCell.row - 1][modifiedCell.column])
                                cellQueue.add(new ModifiedCell(modifiedCell.row - 1, modifiedCell.column,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                        if(modifiedCell.row < 8){
                            if(!cells[modifiedCell.row][modifiedCell.column].bottomBlocked &&
                                    !searchedCells[modifiedCell.row + 1][modifiedCell.column])
                                cellQueue.add(new ModifiedCell(modifiedCell.row + 1, modifiedCell.column,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                        if(modifiedCell.column < 8){
                            if(!cells[modifiedCell.row][modifiedCell.column].rightBlocked &&
                                    !searchedCells[modifiedCell.row][modifiedCell.column + 1])
                                cellQueue.add(new ModifiedCell(modifiedCell.row, modifiedCell.column + 1,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                    }else{
                        if(modifiedCell.column < 8){
                            if(!cells[modifiedCell.row][modifiedCell.column].rightBlocked &&
                                    !searchedCells[modifiedCell.row][modifiedCell.column + 1])
                                cellQueue.add(new ModifiedCell(modifiedCell.row, modifiedCell.column + 1,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                        if (modifiedCell.row > 0){
                            if(!cells[modifiedCell.row][modifiedCell.column].topBlocked &&
                                    !searchedCells[modifiedCell.row - 1][modifiedCell.column])
                                cellQueue.add(new ModifiedCell(modifiedCell.row - 1, modifiedCell.column,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                        if(modifiedCell.row < 8){
                            if(!cells[modifiedCell.row][modifiedCell.column].bottomBlocked &&
                                    !searchedCells[modifiedCell.row + 1][modifiedCell.column])
                                cellQueue.add(new ModifiedCell(modifiedCell.row + 1, modifiedCell.column,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                        if(modifiedCell.column > 0){
                            if(!cells[modifiedCell.row][modifiedCell.column].leftBlocked &&
                                    !searchedCells[modifiedCell.row][modifiedCell.column - 1])
                                cellQueue.add(new ModifiedCell(modifiedCell.row, modifiedCell.column - 1,
                                        modifiedCell, modifiedCell.cost + 1));
                        }
                    }
                }
            }
        }
        return -1;
    }

    static boolean isWin(ModifiedCell modifiedCell, boolean isRow, int goalLine) {
        if (isRow && modifiedCell.row == goalLine)
            return true;
        else if (!isRow && modifiedCell.column == goalLine)
            return true;
        else
            return false;
    }
}


class ModifiedCell {
    ModifiedCell father;
    int row, column;
    int cost;

    ModifiedCell(int row, int column, ModifiedCell father, int cost){
        this.row = row;
        this.column = column;
        this.father = father;
        this.cost = cost;
    }
}
class Player extends Circle {
    Coordinates coordinates;
    int playerNumber, now, goalLine; //NoW = number of walls deployed
    Cell currentCell;
    boolean isAI;
    boolean isRow;

    Player(Coordinates c, int radius, Color color, int playerNumber, Cell currentCell, boolean isAI, int goalLine,
           boolean isRow) {
        super(radius, color);
        this.coordinates = c;
        this.playerNumber = playerNumber;
        this.currentCell = currentCell;
        this.isAI = isAI;
        this.goalLine = goalLine;
        now = 0;
        this.isRow = isRow;
        setTranslateX(c.x);
        setTranslateY(c.y);
    }
}


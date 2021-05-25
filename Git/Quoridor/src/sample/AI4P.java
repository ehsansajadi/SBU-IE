package sample;

public class AI4P {
    Node4P currentNode;
    int maxDepth = 4, maxChild = 141, distanceFactor = -75, nowFactor = -17, moveFactor = 10, maxWalls = 5,
            repeatFactor = 120;
    Cell[][] cells;
    Brick[][] horizontalBricks;
    Brick[][] verticalBricks;
    boolean[][] emptyJoints;
    boolean[][] searchedCells = new boolean[9][9];
    Move repeatMove = null;

    AI4P(Node4P currentNode, Cell[][] cells, Brick[][] horizontalBricks,
         Brick[][] verticalBricks, boolean[][] emptyJoints, Move aiLastMove) {
        this.currentNode = currentNode;
        this.cells = cells;
        this.horizontalBricks = horizontalBricks;
        this.verticalBricks = verticalBricks;
        this.emptyJoints = emptyJoints;
        if (aiLastMove != null && aiLastMove.changePos) {
            if (aiLastMove.direction == Direction.up)
                repeatMove = new Move(Direction.down);
            else if (aiLastMove.direction == Direction.upUp) {
                repeatMove = new Move(Direction.downDown);
            } else if (aiLastMove.direction == Direction.upLeft) {
                repeatMove = new Move(Direction.downRight);
            } else if (aiLastMove.direction == Direction.upRight) {
                repeatMove = new Move(Direction.downLeft);
            } else if (aiLastMove.direction == Direction.down) {
                repeatMove = new Move(Direction.up);
            } else if (aiLastMove.direction == Direction.downDown) {
                repeatMove = new Move(Direction.upUp);
            } else if (aiLastMove.direction == Direction.downRight) {
                repeatMove = new Move(Direction.upLeft);
            } else if (aiLastMove.direction == Direction.downLeft) {
                repeatMove = new Move(Direction.upRight);
            } else if (aiLastMove.direction == Direction.left) {
                repeatMove = new Move(Direction.right);
            } else if (aiLastMove.direction == Direction.leftLeft) {
                repeatMove = new Move(Direction.rightRight);
            } else if (aiLastMove.direction == Direction.right) {
                repeatMove = new Move(Direction.left);
            } else if (aiLastMove.direction == Direction.rightRight) {
                repeatMove = new Move(Direction.leftLeft);
            }
        }
    }

    Move play() {
        System.out.println("Play is called ");
        makeMinMaxTree(currentNode, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (currentNode.nextMove.changePos)
            System.out.println("AI move : " + currentNode.nextMove.direction);
        else
            System.out.println("AI move : " + currentNode.nextMove.firstBrick.row + " " + currentNode.nextMove.firstBrick.column +
                    " " + currentNode.nextMove.firstBrick.isHorizontal);
        return currentNode.nextMove;
    }

    int makeMinMaxTree(Node4P node4P, int currentDepth, int turn, int alpha, int beta) {
        /*node4P.scoreAI = Integer.MIN_VALUE;
        node4P.scoreP1 = Integer.MIN_VALUE;
        node4P.scoreP2 = Integer.MIN_VALUE;
        node4P.scoreP3 = Integer.MIN_VALUE;*/
        if(turn == 0)
            node4P.score = Integer.MAX_VALUE;
        else
            node4P.score = Integer.MIN_VALUE;
        int counter = 0;
        boolean canMakeMore = true;
        if (currentDepth < maxDepth) {
            while (canMakeMore) {
                Node4P newNode = null;
                while (newNode == null && canMakeMore) {
                    newNode = extendNode(node4P, counter, turn);
                    counter++;
                    if (counter == maxChild)
                        canMakeMore = false;
                }
                if (!canMakeMore && newNode == null)
                    break;
                node4P.childNode = newNode;
                changeArrays(node4P.childNode, node4P.childNode.lastMove, turn);
                //int[] tempScore;
                int tempScore;
                if (turn == 3) {
                    tempScore = makeMinMaxTree(node4P.childNode, currentDepth + 1, 0, alpha, beta);
                } else {
                    tempScore = makeMinMaxTree(node4P.childNode, currentDepth + 1, turn + 1, alpha, beta);
                }
                //if (repeatMove != null && turn == 0 && currentDepth == 4 && node4P.childNode.lastMove.Equals(repeatMove)) {
                //    tempScore -= repeatFactor;
                //}
                reverseChangeArrays(node4P.childNode, node4P.childNode.lastMove, turn);
                /*node4P.childNode.scoreAI = tempScore[0];
                node4P.childNode.scoreP1 = tempScore[1];
                node4P.childNode.scoreP2 = tempScore[2];
                node4P.childNode.scoreP3 = tempScore[3];
                if((turn == 0 && node4P.childNode.scoreAI > node4P.scoreAI) ||
                        (turn == 1 && node4P.childNode.scoreP1 > node4P.scoreP1) ||
                        (turn == 2 && node4P.childNode.scoreP2 > node4P.scoreP2) ||
                        (turn == 3 && node4P.childNode.scoreP3 > node4P.scoreP3)){
                    node4P.nextMove = node4P.childNode.lastMove;
                    node4P.scoreAI = node4P.childNode.scoreAI;
                    node4P.scoreP1 = node4P.childNode.scoreP1;
                    node4P.scoreP2 = node4P.childNode.scoreP2;
                    node4P.scoreP3 = node4P.childNode.scoreP3;
                }*/
                node4P.childNode.score = tempScore;
                if(turn == 0 && node4P.score < node4P.childNode.score){
                    node4P.nextMove = node4P.childNode.lastMove;
                    node4P.score = node4P.childNode.score;
                }else if(turn != 0 && node4P.score > node4P.childNode.score){
                    node4P.nextMove = node4P.childNode.lastMove;
                    node4P.score = node4P.childNode.score;
                }
                if (turn == 0 && alpha < newNode.score)
                    alpha = tempScore;
                if (turn != 0 && beta > newNode.score)
                    beta = tempScore;
                if (beta < alpha) {
                    //System.out.println("Pruned depth : "+currentDepth+" counter : "+counter);
                    break;
                }
            }
        } else {
            node4P.score =  heuristicFunction4P(node4P);
        }
        /*int[] temp = new int[4];
        temp[0] = node4P.scoreAI;
        temp[1] = node4P.scoreP1;
        temp[2] = node4P.scoreP2;
        temp[3] = node4P.scoreP3;*/
        return node4P.score;
    }


    Node4P extendNode(Node4P node4P, int counter, int turn) {
        if (counter == 0) {
            if (turn == 0 && node4P.ai.currentCell.row > 0) {
                Cell topCell1 = cells[node4P.ai.currentCell.row - 1][node4P.ai.currentCell.column];
                if (topCell1.isValidCell(cells, node4P.ai.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.up));
                }
            } else if (turn == 1 && node4P.p1.currentCell.row > 0) {
                Cell topCell1 = cells[node4P.p1.currentCell.row - 1][node4P.p1.currentCell.column];
                if (topCell1.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.up));
                }
            }else if (turn == 2 && node4P.p2.currentCell.row > 0) {
                Cell topCell1 = cells[node4P.p2.currentCell.row - 1][node4P.p2.currentCell.column];
                if (topCell1.isValidCell(cells, node4P.p2.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.up));
                }
            }else if (turn == 3 && node4P.p3.currentCell.row > 0) {
                Cell topCell1 = cells[node4P.p3.currentCell.row - 1][node4P.p3.currentCell.column];
                if (topCell1.isValidCell(cells, node4P.p3.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.up));
                }
            }
            return null;
        } else if (counter == 1) {
            if (turn == 0 && node4P.ai.currentCell.row > 1) {
                Cell topCell2 = cells[node4P.ai.currentCell.row - 2][node4P.ai.currentCell.column];
                if (topCell2.isValidCell(cells, node4P.ai.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.upUp));
                }
            } else if (turn == 1 && node4P.p1.currentCell.row > 1) {
                Cell topCell2 = cells[node4P.p1.currentCell.row - 2][node4P.p1.currentCell.column];
                if (topCell2.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.upUp));
                }
            } else if (turn == 2 && node4P.p2.currentCell.row > 1) {
                Cell topCell2 = cells[node4P.p2.currentCell.row - 2][node4P.p2.currentCell.column];
                if (topCell2.isValidCell(cells, node4P.p2.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.upUp));
                }
            } else if (turn == 3 && node4P.p3.currentCell.row > 1) {
                Cell topCell2 = cells[node4P.p3.currentCell.row - 2][node4P.p3.currentCell.column];
                if (topCell2.isValidCell(cells, node4P.p3.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.upUp));
                }
            }
            return null;
        } else if (counter == 2) {
            if (turn == 0 && node4P.ai.currentCell.row > 0 && node4P.ai.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.ai.currentCell.row - 1][node4P.ai.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node4P.ai.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.upLeft));
                }
            } else if (turn == 1 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row - 1][node4P.p1.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.upLeft));
                }
            } else if (turn == 2 && node4P.p2.currentCell.row > 0 && node4P.p2.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p2.currentCell.row - 1][node4P.p2.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node4P.p2.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.upLeft));
                }
            } else if (turn == 3 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row - 1][node4P.p1.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.upLeft));
                }
            }
            return null;
        } else if (counter == 3) {
            if (turn == 0 && node4P.ai.currentCell.row > 0 && node4P.ai.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.ai.currentCell.row - 1][node4P.ai.currentCell.column + 1];
                if (topCell3.isValidCell(cells, node4P.ai.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.upRight));
                }
            } else if (turn == 1 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row - 1][node4P.p1.currentCell.column + 1];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.upRight));
                }
            } else if (turn == 2 && node4P.p2.currentCell.row > 0 && node4P.p2.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p2.currentCell.row - 1][node4P.p2.currentCell.column + 1];
                if (topCell3.isValidCell(cells, node4P.p2.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.upRight));
                }
            } else if (turn == 3 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row - 1][node4P.p1.currentCell.column + 1];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.upRight));
                }
            }

            return null;
        } else if (counter == 4) {
            if (turn == 0 && node4P.ai.currentCell.row > 0 && node4P.ai.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.ai.currentCell.row + 1][node4P.ai.currentCell.column];
                if (topCell3.isValidCell(cells, node4P.ai.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.down));
                }
            } else if (turn == 1 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row + 1][node4P.p1.currentCell.column];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.down));
                }
            } else if (turn == 2 && node4P.p2.currentCell.row > 0 && node4P.p2.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p2.currentCell.row + 1][node4P.p2.currentCell.column];
                if (topCell3.isValidCell(cells, node4P.p2.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.down));
                }
            } else if (turn == 3 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row + 1][node4P.p1.currentCell.column];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.down));
                }
            }
            return null;
        } else if (counter == 5) {
            if (turn == 0 && node4P.ai.currentCell.row > 0 && node4P.ai.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.ai.currentCell.row + 2][node4P.ai.currentCell.column];
                if (topCell3.isValidCell(cells, node4P.ai.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.downDown));
                }
            } else if (turn == 1 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row + 2][node4P.p1.currentCell.column];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.downDown));
                }
            } else if (turn == 2 && node4P.p2.currentCell.row > 0 && node4P.p2.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p2.currentCell.row + 2][node4P.p2.currentCell.column];
                if (topCell3.isValidCell(cells, node4P.p2.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.downDown));
                }
            } else if (turn == 3 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row + 2][node4P.p1.currentCell.column];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.downDown));
                }
            }
            return null;
        } else if (counter == 6) {
            if (turn == 0 && node4P.ai.currentCell.row > 0 && node4P.ai.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.ai.currentCell.row + 1][node4P.ai.currentCell.column + 1];
                if (topCell3.isValidCell(cells, node4P.ai.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.downRight));
                }
            } else if (turn == 1 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row + 1][node4P.p1.currentCell.column + 1];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.downRight));
                }
            } else if (turn == 2 && node4P.p2.currentCell.row > 0 && node4P.p2.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p2.currentCell.row + 1][node4P.p2.currentCell.column + 1];
                if (topCell3.isValidCell(cells, node4P.p2.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.downRight));
                }
            } else if (turn == 3 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row + 1][node4P.p1.currentCell.column + 1];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.downRight));
                }
            }
            return null;
        } else if (counter == 7) {
            if (turn == 0 && node4P.ai.currentCell.row > 0 && node4P.ai.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.ai.currentCell.row + 1][node4P.ai.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node4P.ai.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.downLeft));
                }
            } else if (turn == 1 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row + 1][node4P.p1.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.downLeft));
                }
            } else if (turn == 2 && node4P.p2.currentCell.row > 0 && node4P.p2.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p2.currentCell.row + 1][node4P.p2.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node4P.p2.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.downLeft));
                }
            } else if (turn == 3 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row + 1][node4P.p1.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.downLeft));
                }
            }
            return null;
        } else if (counter == 8) {
            if (turn == 0 && node4P.ai.currentCell.row > 0 && node4P.ai.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.ai.currentCell.row][node4P.ai.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node4P.ai.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.left));
                }
            } else if (turn == 1 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row][node4P.p1.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.left));
                }
            } else if (turn == 2 && node4P.p2.currentCell.row > 0 && node4P.p2.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p2.currentCell.row][node4P.p2.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node4P.p2.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.left));
                }
            } else if (turn == 3 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row][node4P.p1.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.left));
                }
            }
            return null;
        } else if (counter == 9) {
            if (turn == 0 && node4P.ai.currentCell.row > 0 && node4P.ai.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.ai.currentCell.row][node4P.ai.currentCell.column - 2];
                if (topCell3.isValidCell(cells, node4P.ai.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.leftLeft));
                }
            } else if (turn == 1 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row][node4P.p1.currentCell.column - 2];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.leftLeft));
                }
            } else if (turn == 2 && node4P.p2.currentCell.row > 0 && node4P.p2.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p2.currentCell.row][node4P.p2.currentCell.column - 2];
                if (topCell3.isValidCell(cells, node4P.p2.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.leftLeft));
                }
            } else if (turn == 3 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row][node4P.p1.currentCell.column - 2];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.leftLeft));
                }
            }
        } else if (counter == 10) {
            if (turn == 0 && node4P.ai.currentCell.row > 0 && node4P.ai.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.ai.currentCell.row][node4P.ai.currentCell.column + 1];
                if (topCell3.isValidCell(cells, node4P.ai.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.right));
                }
            } else if (turn == 1 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row][node4P.p1.currentCell.column + 1];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.right));
                }
            } else if (turn == 2 && node4P.p2.currentCell.row > 0 && node4P.p2.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p2.currentCell.row][node4P.p2.currentCell.column + 1];
                if (topCell3.isValidCell(cells, node4P.p2.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.right));
                }
            } else if (turn == 3 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row][node4P.p1.currentCell.column + 1];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.right));
                }
            }
            return null;
        } else if (counter == 11) {
            if (turn == 0 && node4P.ai.currentCell.row > 0 && node4P.ai.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.ai.currentCell.row][node4P.ai.currentCell.column + 2];
                if (topCell3.isValidCell(cells, node4P.ai.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.rightRight));
                }
            } else if (turn == 1 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row][node4P.p1.currentCell.column + 2];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.rightRight));
                }
            } else if (turn == 2 && node4P.p2.currentCell.row > 0 && node4P.p2.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p2.currentCell.row][node4P.p2.currentCell.column + 2];
                if (topCell3.isValidCell(cells, node4P.p2.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.rightRight));
                }
            } else if (turn == 3 && node4P.p1.currentCell.row > 0 && node4P.p1.currentCell.column > 0) {
                Cell topCell3 = cells[node4P.p1.currentCell.row][node4P.p1.currentCell.column + 2];
                if (topCell3.isValidCell(cells, node4P.p1.currentCell)) {
                    return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(Direction.rightRight));
                }
            }
            return null;
        } else if (counter < 140) { //11 + 64 + 64 = 139
            int c;
            if ((turn == 0 && node4P.ai.now < maxWalls) || (turn == 1 && node4P.p1.now < maxWalls) ||
                    (turn == 2 && node4P.p2.now < maxWalls) || (turn == 3 && node4P.p3.now < maxWalls)) {
                if (counter < 76) {
                    c = counter - 11;
                    int row = c / 9;
                    int column = c % 8;
                    boolean makeWall = horizontalBricks[row][column].isValidWall(horizontalBricks[row][column + 1])
                            && horizontalBricks[row][column].isValidJoint(horizontalBricks[row][column + 1], emptyJoints)
                            && Cell.canReachDestination4P(cells, searchedCells, node4P.p1, node4P.p2, node4P.ai, node4P.p3);
                    if (makeWall) {
                        return createWall(node4P, horizontalBricks[row][column], horizontalBricks[row][column + 1], turn);
                    }
                } else {
                    c = counter - 75;
                    int row = c / 9;
                    int column = c % 8;
                    boolean makeWall = verticalBricks[row][column].isValidWall(verticalBricks[row + 1][column])
                            && verticalBricks[row][column].isValidJoint(verticalBricks[row + 1][column], emptyJoints)
                            && Cell.canReachDestination4P(cells, searchedCells, node4P.p1, node4P.p2, node4P.ai, node4P.p3);
                    if (makeWall) {
                        return createWall(node4P, verticalBricks[row][column], verticalBricks[row + 1][column], turn);
                    }
                }
            }
        }
        return null;
    }

    int heuristicFunction4P(Node4P node4P) {
        /*if (((node4P.ai.isRow) && node4P.ai.currentCell.row == node4P.ai.goalLine) ||
                ((!node4P.ai.isRow) && node4P.ai.currentCell.column == node4P.ai.goalLine)) {
            node4P.scoreAI = 5000;
            node4P.scoreP1 = -5000;
            node4P.scoreP2 = -5000;
            node4P.scoreP3 = -5000;
        }
        if (((node4P.p1.isRow) && node4P.p1.currentCell.row == node4P.p1.goalLine) ||
        ((!node4P.p1.isRow) && node4P.p1.currentCell.column == node4P.p1.goalLine))
        {
            node4P.scoreAI = -5000;
            node4P.scoreP1 = 5000;
            node4P.scoreP2 = -5000;
            node4P.scoreP3 = -5000;
        }
        if(((node4P.p2.isRow) && node4P.p2.currentCell.row == node4P.p2.goalLine) ||
                ((!node4P.p2.isRow) && node4P.p2.currentCell.column == node4P.p2.goalLine)){
            node4P.scoreAI = -5000;
            node4P.scoreP1 = -5000;
            node4P.scoreP2 = 5000;
            node4P.scoreP3 = -5000;
        }
        if(((node4P.p3.isRow) && node4P.p3.currentCell.row == node4P.p3.goalLine) ||
                ((!node4P.p3.isRow) && node4P.p3.currentCell.column == node4P.p3.goalLine)){
            node4P.scoreAI = -5000;
            node4P.scoreP1 = -5000;
            node4P.scoreP2 = -5000;
            node4P.scoreP3 = +5000;
        }*/
        int aiDistance, p1Distance, p2Distance, p3Distance;
        boolean[][] searchedCells = new boolean[9][9];
        int aiPossibleMoves = countPossibleMoves(node4P.ai.currentCell);
        int p1PossibleMoves = countPossibleMoves(node4P.p1.currentCell);
        int p2PossibleMoves = countPossibleMoves(node4P.p2.currentCell);
        int p3PossibleMoves = countPossibleMoves(node4P.p3.currentCell);
        Cell.resetSeachedCells(searchedCells);
        aiDistance = Cell.bestCostPath(cells, searchedCells, node4P.ai.currentCell.row, node4P.ai.currentCell.column, node4P.ai.isRow, node4P.ai.goalLine);
        Cell.resetSeachedCells(searchedCells);
        p1Distance = Cell.bestCostPath(cells, searchedCells, node4P.p1.currentCell.row, currentNode.p1.currentCell.column, node4P.p1.isRow, node4P.p1.goalLine);
        Cell.resetSeachedCells(searchedCells);
        p2Distance = Cell.bestCostPath(cells, searchedCells, node4P.p2.currentCell.row, currentNode.p2.currentCell.column, node4P.p2.isRow, node4P.p2.goalLine);
        Cell.resetSeachedCells(searchedCells);
        p3Distance = Cell.bestCostPath(cells, searchedCells, node4P.p3.currentCell.row, currentNode.p3.currentCell.column, node4P.p3.isRow, node4P.p3.goalLine);
        /*node4P.scoreAI = distanceFactor * (aiDistance - p1Distance - p2Distance - p3Distance) +
                nowFactor * (node4P.ai.now - node4P.p1.now - node4P.p2.now - node4P.p3.now) +
                (aiPossibleMoves - p1PossibleMoves - p2PossibleMoves - p3PossibleMoves) * moveFactor;
        node4P.scoreP1 = distanceFactor * (p1Distance - aiDistance - p2Distance - p3Distance) +
                nowFactor * (node4P.p1.now - node4P.ai.now - node4P.p2.now - node4P.p3.now) +
                (p1PossibleMoves - aiPossibleMoves - p2PossibleMoves - p3PossibleMoves) * moveFactor;
        node4P.scoreP2 = distanceFactor * (p2Distance - p1Distance - aiDistance - p3Distance) +
                nowFactor * (node4P.p2.now - node4P.p1.now - node4P.ai.now - node4P.p3.now) +
                (p2PossibleMoves - p1PossibleMoves - aiPossibleMoves - p3PossibleMoves) * moveFactor;
        node4P.scoreP3 = distanceFactor * (p3Distance - p1Distance - p2Distance - aiDistance) +
                nowFactor * (node4P.p3.now - node4P.p1.now - node4P.p2.now - node4P.ai.now) +
                (p3PossibleMoves - p1PossibleMoves - p2PossibleMoves - aiPossibleMoves) * moveFactor;
                */
        node4P.score = distanceFactor * (aiDistance - p1Distance - p2Distance - p3Distance) +
                nowFactor * (node4P.ai.now - node4P.p1.now - node4P.p2.now - node4P.p3.now) +
                (aiPossibleMoves - p1PossibleMoves - p2PossibleMoves - p3PossibleMoves) * moveFactor;
        return node4P.score;
    }

    int countPossibleMoves(Cell cell) {
        int counter = 0;
        if (cell.row < 8 && cells[cell.row + 1][cell.column].isValidCell(cells, cell))
            counter++;
        if (cell.row < 7 && cells[cell.row + 2][cell.column].isValidCell(cells, cell))
            counter++;
        if (cell.row < 8 && cell.column < 8 && cells[cell.row + 1][cell.column + 1].isValidCell(cells, cell))
            counter++;
        if (cell.row < 8 && cell.column > 0 && cells[cell.row + 1][cell.column - 1].isValidCell(cells, cell))
            counter++;
        if (cell.row > 0 && cells[cell.row - 1][cell.column].isValidCell(cells, cell))
            counter++;
        if (cell.row > 1 && cells[cell.row - 2][cell.column].isValidCell(cells, cell))
            counter++;
        if (cell.row > 0 && cell.column > 0 && cells[cell.row - 1][cell.column - 1].isValidCell(cells, cell))
            counter++;
        if (cell.row > 0 && cell.column < 8 && cells[cell.row - 1][cell.column + 1].isValidCell(cells, cell))
            counter++;
        if (cell.column < 8 && cells[cell.row][cell.column + 1].isValidCell(cells, cell))
            counter++;
        if (cell.column > 0 && cells[cell.row][cell.column - 1].isValidCell(cells, cell))
            counter++;
        if (cell.column < 7 && cells[cell.row][cell.column + 2].isValidCell(cells, cell))
            counter++;
        if (cell.column > 1 && cells[cell.row][cell.column - 2].isValidCell(cells, cell))
            counter++;
        return counter;
    }

    Node4P createWall(Node4P node4P, Brick b1, Brick b2, int turn) {
        boolean[][] searchedCells = new boolean[9][9];
        Cell.resetSeachedCells(searchedCells);
        if (b1.isHorizontal) {
            cells[b1.row][b1.column].bottomBlocked = true;
            cells[b1.row + 1][b1.column].topBlocked = true;
            cells[b1.row][b1.column + 1].bottomBlocked = true;
            cells[b1.row + 1][b1.column + 1].topBlocked = true;
            horizontalBricks[b1.row][b1.column].isSolid = true;
            horizontalBricks[b1.row][b1.column + 1].isSolid = true;
            emptyJoints[b1.row][b1.column] = false;
            if (Cell.canReachDestination4P(cells, searchedCells, node4P.ai, node4P.p1, node4P.p2, node4P.p3)) {
                if (turn == 0)
                    node4P.ai.now++;
                else if(turn == 1)
                    node4P.p1.now++;
                else if(turn == 2)
                    node4P.p2.now++;
                else
                    node4P.p3.now++;

                return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(b1, b2));
            }
            emptyJoints[b1.row][b1.column] = true;
            horizontalBricks[b1.row][b1.column].isSolid = false;
            horizontalBricks[b1.row][b1.column + 1].isSolid = false;
            cells[b1.row][b1.column].bottomBlocked = false;
            cells[b1.row + 1][b1.column].topBlocked = false;
            cells[b1.row][b1.column + 1].bottomBlocked = false;
            cells[b1.row + 1][b1.column + 1].topBlocked = false;
        } else {
            cells[b1.row][b1.column].rightBlocked = true;
            cells[b1.row][b1.column + 1].leftBlocked = true;
            cells[b1.row + 1][b1.column].rightBlocked = true;
            cells[b1.row + 1][b1.column + 1].leftBlocked = true;
            verticalBricks[b1.row][b1.column].isSolid = true;
            verticalBricks[b1.row + 1][b1.column].isSolid = true;
            emptyJoints[b1.row][b1.column] = false;
            if (Cell.canReachDestination4P(cells, searchedCells, node4P.ai, node4P.p1, node4P.p2, node4P.p3)) {
                if (turn == 0)
                    node4P.ai.now++;
                else if(turn == 1)
                    node4P.p1.now++;
                else if(turn == 2)
                    node4P.p2.now++;
                else
                    node4P.p3.now++;

                emptyJoints[b1.row][b1.column] = false;
                return new Node4P(node4P.ai, node4P.p1, node4P.p2, node4P.p3, new Move(b1, b2));
            }
            emptyJoints[b1.row][b1.column] = true;
            cells[b1.row][b1.column].rightBlocked = false;
            cells[b1.row][b1.column + 1].leftBlocked = false;
            cells[b1.row + 1][b1.column].rightBlocked = false;
            cells[b1.row + 1][b1.column + 1].leftBlocked = false;
            verticalBricks[b1.row][b1.column].isSolid = false;
            verticalBricks[b1.row + 1][b1.column].isSolid = false;
        }
        return null;
    }

    void changeArrays(Node4P node4P, Move move, int turn) {
        Cell aiCell = node4P.ai.currentCell;
        Cell p1Cell = node4P.p1.currentCell;
        Cell p2Cell = node4P.p2.currentCell;
        Cell p3Cell = node4P.p3.currentCell;
        if (move.changePos) {
            if (move.direction == Direction.up) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 1][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row - 1][p1Cell.column];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row - 1][p2Cell.column];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row - 1][p3Cell.column];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.upUp) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 2][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row - 2][p1Cell.column];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row - 2][p2Cell.column];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row - 2][p3Cell.column];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.upRight) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 1][aiCell.column + 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row - 1][p1Cell.column + 1];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row - 1][p2Cell.column + 1];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row - 1][p3Cell.column + 1];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.upLeft) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 1][aiCell.column - 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row - 1][p1Cell.column - 1];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row - 1][p2Cell.column - 1];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row - 1][p3Cell.column - 1];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.down) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 1][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row + 1][p1Cell.column];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row + 1][p2Cell.column];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row + 1][p3Cell.column];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.downDown) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 2][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row + 2][p1Cell.column];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row + 2][p2Cell.column];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row + 2][p3Cell.column];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.downRight) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 1][aiCell.column + 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row + 1][p1Cell.column + 1];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row + 1][p2Cell.column + 1];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row + 1][p3Cell.column + 1];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.downLeft) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 1][aiCell.column - 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row + 1][p1Cell.column - 1];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row + 1][p2Cell.column - 1];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row + 1][p3Cell.column - 1];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.right) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column + 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row][p1Cell.column + 1];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row][p2Cell.column + 1];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row][p3Cell.column + 1];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.rightRight) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column + 2];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row][p1Cell.column + 2];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row][p2Cell.column + 2];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row][p3Cell.column + 2];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.left) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column - 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row][p1Cell.column - 1];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row][p2Cell.column - 1];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row][p3Cell.column - 1];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.leftLeft) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column - 2];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row][p1Cell.column - 2];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row][p2Cell.column - 2];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row][p3Cell.column - 2];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            }
        }
        node4P.ai.currentCell = aiCell;
        node4P.p1.currentCell = p1Cell;
        node4P.p2.currentCell = p2Cell;
        node4P.p3.currentCell = p3Cell;
    }

    void reverseChangeArrays(Node4P node4P, Move move, int turn) {
        Cell aiCell = node4P.ai.currentCell;
        Cell p1Cell = node4P.p1.currentCell;
        Cell p2Cell = node4P.p2.currentCell;
        Cell p3Cell = node4P.p3.currentCell;
        if (move.changePos) {
            if (move.direction == Direction.up) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 1][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row + 1][p1Cell.column];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row + 1][p2Cell.column];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row + 1][p3Cell.column];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.upUp) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 2][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row + 2][p1Cell.column];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row + 2][p2Cell.column];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row + 2][p3Cell.column];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.upRight) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 1][aiCell.column - 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row + 1][p1Cell.column - 1];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row + 1][p2Cell.column - 1];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row + 1][p3Cell.column - 1];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.upLeft) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 1][aiCell.column + 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row + 1][p1Cell.column + 1];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row + 1][p2Cell.column + 1];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row + 1][p3Cell.column + 1];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.down) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 1][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row - 1][p1Cell.column];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row - 1][p2Cell.column];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row - 1][p3Cell.column];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.downDown) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 2][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row - 2][p1Cell.column];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row - 2][p2Cell.column];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row - 2][p3Cell.column];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.downRight) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 1][aiCell.column - 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row - 1][p1Cell.column - 1];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row - 1][p2Cell.column - 1];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row - 1][p3Cell.column - 1];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.downLeft) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 1][aiCell.column + 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row - 1][p1Cell.column + 1];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row - 1][p2Cell.column + 1];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row - 1][p3Cell.column + 1];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.right) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column - 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row][p1Cell.column - 1];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row][p2Cell.column - 1];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row][p3Cell.column - 1];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.rightRight) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column - 2];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row][p1Cell.column - 2];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row][p2Cell.column - 2];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row][p3Cell.column - 2];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.left) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column + 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row][p1Cell.column + 1];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row][p2Cell.column + 1];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row][p3Cell.column + 1];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            } else if (move.direction == Direction.leftLeft) {
                if (turn == 0) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column + 2];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else if(turn == 1){
                    cells[p1Cell.row][p1Cell.column].isFilled = false;
                    p1Cell = cells[p1Cell.row][p1Cell.column + 2];
                    cells[p1Cell.row][p1Cell.column].isFilled = true;
                } else if(turn == 2){
                    cells[p2Cell.row][p2Cell.column].isFilled = false;
                    p2Cell = cells[p2Cell.row][p2Cell.column + 2];
                    cells[p2Cell.row][p2Cell.column].isFilled = true;
                } else if(turn == 3){
                    cells[p3Cell.row][p3Cell.column].isFilled = false;
                    p3Cell = cells[p3Cell.row][p3Cell.column + 2];
                    cells[p3Cell.row][p3Cell.column].isFilled = true;
                }
            }
        } else {
            Brick b1 = move.firstBrick;
            if (move.firstBrick.isHorizontal) {
                cells[b1.row][b1.column].bottomBlocked = false;
                cells[b1.row + 1][b1.column].topBlocked = false;
                cells[b1.row][b1.column + 1].bottomBlocked = false;
                cells[b1.row + 1][b1.column + 1].topBlocked = false;
                horizontalBricks[b1.row][b1.column].isSolid = false;
                horizontalBricks[b1.row][b1.column + 1].isSolid = false;
            } else {
                cells[b1.row][b1.column].rightBlocked = false;
                cells[b1.row][b1.column + 1].leftBlocked = false;
                cells[b1.row + 1][b1.column].rightBlocked = false;
                cells[b1.row + 1][b1.column + 1].leftBlocked = false;
                verticalBricks[b1.row][b1.column].isSolid = false;
                verticalBricks[b1.row + 1][b1.column].isSolid = false;
            }
            emptyJoints[b1.row][b1.column] = true;
            if (turn == 0)
                node4P.ai.now--;
            else if(turn == 1)
                node4P.p1.now--;
            else if(turn == 2)
                node4P.p2.now--;
            else
                node4P.p3.now--;
        }
        node4P.ai.currentCell = aiCell;
        node4P.p1.currentCell = p1Cell;
        node4P.p2.currentCell = p2Cell;
        node4P.p3.currentCell = p3Cell;
    }
}
package sample;

public class AI2P {
    Node2P currentNode;
    int distanceFactor = -75, nowFactor = -17, moveFactor = 10, repeatFactor = 120;
    int maxDepth = 4, maxChild = 141, maxWalls = 10;
    Cell[][] cells;
    Brick[][] horizontalBricks;
    Brick[][] verticalBricks;
    boolean[][] emptyJoints;
    boolean[][] searchedCells = new boolean[9][9];
    Move repeatMove = null;

    /*AI2P(Node2P currentNode, Cell[][] cells, Brick[][] horizontalBricks,
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
    }*/
    AI2P(Node2P currentNode, Cell[][] cells, Brick[][] horizontalBricks,
         Brick[][] verticalBricks, boolean[][] emptyJoints, Move aiLastMove, int distanceFactor,
         int nowFactor, int moveFactor, int repeatFactor){
        this.currentNode = currentNode;
        this.cells = cells;
        this.horizontalBricks = horizontalBricks;
        this.verticalBricks = verticalBricks;
        this.emptyJoints = emptyJoints;
        this.distanceFactor = distanceFactor;
        this.nowFactor = nowFactor;
        this.moveFactor = moveFactor;
        this.repeatFactor = repeatFactor;
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
        int tempScore = makeMinMaxTree(currentNode, 0, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return currentNode.nextMove;
    }

    int makeMinMaxTree(Node2P node2P, int currentDepth, boolean aiTurn, int alpha, int beta) {
        if (aiTurn)
            node2P.score = Integer.MIN_VALUE;
        else
            node2P.score = Integer.MAX_VALUE;
        int counter = 0;
        boolean canMakeMore = true;
        if (currentDepth < maxDepth) {
            while (canMakeMore) {
                Node2P newNode = null;
                while (newNode == null && canMakeMore) {
                    newNode = extendNode(node2P, counter, aiTurn);
                    counter++;
                    if (counter == maxChild)
                        canMakeMore = false;
                }
                if (!canMakeMore && newNode == null)
                    break;
                node2P.childNode = newNode;
                changeArrays(node2P.childNode, node2P.childNode.lastMove, aiTurn);
                int tempScore = makeMinMaxTree(node2P.childNode, currentDepth + 1, !aiTurn, alpha, beta);
                if (repeatMove != null && aiTurn && currentDepth == 1 && node2P.childNode.lastMove.Equals(repeatMove)) {
                    tempScore -= repeatFactor;
                }
                reverseChangeArrays(node2P.childNode, node2P.childNode.lastMove, aiTurn);
                node2P.childNode.score = tempScore;
                if (aiTurn && tempScore > node2P.score) {
                    node2P.score = tempScore;
                    node2P.nextMove = node2P.childNode.lastMove;
                } else if (!aiTurn && tempScore < node2P.score) {
                    node2P.score = tempScore;
                    node2P.nextMove = node2P.childNode.lastMove;
                }
                if (aiTurn && alpha < newNode.score)
                    alpha = tempScore;
                if (!aiTurn && beta > newNode.score)
                    beta = tempScore;
                if (beta < alpha) {
                    //System.out.println("Pruned depth : "+currentDepth+" counter : "+counter);
                    break;
                }
            }
        } else {
            node2P.score = heuristicFunction2P(node2P, aiTurn);
        }
        return node2P.score;
    }


    Node2P extendNode(Node2P node2P, int counter, boolean aiTurn) {
        if (counter == 0) {
            if (aiTurn && node2P.ai.currentCell.row > 0) {
                Cell topCell1 = cells[node2P.ai.currentCell.row - 1][node2P.ai.currentCell.column];
                if (topCell1.isValidCell(cells, node2P.ai.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.up));
                }
            } else if (!aiTurn && node2P.enemy.currentCell.row > 0) {
                Cell topCell1 = cells[node2P.enemy.currentCell.row - 1][node2P.enemy.currentCell.column];
                if (topCell1.isValidCell(cells, node2P.enemy.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.up));
                }
            }
            return null;
        } else if (counter == 1) {
            if (aiTurn && node2P.ai.currentCell.row > 1) {
                Cell topCell2 = cells[node2P.ai.currentCell.row - 2][node2P.ai.currentCell.column];
                if (topCell2.isValidCell(cells, node2P.ai.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.upUp));
                }
            } else if (!aiTurn && node2P.enemy.currentCell.row > 1) {
                Cell topCell2 = cells[node2P.enemy.currentCell.row - 2][node2P.enemy.currentCell.column];
                if (topCell2.isValidCell(cells, node2P.enemy.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.upUp));
                }
            }
            return null;
        } else if (counter == 2) {
            if (aiTurn && node2P.ai.currentCell.row > 0 && node2P.ai.currentCell.column > 0) {
                Cell topCell3 = cells[node2P.ai.currentCell.row - 1][node2P.ai.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node2P.ai.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.upLeft));
                }
            } else if (!aiTurn && node2P.enemy.currentCell.row > 0 && node2P.enemy.currentCell.column > 0) {
                Cell topCell3 = cells[node2P.enemy.currentCell.row - 1][node2P.enemy.currentCell.column - 1];
                if (topCell3.isValidCell(cells, node2P.enemy.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.upLeft));
                }
            }
            return null;
        } else if (counter == 3) {
            if (aiTurn && node2P.ai.currentCell.row > 0 && node2P.ai.currentCell.column < 8) {
                Cell topCell4 = cells[node2P.ai.currentCell.row - 1][node2P.ai.currentCell.column + 1];
                if (topCell4.isValidCell(cells, node2P.ai.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.upRight));
                }
            } else if (!aiTurn && node2P.enemy.currentCell.row > 0 && node2P.enemy.currentCell.column < 8) {
                Cell topCell4 = cells[node2P.enemy.currentCell.row - 1][node2P.enemy.currentCell.column + 1];
                if (topCell4.isValidCell(cells, node2P.enemy.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.upRight));
                }
            }
            return null;
        } else if (counter == 4) {
            if (aiTurn && node2P.ai.currentCell.row < 8) {
                Cell bottomCell1 = cells[node2P.ai.currentCell.row + 1][node2P.ai.currentCell.column];
                if (bottomCell1.isValidCell(cells, node2P.ai.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.down));
                }
            } else if (!aiTurn && node2P.enemy.currentCell.row < 8) {
                Cell bottomCell1 = cells[node2P.enemy.currentCell.row + 1][node2P.enemy.currentCell.column];
                if (bottomCell1.isValidCell(cells, node2P.enemy.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.down));
                }
            }
            return null;
        } else if (counter == 5) {
            if (aiTurn && node2P.ai.currentCell.row < 7) {
                Cell bottomCell2 = cells[node2P.ai.currentCell.row + 2][node2P.ai.currentCell.column];
                if (bottomCell2.isValidCell(cells, node2P.ai.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.downDown));
                }
            } else if (!aiTurn && node2P.enemy.currentCell.row < 7) {
                Cell bottomCell2 = cells[node2P.enemy.currentCell.row + 2][node2P.enemy.currentCell.column];
                if (bottomCell2.isValidCell(cells, node2P.enemy.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.downDown));
                }
            }
            return null;
        } else if (counter == 6) {
            if (aiTurn && node2P.ai.currentCell.row < 8 && node2P.ai.currentCell.column < 8) {
                Cell bottomCell3 = cells[node2P.ai.currentCell.row + 1][node2P.ai.currentCell.column + 1];
                if (bottomCell3.isValidCell(cells, node2P.ai.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.downRight));
                }
            } else if (!aiTurn && node2P.enemy.currentCell.row < 8 && node2P.enemy.currentCell.column < 8) {
                Cell bottomCell3 = cells[node2P.enemy.currentCell.row + 1][node2P.enemy.currentCell.column + 1];
                if (bottomCell3.isValidCell(cells, node2P.enemy.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.downRight));
                }
            }
            return null;
        } else if (counter == 7) {
            if (aiTurn && node2P.ai.currentCell.row < 8 && node2P.ai.currentCell.column > 0) {
                Cell bottomCell4 = cells[node2P.ai.currentCell.row + 1][node2P.ai.currentCell.column - 1];
                if (bottomCell4.isValidCell(cells, node2P.ai.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.downLeft));
                }
            } else if (!aiTurn && node2P.enemy.currentCell.row < 8 && node2P.enemy.currentCell.column > 0) {
                Cell bottomCell4 = cells[node2P.enemy.currentCell.row + 1][node2P.enemy.currentCell.column - 1];
                if (bottomCell4.isValidCell(cells, node2P.enemy.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.downLeft));
                }
            }
            return null;
        } else if (counter == 8) {
            if (aiTurn && node2P.ai.currentCell.column > 0) {
                Cell leftCell1 = cells[node2P.ai.currentCell.row][node2P.ai.currentCell.column - 1];
                if (leftCell1.isValidCell(cells, node2P.ai.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.left));

                }
            } else if (!aiTurn && node2P.enemy.currentCell.column > 0) {
                Cell leftCell1 = cells[node2P.enemy.currentCell.row][node2P.enemy.currentCell.column - 1];
                if (leftCell1.isValidCell(cells, node2P.enemy.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.left));

                }
            }
            return null;
        } else if (counter == 9) {
            if (aiTurn && node2P.ai.currentCell.column > 1) {
                Cell leftCell2 = cells[node2P.ai.currentCell.row][node2P.ai.currentCell.column - 2];
                if (leftCell2.isValidCell(cells, node2P.ai.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.leftLeft));
                }
            } else if (!aiTurn && node2P.enemy.currentCell.column > 1) {
                Cell leftCell2 = cells[node2P.enemy.currentCell.row][node2P.enemy.currentCell.column - 2];
                if (leftCell2.isValidCell(cells, node2P.enemy.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.leftLeft));
                }
            }
        } else if (counter == 10) {
            if (aiTurn && node2P.ai.currentCell.column < 8) {
                Cell rightCell1 = cells[node2P.ai.currentCell.row][node2P.ai.currentCell.column + 1];
                if (rightCell1.isValidCell(cells, node2P.ai.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.right));
                }
            } else if (!aiTurn && node2P.enemy.currentCell.column < 8) {
                Cell rightCell1 = cells[node2P.enemy.currentCell.row][node2P.enemy.currentCell.column + 1];
                if (rightCell1.isValidCell(cells, node2P.enemy.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.right));
                }
            }
            return null;
        } else if (counter == 11) {
            if (aiTurn && node2P.ai.currentCell.column < 7) {
                Cell rightCell2 = cells[node2P.ai.currentCell.row][node2P.ai.currentCell.column + 2];
                if (rightCell2.isValidCell(cells, node2P.ai.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.rightRight));
                }
            } else if (!aiTurn && node2P.enemy.currentCell.column < 7) {
                Cell rightCell2 = cells[node2P.enemy.currentCell.row][node2P.enemy.currentCell.column + 2];
                if (rightCell2.isValidCell(cells, node2P.enemy.currentCell)) {
                    return new Node2P(node2P.ai, node2P.enemy, new Move(Direction.rightRight));
                }
            }
            return null;
        } else if (counter == 12) {
            if (aiTurn && node2P.ai.currentCell.row < 8 && node2P.ai.currentCell.column < 8 && node2P.ai.now < maxWalls) {
                Brick brick1 = horizontalBricks[node2P.ai.currentCell.row][node2P.ai.currentCell.column];
                Brick brick2 = horizontalBricks[node2P.ai.currentCell.row][node2P.ai.currentCell.column + 1];
                boolean makeWall = !node2P.priorityBricks.contains(brick2) && !node2P.priorityBricks.contains(brick1) &&
                        brick1.isValidWall(brick2) && brick1.isValidJoint(brick2, emptyJoints);
                if (makeWall) {
                    node2P.priorityBricks.add(brick1);
                    node2P.priorityBricks.add(brick2);
                    return createWall(node2P, brick1, brick2, true);
                }
            } else if(!aiTurn && node2P.enemy.currentCell.row < 8 && node2P.enemy.currentCell.column < 8 && node2P.enemy.now < maxWalls){
                Brick brick1 = horizontalBricks[node2P.enemy.currentCell.row][node2P.enemy.currentCell.column];
                Brick brick2 = horizontalBricks[node2P.enemy.currentCell.row][node2P.enemy.currentCell.column + 1];
                boolean makeWall = !node2P.priorityBricks.contains(brick2) && !node2P.priorityBricks.contains(brick1) &&
                        brick1.isValidWall(brick2) && brick1.isValidJoint(brick2, emptyJoints);
                if (makeWall) {
                    node2P.priorityBricks.add(brick1);
                    node2P.priorityBricks.add(brick2);
                    return createWall(node2P, brick1, brick2, false);
                }
            }

        } else if (counter == 13) {
            if (aiTurn && node2P.ai.currentCell.row > 0 && node2P.ai.currentCell.column < 8 && node2P.ai.now < maxWalls) {
                Brick brick1 = horizontalBricks[node2P.ai.currentCell.row - 1][node2P.ai.currentCell.column];
                Brick brick2 = horizontalBricks[node2P.ai.currentCell.row - 1][node2P.ai.currentCell.column + 1];
                boolean makeWall = !node2P.priorityBricks.contains(brick2) && !node2P.priorityBricks.contains(brick1) &&
                        brick1.isValidWall(brick2) && brick1.isValidJoint(brick2, emptyJoints);
                if (makeWall) {
                    node2P.priorityBricks.add(brick1);
                    node2P.priorityBricks.add(brick2);
                    return createWall(node2P, brick1, brick2, true);
                }
            } else if(!aiTurn && node2P.enemy.currentCell.row > 0 && node2P.enemy.currentCell.column < 8 && node2P.enemy.now < maxWalls){
                Brick brick1 = horizontalBricks[node2P.enemy.currentCell.row - 1][node2P.enemy.currentCell.column];
                Brick brick2 = horizontalBricks[node2P.enemy.currentCell.row - 1][node2P.enemy.currentCell.column + 1];
                boolean makeWall = !node2P.priorityBricks.contains(brick2) && !node2P.priorityBricks.contains(brick1) &&
                        brick1.isValidWall(brick2) && brick1.isValidJoint(brick2, emptyJoints);
                if (makeWall) {
                    node2P.priorityBricks.add(brick1);
                    node2P.priorityBricks.add(brick2);
                    return createWall(node2P, brick1, brick2, false);
                }
            }

        }else if (counter == 14) {
            if (aiTurn && node2P.ai.currentCell.column > 1 && node2P.ai.currentCell.row < 8 && node2P.ai.now < maxWalls) {
                Brick brick1 = verticalBricks[node2P.ai.currentCell.row][node2P.ai.currentCell.column - 1];
                Brick brick2 = verticalBricks[node2P.ai.currentCell.row + 1][node2P.ai.currentCell.column - 1];
                boolean makeWall = !node2P.priorityBricks.contains(brick2) && !node2P.priorityBricks.contains(brick1) &&
                        brick1.isValidWall(brick2) && brick1.isValidJoint(brick2, emptyJoints);
                if (makeWall) {
                    node2P.priorityBricks.add(brick1);
                    node2P.priorityBricks.add(brick2);
                    return createWall(node2P, brick1, brick2, true);
                }
            } else if(!aiTurn && node2P.enemy.currentCell.column > 1 && node2P.enemy.currentCell.row < 8 && node2P.enemy.now < maxWalls){
                Brick brick1 = verticalBricks[node2P.enemy.currentCell.row][node2P.enemy.currentCell.column - 1];
                Brick brick2 = verticalBricks[node2P.enemy.currentCell.row + 1][node2P.enemy.currentCell.column - 1];
                boolean makeWall = !node2P.priorityBricks.contains(brick2) && !node2P.priorityBricks.contains(brick1) &&
                        brick1.isValidWall(brick2) && brick1.isValidJoint(brick2, emptyJoints);
                if (makeWall) {
                    node2P.priorityBricks.add(brick1);
                    node2P.priorityBricks.add(brick2);
                    return createWall(node2P, brick1, brick2, false);
                }
            }

        }else if (counter == 15) {
            if (aiTurn && node2P.ai.currentCell.column < 8 && node2P.ai.currentCell.row < 8 && node2P.ai.now < maxWalls) {
                Brick brick1 = verticalBricks[node2P.ai.currentCell.row][node2P.ai.currentCell.column];
                Brick brick2 = verticalBricks[node2P.ai.currentCell.row + 1][node2P.ai.currentCell.column];
                boolean makeWall = !node2P.priorityBricks.contains(brick2) && !node2P.priorityBricks.contains(brick1) &&
                        brick1.isValidWall(brick2) && brick1.isValidJoint(brick2, emptyJoints);
                if (makeWall) {
                    node2P.priorityBricks.add(brick1);
                    node2P.priorityBricks.add(brick2);
                    return createWall(node2P, brick1, brick2, true);
                }
            } else if(!aiTurn && node2P.enemy.currentCell.column < 8 && node2P.enemy.currentCell.row < 8 && node2P.enemy.now < maxWalls){
                Brick brick1 = verticalBricks[node2P.enemy.currentCell.row][node2P.enemy.currentCell.column];
                Brick brick2 = verticalBricks[node2P.enemy.currentCell.row + 1][node2P.enemy.currentCell.column];
                boolean makeWall = !node2P.priorityBricks.contains(brick2) && !node2P.priorityBricks.contains(brick1) &&
                        brick1.isValidWall(brick2) && brick1.isValidJoint(brick2, emptyJoints);
                if (makeWall) {
                    node2P.priorityBricks.add(brick1);
                    node2P.priorityBricks.add(brick2);
                    return createWall(node2P, brick1, brick2, false);
                }
            }

        }else if (counter < 145) { //15 + 64 + 64 = 143
            int c;
            if ((aiTurn && node2P.ai.now < maxWalls) || (!aiTurn && node2P.enemy.now < maxWalls) && node2P.ai.now < maxWalls) {
                if (counter < 76) {
                    c = counter - 11;
                    int row = c / 9;
                    int column = c % 8;
                    Cell.resetSeachedCells(searchedCells);
                    boolean makeWall = horizontalBricks[row][column].isValidWall(horizontalBricks[row][column + 1])
                            && horizontalBricks[row][column].isValidJoint(horizontalBricks[row][column + 1], emptyJoints);
                    if (makeWall) {
                        return createWall(node2P, horizontalBricks[row][column], horizontalBricks[row][column + 1], aiTurn);
                    }
                } else {
                    c = counter - 75;
                    int row = c / 9;
                    int column = c % 8;
                    boolean makeWall = verticalBricks[row][column].isValidWall(verticalBricks[row + 1][column])
                            && verticalBricks[row][column].isValidJoint(verticalBricks[row + 1][column], emptyJoints);
                    if (makeWall) {
                        return createWall(node2P, verticalBricks[row][column], verticalBricks[row + 1][column], aiTurn);
                    }
                }
            }
        }
        return null;
    }

    int heuristicFunction2P(Node2P node2P, boolean aiTurn) {
        if (!aiTurn && node2P.ai.currentCell.row == node2P.ai.goalLine)
            return -5000;
        if (aiTurn && node2P.ai.currentCell.row == node2P.ai.goalLine)
            return 5000;
        int aiDistance, enemyDistance;
        boolean[][] aiSearchedCells = new boolean[9][9];
        boolean[][] enemySearchedCells = new boolean[9][9];
        int aiPossibleMoves = countPossibleMoves(node2P.ai.currentCell);
        int enemyPossibleMoves = countPossibleMoves(node2P.enemy.currentCell);
        Cell.resetSeachedCells(aiSearchedCells);
        Cell.resetSeachedCells(enemySearchedCells);
        aiDistance = Cell.bestCostPath(cells, aiSearchedCells, node2P.ai.currentCell.row, node2P.ai.currentCell.column, true, node2P.ai.goalLine);
        enemyDistance = Cell.bestCostPath(cells, enemySearchedCells, node2P.enemy.currentCell.row, currentNode.enemy.currentCell.column, true, node2P.enemy.goalLine);
        node2P.score = distanceFactor * (aiDistance - enemyDistance) + nowFactor * (node2P.ai.now - node2P.enemy.now) +
                (aiPossibleMoves - enemyPossibleMoves) * moveFactor;
        if (aiTurn)
            node2P.score++;
        else
            node2P.score--;
        return node2P.score;
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

    Node2P createWall(Node2P node2P, Brick b1, Brick b2, boolean aiTurn) {
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
            if (Cell.canReachDestination2P(cells, searchedCells, node2P.ai, node2P.enemy)) {
                if (aiTurn)
                    node2P.ai.now++;
                else
                    node2P.enemy.now++;

                return new Node2P(node2P.ai, node2P.enemy, new Move(b1, b2));
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
            if (Cell.canReachDestination2P(cells, searchedCells, node2P.ai, node2P.enemy)) {
                if (aiTurn)
                    node2P.ai.now++;
                else
                    node2P.enemy.now++;

                emptyJoints[b1.row][b1.column] = false;
                return new Node2P(node2P.ai, node2P.enemy, new Move(b1, b2));
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

    void changeArrays(Node2P node2P, Move move, boolean aiTurn) {
        Cell aiCell = node2P.ai.currentCell;
        Cell enemyCell = node2P.enemy.currentCell;
        if (move.changePos) {
            if (move.direction == Direction.up) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 1][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row - 1][enemyCell.column];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;

                }
            } else if (move.direction == Direction.upUp) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 2][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row - 2][enemyCell.column];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.upRight) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 1][aiCell.column + 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row - 1][enemyCell.column + 1];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.upLeft) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 1][aiCell.column - 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;

                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row - 1][enemyCell.column - 1];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.down) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 1][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row + 1][enemyCell.column];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.downDown) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 2][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row + 2][enemyCell.column];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.downRight) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 1][aiCell.column + 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row + 1][enemyCell.column + 1];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.downLeft) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 1][aiCell.column - 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row + 1][enemyCell.column - 1];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.right) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column + 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row][enemyCell.column + 1];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.rightRight) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column + 2];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row][enemyCell.column + 2];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.left) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column - 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;

                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row][enemyCell.column - 1];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.leftLeft) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column - 2];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row][enemyCell.column - 2];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            }
        }
        node2P.ai.currentCell = aiCell;
        node2P.enemy.currentCell = enemyCell;
    }

    void reverseChangeArrays(Node2P node2P, Move move, boolean aiTurn) {
        Cell aiCell = node2P.ai.currentCell;
        Cell enemyCell = node2P.enemy.currentCell;
        if (move.changePos) {
            if (move.direction == Direction.up) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 1][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row + 1][enemyCell.column];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.upUp) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 2][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row + 2][enemyCell.column];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;

                }
            } else if (move.direction == Direction.upRight) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 1][aiCell.column - 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row + 1][enemyCell.column - 1];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.upLeft) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row + 1][aiCell.column + 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row + 1][enemyCell.column + 1];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.down) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 1][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row - 1][enemyCell.column];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;

                }
            } else if (move.direction == Direction.downDown) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 2][aiCell.column];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row - 2][enemyCell.column];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.downRight) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 1][aiCell.column - 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row - 1][enemyCell.column - 1];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;

                }
            } else if (move.direction == Direction.downLeft) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row - 1][aiCell.column + 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;

                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row - 1][enemyCell.column + 1];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.right) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column - 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row][enemyCell.column - 1];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;

                }
            } else if (move.direction == Direction.rightRight) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column - 2];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row][enemyCell.column - 2];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.left) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column + 1];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row][enemyCell.column + 1];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
                }
            } else if (move.direction == Direction.leftLeft) {
                if (aiTurn) {
                    cells[aiCell.row][aiCell.column].isFilled = false;
                    aiCell = cells[aiCell.row][aiCell.column + 2];
                    cells[aiCell.row][aiCell.column].isFilled = true;
                } else {
                    cells[enemyCell.row][enemyCell.column].isFilled = false;
                    enemyCell = cells[enemyCell.row][enemyCell.column + 2];
                    cells[enemyCell.row][enemyCell.column].isFilled = true;
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
            if (aiTurn)
                node2P.ai.now--;
            else
                node2P.enemy.now--;
        }
        node2P.ai.currentCell = aiCell;
        node2P.enemy.currentCell = enemyCell;
    }
}

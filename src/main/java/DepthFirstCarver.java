package main.java;

import java.util.Arrays;
import java.util.Collections;

class DepthFirstCarver implements MazeAlgorithm {

    private final Cell[][] cellNetwork;
    private final Cell.possibleDirections[] possibleDirections;
    private final Coords currentCoordinates;
    private final Coords exitCoordinates;

    public DepthFirstCarver(Cell[][] cellNetwork, Coords entryCoordinates, Coords exitCoordinates) {
        this.cellNetwork = cellNetwork;
        this.possibleDirections = new Cell.possibleDirections[4];
        System.arraycopy(Cell.possibleDirections.values(), 0, this.possibleDirections, 0, 4);

        this.currentCoordinates = new Coords(entryCoordinates.x, entryCoordinates.y);
        this.exitCoordinates = exitCoordinates;
    }

    @Override
    public void go() {
        this.departFromMargin();
        this.fillWithWalls();
        this.cellNetwork[this.currentCoordinates.x][this.currentCoordinates.y] = new Cell(true);
        this.carve(this.currentCoordinates);
        this.clearExit();
    }

    public void departFromMargin() {
        if (this.currentCoordinates.x == 0) {
            ++this.currentCoordinates.x;
        } else if (this.currentCoordinates.x == this.cellNetwork.length - 1) {
            --this.currentCoordinates.x;
        } else if (this.currentCoordinates.y == 0) {
            ++this.currentCoordinates.y;
        } else {
            --this.currentCoordinates.y;
        }
    }

    private void fillWithWalls() {
        for (int line = 1; line < this.cellNetwork.length - 1; line++) {
            for (int column = 1; column < this.cellNetwork.length - 1; column++) {
                this.cellNetwork[line][column] = new Cell(false);
            }
        }
    }

    private void carve(Coords cell) {
        Collections.shuffle(Arrays.asList(this.possibleDirections));

        for (Cell.possibleDirections direction : this.possibleDirections) {
            Coords newCell = this.moveToDirection(cell, direction);

            if (this.checkIfFeasible(newCell)) {
                this.cellNetwork[newCell.x][newCell.y] = new Cell(true);
                this.carve(newCell);
            }
        }
    }

    // A feasible point will never connect two already instantiated cells, we search for non-explored areas only.
    // Additionally, we don't want to touch the margins of the maze either.
    private boolean checkIfFeasible(Coords cell) {
        if (cell.x == 0 || cell.x == this.cellNetwork.length - 1 || cell.y == 0 || cell.y == this.cellNetwork.length - 1) {
            return false;
        }

        int countTraversableNeighbours = 0;
        for (Cell.possibleDirections direction : this.possibleDirections) {
            Coords newCell = this.moveToDirection(cell, direction);

            if (this.cellNetwork[newCell.x][newCell.y].isTraversable()) {
                ++countTraversableNeighbours;
            }
        }

        return countTraversableNeighbours <= 1;
    }

    private Coords moveToDirection(Coords cell, Cell.possibleDirections direction) {
        Coords newCell = new Coords(cell.x, cell.y);

        switch (direction) {
            case NORTH:
                --newCell.x;
                break;
            case EAST:
                ++newCell.y;
                break;
            case SOUTH:
                ++newCell.x;
                break;
            case WEST:
                --newCell.y;
                break;
        }

        return newCell;
    }

    private void clearExit() {
        if (this.exitCoordinates.x == 0) {
            this.cellNetwork[this.exitCoordinates.x + 1][this.exitCoordinates.y] = new Cell(true);
            this.cellNetwork[this.exitCoordinates.x + 2][this.exitCoordinates.y] = new Cell(true);
        } else if (this.exitCoordinates.x == this.cellNetwork.length - 1) {
            this.cellNetwork[this.exitCoordinates.x - 1][this.exitCoordinates.y] = new Cell(true);
            this.cellNetwork[this.exitCoordinates.x - 2][this.exitCoordinates.y] = new Cell(true);
        } else if (this.exitCoordinates.y == 0) {
            this.cellNetwork[this.exitCoordinates.x][this.exitCoordinates.y + 1] = new Cell(true);
            this.cellNetwork[this.exitCoordinates.x][this.exitCoordinates.y + 2] = new Cell(true);
        } else if (this.exitCoordinates.y == this.cellNetwork.length - 1) {
            this.cellNetwork[this.exitCoordinates.x][this.exitCoordinates.y - 1] = new Cell(true);
            this.cellNetwork[this.exitCoordinates.x][this.exitCoordinates.y - 2] = new Cell(true);
        }
    }
}

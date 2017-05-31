package sample.Utilities;

import java.util.List;

public class Board {
    public Integer SIZE;
    public State[][] boardState;

    public Board() {
        SIZE = 8;
        boardState = new State[SIZE][SIZE];
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                boardState[x][y] = State.FREE;
            }
        }
    }

    private void newGame() {
        cleanBoard();
        placeBluePawns();
        placeYellowPawns();
    }

    public void cleanBoard() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                boardState[x][y] = State.FREE;
            }
        }
    }

    private void placeBluePawns() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (y % 2 == x % 2)
                    boardState[x][y] = State.BLUE;
            }
        }
    }

    private void placeYellowPawns() {
        for (int x = SIZE - 2; x < SIZE; x++) {
            for (int y = 5; y < 8; y++) {
                if (y % 2 == x % 2)
                    boardState[x][y] = State.GREEN;
            }
        }
    }
}

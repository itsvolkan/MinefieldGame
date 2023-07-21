import java.util.Random;

public class MinefieldModel {
    private static final int GRID_SIZE = 5;
    private static final int NUM_MINES = 5;

    private boolean[][] mineGrid;
    private boolean[][] revealedGrid;
    private boolean[][] flaggedGrid;

    private int score;


    public void increaseScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }

    public MinefieldModel() {
        mineGrid = new boolean[GRID_SIZE][GRID_SIZE];
        revealedGrid = new boolean[GRID_SIZE][GRID_SIZE];
        flaggedGrid = new boolean[GRID_SIZE][GRID_SIZE];
    }

    public void generateMinefield(int startRow, int startCol) {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < NUM_MINES) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);

            if (!mineGrid[row][col] && !(row == startRow && col == startCol)) {
                mineGrid[row][col] = true;
                minesPlaced++;
            }
        }
    }

    public boolean isMine(int row, int col) {
        return mineGrid[row][col];
    }

    public boolean isRevealed(int row, int col) {
        return revealedGrid[row][col];
    }

    public boolean isFlagged(int row, int col) {
        return flaggedGrid[row][col];
    }

    public void toggleFlag(int row, int col) {
        flaggedGrid[row][col] = !flaggedGrid[row][col];
    }

    public void revealCell(int row, int col) {
        revealedGrid[row][col] = true;
    }

    public int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < GRID_SIZE && j >= 0 && j < GRID_SIZE && mineGrid[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean checkGameWin() {
        int revealedCells = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (revealedGrid[row][col]) {
                    revealedCells++;
                }
            }
        }
        return revealedCells == GRID_SIZE * GRID_SIZE - NUM_MINES;
    }

    public void revealMines() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (mineGrid[row][col]) {
                    revealedGrid[row][col] = true;
                }
            }
        }
    }

    public void reset() {
        mineGrid = new boolean[GRID_SIZE][GRID_SIZE];
        revealedGrid = new boolean[GRID_SIZE][GRID_SIZE];
        flaggedGrid = new boolean[GRID_SIZE][GRID_SIZE];
    }
}

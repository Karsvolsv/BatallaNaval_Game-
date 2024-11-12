package ethan.batallanaval.Model;

public class ModelBattleship {
    private final int BOARD_SIZE = 10;
    private CellStatus[][] board;

    public ModelBattleship() {
        board = new CellStatus[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = CellStatus.EMPTY;
            }
        }
    }

    public boolean placeShip(int startX, int startY, int length, boolean horizontal) {
        if (!isValidPlacement(startX, startY, length, horizontal)) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (horizontal) {
                board[startX][startY + i] = CellStatus.SHIP;
            } else {
                board[startX + i][startY] = CellStatus.SHIP;
            }
        }
        return true;
    }

    private boolean isValidPlacement(int startX, int startY, int length, boolean horizontal) {
        if (horizontal && (startY + length > BOARD_SIZE)) return false;
        if (!horizontal && (startX + length > BOARD_SIZE)) return false;
        for (int i = 0; i < length; i++) {
            if (horizontal) {
                if (board[startX][startY + i] != CellStatus.EMPTY) return false;
            } else {
                if (board[startX + i][startY] != CellStatus.EMPTY) return false;
            }
        }
        return true;
    }

    public CellStatus getCellStatus(int x, int y) {
        return board[x][y];
    }

    public enum CellStatus {
        EMPTY, SHIP, HIT, MISS
    }
}

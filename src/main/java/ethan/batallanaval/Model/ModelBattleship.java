package ethan.batallanaval.Model;

import java.util.ArrayList;
import java.util.List;

public class ModelBattleship {
    private static final int BOARD_SIZE = 10;
    private Cell[][] board;
    private List<Ship> ships;

    // Cantidades máximas de cada tipo de barco
    public static final int MAX_PORTAAVIONES = 1;
    public static final int MAX_SUBMARINOS = 2;
    public static final int MAX_DESTRUCTORES = 3;
    public static final int MAX_FRAGATAS = 4;

    // Listas para almacenar los barcos por tipo
    private List<Ship> portaaviones = new ArrayList<>();
    private List<Ship> submarinos = new ArrayList<>();
    private List<Ship> destructores = new ArrayList<>();
    private List<Ship> fragatas = new ArrayList<>();

    public ModelBattleship() {
        board = new Cell[BOARD_SIZE][BOARD_SIZE];
        ships = new ArrayList<>();
        initializeBoard();
    }

    // Inicializa el tablero con celdas vacías
    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new Cell(CellStatus.EMPTY); // Cada celda comienza vacía
            }
        }
    }

    // Método para colocar un barco en el tablero
    public boolean placeShip(int startX, int startY, int length, boolean horizontal) {
        if (!isValidPlacement(startX, startY, length, horizontal)) {
            return false; // Si no es válido, no se coloca el barco
        }

        // Crear el barco
        Ship ship = new Ship(startX, startY, length, horizontal);
        ships.add(ship);

        // Colocar el barco en el tablero
        for (int i = 0; i < length; i++) {
            int x = horizontal ? startX : startX + i;
            int y = horizontal ? startY + i : startY;
            board[x][y].setStatus(CellStatus.SHIP);  // Marca la celda como ocupada por un barco
            ship.addCell(x, y);
        }

        // Añadir el barco a la lista correspondiente
        addShipToList(ship);

        return true;
    }

    // Añade el barco a la lista correspondiente según su tamaño
    private void addShipToList(Ship ship) {
        int length = ship.getLength();
        if (length == 4) {
            if (portaaviones.size() < MAX_PORTAAVIONES) portaaviones.add(ship);
        } else if (length == 3) {
            if (submarinos.size() < MAX_SUBMARINOS) submarinos.add(ship);
        } else if (length == 2) {
            if (destructores.size() < MAX_DESTRUCTORES) destructores.add(ship);
        } else if (length == 1) {
            if (fragatas.size() < MAX_FRAGATAS) fragatas.add(ship);
        }
    }

    // Verifica si la colocación de un barco es válida en el tablero
    private boolean isValidPlacement(int startX, int startY, int length, boolean horizontal) {
        // Verificar si el barco se sale del tablero
        if (horizontal && startY + length > BOARD_SIZE) return false;
        if (!horizontal && startX + length > BOARD_SIZE) return false;

        // Verificar que no haya otro barco en las celdas donde queremos colocar el barco
        for (int i = 0; i < length; i++) {
            int x = horizontal ? startX : startX + i;
            int y = horizontal ? startY + i : startY;
            if (board[x][y].getStatus() != CellStatus.EMPTY) {
                return false;  // Si alguna celda ya está ocupada, no es válido
            }
        }
        return true;
    }

    // Obtiene el estado de una celda específica en el tablero
    public CellStatus getCellStatus(int x, int y) {
        return board[x][y].getStatus();
    }

    // Verifica si se ha alcanzado el número máximo de barcos
    public boolean isMaxShipsPlaced() {
        return portaaviones.size() == MAX_PORTAAVIONES &&
                submarinos.size() == MAX_SUBMARINOS &&
                destructores.size() == MAX_DESTRUCTORES &&
                fragatas.size() == MAX_FRAGATAS;
    }

    // Obtiene la lista de barcos
    public List<Ship> getShips() {
        return ships;
    }

    // Enum para representar el estado de cada celda en el tablero
    public enum CellStatus {
        EMPTY, SHIP, HIT, MISS, SUNK
    }

    // Clase interna para representar un barco en el juego
    public static class Ship {
        private int startX, startY, length;
        private boolean horizontal;
        private int hits;
        private List<int[]> cells;

        public Ship(int startX, int startY, int length, boolean horizontal) {
            this.startX = startX;
            this.startY = startY;
            this.length = length;
            this.horizontal = horizontal;
            this.hits = 0;
            this.cells = new ArrayList<>();
        }

        // Incrementa el conteo de impactos en el barco
        public void hit() {
            hits++;
        }

        // Verifica si el barco ha sido hundido
        public boolean isSunk() {
            return hits >= length;
        }

        // Verifica si el barco ha sido tocado en una posición dada
        public boolean isHitAt(int x, int y) {
            if (horizontal) {
                return y >= startY && y < startY + length && x == startX;
            } else {
                return x >= startX && x < startX + length && y == startY;
            }
        }

        // Añade una celda a la lista de celdas ocupadas por el barco
        public void addCell(int x, int y) {
            cells.add(new int[]{x, y});
        }

        // Devuelve las celdas ocupadas por el barco
        public List<int[]> getCells() {
            return cells;
        }

        // Métodos de acceso para las propiedades del barco
        public int getStartX() { return startX; }
        public int getStartY() { return startY; }
        public int getLength() { return length; }
        public boolean isHorizontal() { return horizontal; }
    }

    // Clase para representar una celda en el tablero
    public static class Cell {
        private CellStatus status;
        private Ship ship;

        public Cell(CellStatus status) {
            this.status = status;
            this.ship = null;
        }

        public CellStatus getStatus() {
            return status;
        }

        public void setStatus(CellStatus status) {
            this.status = status;
        }

        public Ship getShip() {
            return ship;
        }

        public void setShip(Ship ship) {
            this.ship = ship;
        }
    }
}

package ethan.batallanaval.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ModelBattleship {
    private final int BOARD_SIZE = 10;
    private CellStatus[][] board;
    private List<Ship> ships;

    // Mapa para manejar la cantidad de barcos de cada tipo
    private Map<Integer, Integer> shipCountMap;

    // Cantidades máximas de barcos según las reglas
    public static final int MAX_PORTAAVIONES = 1;
    public static final int MAX_SUBMARINOS = 2;
    public static final int MAX_DESTRUCTORES = 3;
    public static final int MAX_FRAGATAS = 4;

    public ModelBattleship() {
        board = new CellStatus[BOARD_SIZE][BOARD_SIZE];
        ships = new ArrayList<>();
        shipCountMap = new HashMap<>();
        initializeBoard();
        initializeShipCounts();
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = CellStatus.EMPTY;
            }
        }
    }

    private void initializeShipCounts() {
        shipCountMap.put(4, 0);  // Portaaviones
        shipCountMap.put(3, 0);  // Submarinos
        shipCountMap.put(2, 0);  // Destructores
        shipCountMap.put(1, 0);  // Fragatas
    }

    // Métodos para obtener el número de barcos de cada tipo
    public int getPortaavionesCount() {
        return shipCountMap.get(4);
    }

    public int getSubmarinosCount() {
        return shipCountMap.get(3);
    }

    public int getDestructoresCount() {
        return shipCountMap.get(2);
    }

    public int getFragatasCount() {
        return shipCountMap.get(1);
    }

    // Método para manejar disparos
    public CellStatus shoot(int x, int y) {
        if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) {
            throw new IllegalArgumentException("Coordenadas fuera del tablero.");
        }

        if (board[x][y] == CellStatus.EMPTY) {
            board[x][y] = CellStatus.MISS;
            return CellStatus.MISS;
        }

        if (board[x][y] == CellStatus.SHIP) {
            board[x][y] = CellStatus.HIT;

            // Identificar el barco golpeado y actualizar su estado
            for (Ship ship : ships) {
                if (ship.isPartOfShip(x, y)) {
                    ship.hit();
                    if (ship.isSunk()) {
                        markShipAsSunk(ship);
                        return CellStatus.SUNK;
                    }
                    return CellStatus.HIT;
                }
            }
        }

        return board[x][y]; // Devuelve el estado actual si ya fue impactado antes
    }

    // Método para marcar un barco hundido en el tablero
    private void markShipAsSunk(Ship ship) {
        for (int i = 0; i < ship.getLength(); i++) {
            int x = ship.isHorizontal() ? ship.getStartX() : ship.getStartX() + i;
            int y = ship.isHorizontal() ? ship.getStartY() + i : ship.getStartY();
            board[x][y] = CellStatus.SUNK;
        }
    }

    // Método para verificar si todos los barcos están hundidos
    public boolean isGameOver() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    // Mejorar la validación de la colocación para evitar barcos demasiado cercanos
    private boolean isValidPlacement(int startX, int startY, int length, boolean horizontal) {
        if (horizontal && (startY + length > BOARD_SIZE)) return false;
        if (!horizontal && (startX + length > BOARD_SIZE)) return false;

        for (int i = 0; i < length; i++) {
            int x = horizontal ? startX : startX + i;
            int y = horizontal ? startY + i : startY;

            // Verificar la celda actual y las adyacentes
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = x + dx;
                    int ny = y + dy;
                    if (nx >= 0 && nx < BOARD_SIZE && ny >= 0 && ny < BOARD_SIZE) {
                        if (board[nx][ny] != CellStatus.EMPTY) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    // Método para colocar un barco en el tablero
    public boolean placeShip(int startX, int startY, int length, boolean horizontal) {
        // Verificar si la colocación es válida
        if (!isValidPlacement(startX, startY, length, horizontal)) {
            return false;  // Si no es válida, no se coloca el barco
        }

        // Colocar el barco en las celdas del tablero
        Ship newShip = new Ship(startX, startY, length, horizontal);
        for (int i = 0; i < length; i++) {
            int x = horizontal ? startX : startX + i;
            int y = horizontal ? startY + i : startY;
            board[x][y] = CellStatus.SHIP;  // Marcar la celda con el barco
        }

        // Agregar el barco a la lista de barcos
        ships.add(newShip);

        // Actualizar el contador del tipo de barco
        if (length == 4) {
            shipCountMap.put(4, shipCountMap.get(4) + 1);  // Portaaviones
        } else if (length == 3) {
            shipCountMap.put(3, shipCountMap.get(3) + 1);  // Submarinos
        } else if (length == 2) {
            shipCountMap.put(2, shipCountMap.get(2) + 1);  // Destructores
        } else if (length == 1) {
            shipCountMap.put(1, shipCountMap.get(1) + 1);  // Fragatas
        }

        return true;  // La colocación fue exitosa
    }

    // Método para obtener el estado actual del tablero
    public CellStatus[][] getBoardState() {
        CellStatus[][] boardCopy = new CellStatus[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(board[i], 0, boardCopy[i], 0, BOARD_SIZE);
        }
        return boardCopy;
    }

    // Método para reiniciar el modelo
    public void reset() {
        initializeBoard();
        ships.clear();
        initializeShipCounts();
    }

    // Estados posibles de las celdas en el tablero
    public enum CellStatus {
        EMPTY, SHIP, HIT, MISS, SUNK
    }

    // Método para obtener el estado de una celda en el tablero
    public CellStatus getCellStatus(int x, int y) {
        if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) {
            throw new IllegalArgumentException("Coordenadas fuera del tablero.");
        }
        return board[x][y];  // Devuelve el estado de la celda
    }

    // Clase interna Ship mejorada
    public static class Ship {
        private int startX, startY, length;
        private boolean horizontal;
        private int hits;

        public Ship(int startX, int startY, int length, boolean horizontal) {
            this.startX = startX;
            this.startY = startY;
            this.length = length;
            this.horizontal = horizontal;
            this.hits = 0;
        }

        public boolean isSunk() {
            return hits >= length;
        }

        public void hit() {
            hits++;
        }

        public boolean isPartOfShip(int x, int y) {
            for (int i = 0; i < length; i++) {
                int shipX = horizontal ? startX : startX + i;
                int shipY = horizontal ? startY + i : startY;
                if (shipX == x && shipY == y) {
                    return true;
                }
            }
            return false;
        }

        public int getStartX() {
            return startX;
        }

        public int getStartY() {
            return startY;
        }

        public boolean isHorizontal() {
            return horizontal;
        }

        public int getLength() {
            return length;
        }
    }
}
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

    // Inicializa los contadores de barcos en el mapa
    private void initializeShipCounts() {
        shipCountMap.put(4, 0);  // Portaaviones
        shipCountMap.put(3, 0);  // Submarinos
        shipCountMap.put(2, 0);  // Destructores
        shipCountMap.put(1, 0);  // Fragatas
    }

    // Método para colocar un barco en el tablero
    public boolean placeShip(int startX, int startY, int length, boolean horizontal) {
        if (!isValidPlacement(startX, startY, length, horizontal)) {
            return false;
        }

        // Verificar que no excedamos la cantidad máxima de barcos
        if (!canPlaceShip(length)) {
            return false;
        }

        // Colocar el barco
        Ship ship = new Ship(startX, startY, length, horizontal);
        ships.add(ship);

        for (int i = 0; i < length; i++) {
            if (horizontal) {
                board[startX][startY + i] = CellStatus.SHIP;
            } else {
                board[startX + i][startY] = CellStatus.SHIP;
            }
        }

        // Actualizar el contador de barcos
        updateShipCount(length);

        return true;
    }

    // Verificar si se puede colocar el barco dependiendo de su tamaño
    private boolean canPlaceShip(int length) {
        if (length == 4 && shipCountMap.get(4) < MAX_PORTAAVIONES) {
            return true;  // Puede colocar un portaaviones
        } else if (length == 3 && shipCountMap.get(3) < MAX_SUBMARINOS) {
            return true;  // Puede colocar un submarino
        } else if (length == 2 && shipCountMap.get(2) < MAX_DESTRUCTORES) {
            return true;  // Puede colocar un destructor
        } else if (length == 1 && shipCountMap.get(1) < MAX_FRAGATAS) {
            return true;  // Puede colocar una fragata
        }
        return false;  // No se puede colocar más barcos de este tamaño
    }

    // Actualizar el contador de barcos según su tamaño
    private void updateShipCount(int length) {
        shipCountMap.put(length, shipCountMap.get(length) + 1);
    }

    // Verificar si la colocación es válida
    private boolean isValidPlacement(int startX, int startY, int length, boolean horizontal) {
        if (horizontal && (startY + length > BOARD_SIZE)) return false;
        if (!horizontal && (startX + length > BOARD_SIZE)) return false;

        for (int i = 0; i < length; i++) {
            int x = horizontal ? startX : startX + i;
            int y = horizontal ? startY + i : startY;
            if (board[x][y] != CellStatus.EMPTY) {
                return false;
            }
        }
        return true;
    }

    // Obtener el estado de una celda específica
    public CellStatus getCellStatus(int x, int y) {
        return board[x][y];
    }

    // Devuelve la lista de barcos colocados
    public List<Ship> getShips() {
        return ships;
    }

    // Método que devuelve si se ha colocado el número máximo de barcos
    public boolean isMaxShipsPlaced() {
        return shipCountMap.get(4) >= MAX_PORTAAVIONES &&
                shipCountMap.get(3) >= MAX_SUBMARINOS &&
                shipCountMap.get(2) >= MAX_DESTRUCTORES &&
                shipCountMap.get(1) >= MAX_FRAGATAS;
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

    // Definir los estados posibles de las celdas en el tablero
    public enum CellStatus {
        EMPTY, SHIP, HIT, MISS, SUNK
    }

    // Clase interna Ship para almacenar la información de cada barco
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

        public int getLength() {
            return length;
        }
    }
}

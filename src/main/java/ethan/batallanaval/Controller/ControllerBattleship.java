package ethan.batallanaval.Controller;

import ethan.batallanaval.Model.ModelBattleship;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

public class ControllerBattleship {
    private ModelBattleship model;

    @FXML private GridPane boardGrid;  // Cambiado a GridPane según el FXML
    @FXML private Pane shipsContainer;  // Panel de contenedor de barcos

    private static final int BOARD_SIZE = 10;
    private static final int CELL_SIZE = 60;

    private Map<String, Ship> shipMap = new HashMap<>();
    private Ship selectedShip = null;  // Para el barco que el usuario selecciona

    private Cell[][] board = new Cell[BOARD_SIZE][BOARD_SIZE];

    public void initialize() {
        model = new ModelBattleship();
        loadImages();
        createBoard();
        initializeShips();
    }

    // Cargar las imágenes de los barcos
    private void loadImages() {
        shipMap.put("portaaviones", createShip("/Imagenes/portaaviones.png", 4, ModelBattleship.MAX_PORTAAVIONES));
        shipMap.put("submarino", createShip("/Imagenes/submarinos.png", 3, ModelBattleship.MAX_SUBMARINOS));
        shipMap.put("destructor", createShip("/Imagenes/destructores.png", 2, ModelBattleship.MAX_DESTRUCTORES));
        shipMap.put("fragata", createShip("/Imagenes/fragatas.png", 1, ModelBattleship.MAX_FRAGATAS));
    }

    // Crear un objeto Ship con una imagen, tamaño y cantidad
    private Ship createShip(String imagePath, int size, int count) {
        InputStream stream = getClass().getResourceAsStream(imagePath);
        if (stream == null) {
            throw new IllegalArgumentException("Error: No se pudo cargar la imagen " + imagePath);
        }
        return new Ship(stream, size, count);
    }

    // Crear el tablero con Rectángulos en lugar de botones
    private void createBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // Inicializamos las celdas en la matriz
                board[i][j] = new Cell();

                Rectangle cellRectangle = createCell(i, j);
                boardGrid.add(cellRectangle, j, i);  // Añadimos el rectángulo al GridPane (boardGrid)
            }
        }
    }

    // Crear una celda en el tablero
    private Rectangle createCell(int x, int y) {
        Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
        rect.setFill(Color.BLUE);  // Celda vacía, por defecto

        // Añadir evento de clic para colocar barcos
        rect.setOnMouseClicked(event -> onCellClick(event, x, y));

        return rect;
    }

    private void onCellClick(MouseEvent event, int x, int y) {
        // Verificamos si hay un barco seleccionado y si está disponible
        if (selectedShip == null) {
            showError("No hay barcos disponibles para colocar.");
            return;
        }

        // Intentamos colocar el barco en la celda seleccionada
        if (model.placeShip(x, y, selectedShip.getSize(), true)) {  // Por defecto, horizontal
            updateBoard();  // Actualiza el tablero para reflejar los cambios
        } else {
            showError("No se pudo colocar el barco en esta posición.");
        }
    }

    // Inicializar los barcos en la interfaz
    private void initializeShips() {
        shipsContainer.getChildren().clear();
        shipMap.forEach((name, ship) -> {
            for (int i = 0; i < ship.getCount(); i++) {
                createDraggableShip(ship);
            }
        });
    }

    // Crear un barco arrastrable en la interfaz
    private void createDraggableShip(Ship ship) {
        // Esto se hace de forma similar a antes, usando imágenes
        ImageView shipImageView = new ImageView(ship.getImageView().getImage());
        shipImageView.setFitWidth(ship.getSize() * CELL_SIZE);
        shipImageView.setFitHeight(CELL_SIZE);

        // Lógica de drag-and-drop para los barcos
        shipImageView.setOnMouseClicked(event -> {
            selectedShip = ship;  // Selecciona el barco
            showMessage("Barco seleccionado: " + ship.getSize() + " casillas.");
        });

        shipImageView.setOnMouseDragged(event -> {
            shipImageView.setX(event.getSceneX() - shipImageView.getFitWidth() / 2);
            shipImageView.setY(event.getSceneY() - shipImageView.getFitHeight() / 2);
        });

        shipImageView.setOnMouseReleased(event -> {
            int x = (int) (event.getSceneY() / CELL_SIZE);
            int y = (int) (event.getSceneX() / CELL_SIZE);
            placeShip(x, y, ship.getSize(), true);
        });

        shipsContainer.getChildren().add(shipImageView);
    }

    // Colocar un barco en el tablero
    private void placeShip(int x, int y, int shipSize, boolean isHorizontal) {
        if (isHorizontal) {
            if (y + shipSize > BOARD_SIZE) {
                showError("El barco no cabe en esa posición horizontal.");
                return;
            }

            for (int i = 0; i < shipSize; i++) {
                if (board[x][y + i].getStatus() != ModelBattleship.CellStatus.EMPTY) {
                    showError("La posición ya está ocupada.");
                    return;
                }
            }

            // Colocar el barco
            for (int i = 0; i < shipSize; i++) {
                board[x][y + i].setShip(selectedShip);
                board[x][y + i].setStatus(ModelBattleship.CellStatus.SHIP);
            }
        } else {
            if (x + shipSize > BOARD_SIZE) {
                showError("El barco no cabe en esa posición vertical.");
                return;
            }

            for (int i = 0; i < shipSize; i++) {
                if (board[x + i][y].getStatus() != ModelBattleship.CellStatus.EMPTY) {
                    showError("La posición ya está ocupada.");
                    return;
                }
            }

            // Colocar el barco
            for (int i = 0; i < shipSize; i++) {
                board[x + i][y].setShip(selectedShip);
                board[x + i][y].setStatus(ModelBattleship.CellStatus.SHIP);
            }
        }

        updateBoard();  // Actualiza el tablero visualmente
    }

    // Actualizar el tablero visualmente
    private void updateBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Rectangle cell = (Rectangle) getCellFromGridPane(i, j);
                ModelBattleship.CellStatus status = board[i][j].getStatus();
                updateCellStyle(cell, status);
            }
        }
    }

    private void updateCellStyle(Rectangle rect, ModelBattleship.CellStatus status) {
        switch (status) {
            case EMPTY:
                rect.setFill(Color.BLUE);  // Agua
                break;
            case SHIP:
                rect.setFill(Color.GRAY);  // Barco
                break;
            case HIT:
                rect.setFill(Color.RED);   // Tocado
                break;
            case MISS:
                rect.setFill(Color.WHITE); // Agua
                break;
            case SUNK:
                rect.setFill(Color.BLACK); // Hundido
                break;
        }
    }

    // Método para obtener la celda desde el GridPane (adaptado)
    private Rectangle getCellFromGridPane(int x, int y) {
        for (var node : boardGrid.getChildren()) {
            if (node instanceof Rectangle) {
                Rectangle rect = (Rectangle) node;
                if (rect.getX() == y * CELL_SIZE && rect.getY() == x * CELL_SIZE) {
                    return rect;
                }
            }
        }
        return null;
    }

    @FXML
    private void startGame() {
        System.out.println("Juego iniciado");
        updateBoard();
    }

    @FXML
    private void resetGame() {
        model = new ModelBattleship();
        clearBoard();
        initializeShips();
        System.out.println("Juego reiniciado");
    }

    private void clearBoard() {
        boardGrid.getChildren().clear();
        createBoard();
    }

    @FXML
    private void showInstructions() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Instrucciones del Juego");
        alert.setHeaderText("Reglas y cantidad de barcos:");
        alert.setContentText(
                "Reglas del Juego:\n" +
                        "- Coloca los barcos en el tablero antes de iniciar.\n" +
                        "- Los barcos pueden colocarse horizontalmente por defecto.\n" +
                        "- Si un barco es tocado pero no hundido, mostrará una bomba.\n" +
                        "- Si un barco es hundido, mostrará un fuego en las celdas correspondientes.\n\n" +
                        "Cantidad de Barcos:\n" +
                        "1. Portaaviones (4 casillas) - Cantidad: 1\n" +
                        "2. Submarinos (3 casillas cada uno) - Cantidad: 2\n" +
                        "3. Destructores (2 casillas cada uno) - Cantidad: 3\n" +
                        "4. Fragatas (1 casilla cada una) - Cantidad: 4\n"
        );
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Colocación no válida");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clase para representar las celdas del tablero
    private static class Cell {
        private ModelBattleship.CellStatus status;
        private Ship ship;

        public Cell() {
            this.status = ModelBattleship.CellStatus.EMPTY;
            this.ship = null;
        }

        public ModelBattleship.CellStatus getStatus() {
            return status;
        }

        public void setStatus(ModelBattleship.CellStatus status) {
            this.status = status;
        }

        public Ship getShip() {
            return ship;
        }

        public void setShip(Ship ship) {
            this.ship = ship;
        }
    }

    // Clase Ship (simplificada y corregida)
    private static class Ship {
        private final ImageView imageView;
        private final int size;
        private final int count;
        private boolean isHorizontal;

        public Ship(InputStream imagePath, int size, int count) {
            this.imageView = new ImageView(new Image(imagePath, size * CELL_SIZE, CELL_SIZE, true, true));
            this.size = size;
            this.count = count;
            this.isHorizontal = true;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public int getSize() {
            return size;
        }

        public int getCount() {
            return count;
        }

        public boolean isHorizontal() {
            return isHorizontal;
        }

        public void setHorizontal(boolean isHorizontal) {
            this.isHorizontal = isHorizontal;
        }
    }
}

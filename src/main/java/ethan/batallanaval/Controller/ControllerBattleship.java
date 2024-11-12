package ethan.batallanaval.Controller;

import ethan.batallanaval.Model.ModelBattleship;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ControllerBattleship {
    private ModelBattleship model;

    @FXML
    private GridPane boardGrid;

    @FXML
    private Button showInstructions;

    private static final int BOARD_SIZE = 10;
    private static final int CELL_SIZE = 60;

    public void initialize() {
        model = new ModelBattleship();
        createBoard();
    }

    private void createBoard() {
        // Ajustar las restricciones de la grid para las celdas
        setUpGridConstraints();

        // Crear las filas de números y letras
        createLabels();

        // Crear las celdas del tablero y agregarlas a la grid
        populateBoardCells();
    }

    private void setUpGridConstraints() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            javafx.scene.layout.ColumnConstraints colConstraints = new javafx.scene.layout.ColumnConstraints();
            colConstraints.setMinWidth(CELL_SIZE);
            colConstraints.setMaxWidth(CELL_SIZE);
            colConstraints.setHgrow(javafx.scene.layout.Priority.NEVER);

            javafx.scene.layout.RowConstraints rowConstraints = new javafx.scene.layout.RowConstraints();
            rowConstraints.setMinHeight(CELL_SIZE);
            rowConstraints.setMaxHeight(CELL_SIZE);
            rowConstraints.setVgrow(javafx.scene.layout.Priority.NEVER);

            boardGrid.getColumnConstraints().add(colConstraints);
            boardGrid.getRowConstraints().add(rowConstraints);
        }
    }

    private void createLabels() {
        // Fila de números en la columna 0 (de la fila 1 a 10)
        for (int i = 0; i < BOARD_SIZE; i++) {
            Label label = createLabel(String.valueOf(i + 1));
            boardGrid.add(label, 0, i + 1); // Colocar los números en la columna 0
        }

        // Fila de letras en la fila 0 (de la columna 1 a 10)
        char letter = 'A';
        for (int j = 0; j < BOARD_SIZE; j++) {
            Label label = createLabel(String.valueOf(letter));
            boardGrid.add(label, j + 1, 0); // Colocar las letras en la fila 0
            letter++;
        }
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setPrefSize(CELL_SIZE, CELL_SIZE);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-alignment: center;");
        return label;
    }

    private void populateBoardCells() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Button cell = createCell(i, j);
                boardGrid.add(cell, j + 1, i + 1); // Agregar las celdas al tablero
            }
        }
    }

    private Button createCell(int x, int y) {
        Button cell = new Button();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);
        cell.setOnAction(event -> handleCellClick(x, y)); // Manejar el clic en la celda
        return cell;
    }

    private void handleCellClick(int x, int y) {
        if (model.getCellStatus(x, y) == ModelBattleship.CellStatus.EMPTY) {
            boolean placed = model.placeShip(x, y, 3, true);  // Ejemplo con barco de tamaño 3
            if (placed) {
                updateBoard(); // Actualizar el tablero visual después de colocar el barco
            } else {
                showError("No se pudo colocar el barco aquí.");
            }
        }
    }

    private void updateBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Button cell = (Button) getCellAt(i, j);
                ModelBattleship.CellStatus status = model.getCellStatus(i, j);

                updateCellStyle(cell, status);
            }
        }
    }

    private Node getCellAt(int x, int y) {
        return boardGrid.getChildren().stream()
                .filter(node -> GridPane.getColumnIndex(node) == (y + 1) && GridPane.getRowIndex(node) == (x + 1))
                .findFirst().orElse(null);
    }

    private void updateCellStyle(Button cell, ModelBattleship.CellStatus status) {
        switch (status) {
            case EMPTY:
                cell.setStyle("-fx-background-color: lightblue; -fx-font-weight: bold;");
                break;
            case SHIP:
                cell.setStyle("-fx-background-color: gray; -fx-font-weight: bold;");
                break;
            case HIT:
                cell.setStyle("-fx-background-color: red; -fx-font-weight: bold;");
                break;
            case MISS:
                cell.setStyle("-fx-background-color: white; -fx-font-weight: bold;");
                break;
        }
    }

    @FXML
    private void startGame() {
        System.out.println("Juego iniciado");
        model.placeShip(0, 0, 3, true);  // Coloca un barco de tamaño 3 en la esquina superior izquierda
        model.placeShip(2, 2, 4, false); // Coloca un barco de tamaño 4 en posición vertical
        updateBoard();
    }

    @FXML
    private void resetGame() {
        model = new ModelBattleship();  // Reinicia el modelo
        updateBoard();  // Actualiza la interfaz visual
        System.out.println("Juego reiniciado");
    }

    @FXML
    private void showInstructions() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Instrucciones del Juego");
        alert.setHeaderText("Cantidad de barcos y tamaños:");

        String instructions =
                "1. Portaaviones: Ocupa 4 casillas. ( Cantidad: 1)\n" +
                        "2. Submarinos: Ocupan 3 casillas cada uno. ( Cantidad: 2)\n" +
                        "3. Destructores: Ocupan 2 casillas cada uno. ( Cantidad: 3)\n" +
                        "4. Fragatas: Ocupan 1 casilla cada una. ( Cantidad: 4)";

        alert.setContentText(instructions);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Colocación no válida");
        alert.setContentText(message);
        alert.showAndWait();
    }
}




package ethan.batallanaval.Controller;

import ethan.batallanaval.Model.ModelBattleship;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.input.TransferMode;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ControllerBattleship {
    private ModelBattleship model;

    @FXML
    private GridPane boardGrid;

    @FXML
    private HBox shipsContainer;

    @FXML
    private Button showInstructions;

    @FXML
    private Button resetButton;

    private static final int BOARD_SIZE = 10;
    private static final int CELL_SIZE = 60;

    // Imágenes para los diferentes estados y tipos de barcos
    private Image waterImage, hitImage, sunkImage;
    private Image portaavionesImage, submarinoImage, destructorImage, fragataImage;

    public void initialize() {
        model = new ModelBattleship();
        loadImages();
        createBoard();
        initializeShips();
    }

    private void loadImages() {
        String basePath = getClass().getResource("/Imagenes/").toExternalForm();
        waterImage = new Image(basePath + "agua.png");
        hitImage = new Image(basePath + "tocado.png");
        sunkImage = new Image(basePath + "hundido.png");
        portaavionesImage = new Image(basePath + "portaaviones.png", CELL_SIZE * 4, CELL_SIZE, true, true);
        submarinoImage = new Image(basePath + "submarinos.png", CELL_SIZE * 3, CELL_SIZE, true, true);
        destructorImage = new Image(basePath + "destructores.png", CELL_SIZE * 2, CELL_SIZE, true, true);
        fragataImage = new Image(basePath + "fragatas.png", CELL_SIZE, CELL_SIZE, true, true);
    }

    private void createBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Button cell = createCell(i, j);
                boardGrid.add(cell, j, i);
            }
        }
    }

    private void initializeShips() {
        // Limpiar cualquier barco previamente colocado
        shipsContainer.getChildren().clear();

        // Asegurarse de que las filas (HBox) estén presentes antes de agregar los barcos
        if (shipsContainer.getChildren().isEmpty()) {
            shipsContainer.getChildren().add(new HBox(10));  // Primera fila (HBox)
            shipsContainer.getChildren().add(new HBox(10));  // Segunda fila (HBox)
        }

        // Crear los barcos en el orden correcto (de mayor a menor tamaño)

        // 1. Portaaviones (4 casillas) - Cantidad: 1
        if (model.getPortaavionesCount() < ModelBattleship.MAX_PORTAAVIONES) {
            createDraggableShip(portaavionesImage, 4);  // 1 Portaaviones
        }

        // 2. Submarinos (3 casillas cada uno) - Cantidad: 2
        for (int i = 0; i < ModelBattleship.MAX_SUBMARINOS; i++) {
            if (model.getSubmarinosCount() < ModelBattleship.MAX_SUBMARINOS) {
                createDraggableShip(submarinoImage, 3);    // 2 Submarinos
            }
        }

        // 3. Destructores (2 casillas cada uno) - Cantidad: 3
        for (int i = 0; i < ModelBattleship.MAX_DESTRUCTORES; i++) {
            if (model.getDestructoresCount() < ModelBattleship.MAX_DESTRUCTORES) {
                createDraggableShip(destructorImage, 2);   // 3 Destructores
            }
        }

        // 4. Fragatas (1 casilla cada una) - Cantidad: 4
        for (int i = 0; i < ModelBattleship.MAX_FRAGATAS; i++) {
            if (model.getFragatasCount() < ModelBattleship.MAX_FRAGATAS) {
                createDraggableShip(fragataImage, 1);      // 4 Fragatas
            }
        }
    }

    private void createDraggableShip(Image shipImage, int size) {
        Button shipButton = new Button();
        shipButton.setGraphic(new ImageView(shipImage));
        shipButton.setPrefSize(CELL_SIZE * size, CELL_SIZE);

        // Dragging logic
        shipButton.setOnDragDetected(event -> {
            Dragboard db = shipButton.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(size)); // Enviar el tamaño del barco
            db.setContent(content);
            event.consume();
        });

        // Se agregan los barcos a las filas
        HBox targetHBox = (HBox) shipsContainer.getChildren().get(
                ((HBox) shipsContainer.getChildren().get(0)).getChildren().size() < 2 ? 0 : 1
        );
        targetHBox.getChildren().add(shipButton);
    }

    private Button createCell(int x, int y) {
        Button cell = new Button();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);

        // Permitir el arrastre sobre la celda
        cell.setOnDragOver(event -> {
            if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // Colocar un barco en la celda
        cell.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                int shipSize = Integer.parseInt(db.getString());
                boolean placed = model.placeShip(x, y, shipSize, true); // Colocación horizontal por defecto
                if (placed) {
                    updateBoard();
                    success = true;
                } else {
                    showError("No se puede colocar el barco aquí.");
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        return cell;
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
                .filter(node -> GridPane.getColumnIndex(node) == (y) && GridPane.getRowIndex(node) == (x))
                .findFirst().orElse(null);
    }

    private void updateCellStyle(Button cell, ModelBattleship.CellStatus status) {
        switch (status) {
            case EMPTY:
                cell.setGraphic(new ImageView(waterImage));
                break;
            case SHIP:
                cell.setStyle("-fx-background-color: gray;");
                break;
            case HIT:
                cell.setGraphic(new ImageView(hitImage));
                break;
            case MISS:
                cell.setGraphic(new ImageView(waterImage));
                break;
            case SUNK:
                cell.setGraphic(new ImageView(sunkImage));
                break;
        }
    }

    @FXML
    private void startGame() {
        System.out.println("Juego iniciado");
        updateBoard();
    }

    @FXML
    private void resetGame() {
        model = new ModelBattleship(); // Reinicia el modelo
        clearBoard(); // Limpia el tablero visualmente
        shipsContainer.getChildren().clear(); // Elimina los barcos colocados en la interfaz
        initializeShips(); // Vuelve a agregar los barcos al contenedor para ser colocados nuevamente
        System.out.println("Juego reiniciado");
    }

    private void clearBoard() {
        for (Node node : boardGrid.getChildren()) {
            Button cell = (Button) node;
            cell.setGraphic(null); // Elimina cualquier imagen del botón
            cell.setStyle(""); // Elimina cualquier estilo de fondo aplicado
        }
    }

    @FXML
    private void showInstructions() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Instrucciones del Juego");
        alert.setHeaderText("Reglas y cantidad de barcos:");

        String instructions =
                "Reglas del Juego:\n" +
                        "- Coloca los barcos en el tablero antes de iniciar.\n" +
                        "- Los barcos pueden colocarse horizontalmente por defecto.\n" +
                        "- Si un barco es tocado pero no hundido, mostrará una bomba.\n" +
                        "- Si un barco es hundido, mostrará un fuego en las celdas correspondientes.\n\n" +
                        "Cantidad de Barcos:\n" +
                        "1. Portaaviones (4 casillas) - Cantidad: 1\n" +
                        "2. Submarinos (3 casillas cada uno) - Cantidad: 2\n" +
                        "3. Destructores (2 casillas cada uno) - Cantidad: 3\n" +
                        "4. Fragatas (1 casilla cada una) - Cantidad: 4\n";

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
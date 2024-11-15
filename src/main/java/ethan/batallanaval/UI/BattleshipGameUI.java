package ethan.batallanaval.UI;

import ethan.batallanaval.Model.ModelBattleship;
import ethan.batallanaval.Model.ModelBattleship.CellStatus;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BattleshipGameUI extends Application {
    private ModelBattleship model;

    @Override
    public void start(Stage primaryStage) {
        model = new ModelBattleship();  // Inicializamos el modelo del juego

        // Crear un nuevo tablero para el juego
        GridPane grid = createBoard();

        // Botón para disparar
        Button shootButton = new Button("Disparar");
        shootButton.setOnAction(e -> handleShootButton(grid));

        // Layout principal (agregar el tablero y el botón)
        GridPane mainLayout = new GridPane();
        mainLayout.setVgap(10);
        mainLayout.setHgap(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.add(grid, 0, 0);
        mainLayout.add(shootButton, 0, 1);

        // Crear la escena para el juego y agregarla a la ventana
        Scene scene = new Scene(mainLayout, 500, 600);
        primaryStage.setTitle("Batalla Naval");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Crea un tablero 10x10
    private GridPane createBoard() {
        GridPane board = new GridPane();
        board.setVgap(5);
        board.setHgap(5);

        // Recorrer las filas y columnas para crear celdas
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                // Crear un rectángulo para cada celda del tablero
                Rectangle cell = new Rectangle(40, 40);
                cell.setFill(Color.LIGHTBLUE);
                cell.setStroke(Color.BLACK);

                // Asignar un evento de clic para disparar
                final int x = i;
                final int y = j;
                cell.setOnMouseClicked(e -> handleCellClick(x, y, cell));

                // Agregar la celda al tablero
                board.add(cell, j, i);
            }
        }

        return board;
    }

    // Maneja el clic en una celda (disparo)
    private void handleCellClick(int x, int y, Rectangle cell) {
        // Lógica de disparo: obtenemos el estado de la celda (agua, tocado, hundido)
        CellStatus result = model.shoot(x, y);
        updateCellDisplay(cell, result);
    }

    // Actualiza la celda después de un disparo
    private void updateCellDisplay(Rectangle cell, CellStatus result) {
        switch (result) {
            case MISS:
                cell.setFill(Color.BLUE);  // Agua
                break;
            case HIT:
                cell.setFill(Color.RED);   // Tocado
                break;
            case SUNK:
                cell.setFill(Color.DARKRED);  // Hundido
                break;
            case EMPTY:
                cell.setFill(Color.LIGHTBLUE);  // Agua (vacía)
                break;
            default:
                break;
        }
    }

    // Lógica de disparo (podemos agregar más funciones si es necesario)
    private void handleShootButton(GridPane grid) {
        // Aquí podrías añadir más lógica, por ejemplo, mostrar el turno actual o controlar la cantidad de disparos
        System.out.println("Botón de disparo presionado");
    }
}
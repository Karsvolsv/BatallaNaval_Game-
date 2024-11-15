package ethan.batallanaval;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el archivo FXML para el menú principal
        Parent root = FXMLLoader.load(getClass().getResource("/MainMenu.fxml"));

        // Ajustar tamaño de la ventana
        primaryStage.setTitle("Batalla Naval");
        primaryStage.setScene(new Scene(root, 850, 850)); // Tamaño de la ventana
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package ethan.batallanaval;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/MainMenu.fxml"));

        // Ajustar tamaño de la ventana
        primaryStage.setTitle("Batalla Naval");
        // Tamaño de la ventana: 10 celdas de 60px (600px) + margen + altura de botones (200px aprox.)
        primaryStage.setScene(new Scene(root, 850, 850)); // Ancho 800px, alto 820px
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
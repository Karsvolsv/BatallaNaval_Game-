package ethan.batallanaval.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainMenuController {

    @FXML
    private Label welcomeText;

    @FXML
    private void onHelloButtonClick() {
        welcomeText.setText("¡Hola, bienvenido a Batalla Naval!");
    }
}

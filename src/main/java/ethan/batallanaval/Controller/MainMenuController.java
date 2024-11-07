package ethan.batallanaval.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainMenuController {


    @FXML
    private Button jugarButton;

    @FXML
    private Button salirButton;

    @FXML
    private void onJugarButtonClick() {
    }

    @FXML
    private void onSalirButtonClick() {
        Stage stage = (Stage) salirButton.getScene().getWindow();
        stage.close();
    }
}



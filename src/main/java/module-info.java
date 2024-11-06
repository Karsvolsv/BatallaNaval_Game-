module ethan.batallanaval {
    requires javafx.controls;
    requires javafx.fxml;


    opens ethan.batallanaval to javafx.fxml;
    opens ethan.batallanaval.Controller to javafx.fxml;

    exports ethan.batallanaval.Controller to javafx.fxml;
    exports ethan.batallanaval;
}
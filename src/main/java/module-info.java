module com.vendingmachine {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.vendingmachine to javafx.fxml;
    opens com.vendingmachine.controller to javafx.fxml;
    opens com.vendingmachine.model to javafx.base;

    exports com.vendingmachine;
    exports com.vendingmachine.controller;
    exports com.vendingmachine.model;
}

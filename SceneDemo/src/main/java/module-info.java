module com.example.scenedemo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.scenedemo to javafx.fxml;
    exports com.example.scenedemo;
}
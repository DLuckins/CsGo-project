module com.example.scenedemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.junit.jupiter.api;
    requires junit;


    opens com.example.scenedemo to javafx.fxml;
    exports com.example.scenedemo;
}
package com.example.scenedemo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FirstController {
    private Stage stage;
    private Scene scene;
    @FXML
    Label label1;
    @FXML
    private AnchorPane MainAn;

    @FXML
    private Button button;
    @FXML
    void SwitchTo2(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(First.class.getResource("Second.fxml"));
        stage=(Stage)((Node) event.getSource()).getScene().getWindow();
        scene=new Scene(fxmlLoader.load(), 600, 386);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    void SwitchTo1(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(First.class.getResource("First.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(fxmlLoader.load(), 600, 386);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void change1(MouseEvent event) {
        button.getScene().setCursor(Cursor.HAND);
    }
    @FXML
    void change2(MouseEvent event) {
        button.getScene().setCursor(Cursor.DEFAULT);
    }
    @FXML
    void ReadFile(ActionEvent event) throws IOException{
        Path fileName = Path.of("Example.txt");
        String actual = Files.readString(fileName);
        this.label1.setText(actual);

    }
}
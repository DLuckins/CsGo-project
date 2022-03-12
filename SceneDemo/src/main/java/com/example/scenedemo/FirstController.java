package com.example.scenedemo;
import java.lang.Object;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Condition;
import java.util.jar.Attributes;

import static com.example.scenedemo.Connect.Generator;

public class FirstController {
    private Stage stage;
    private Scene scene;
    @FXML
    private Label Updating;
    @FXML
    private Label resultLabel;
    @FXML
    private Button UpdatingButton;
    @FXML
    private Button Scene2Button;
    @FXML
    private Button GenerateButton;

    @FXML
    private Button GoBackButton;
    @FXML
    Label label1;
    @FXML
    private Pane MainPane;
    @FXML
    private Pane LoadingPane;
    @FXML
    private ImageView LoadingGif,LoadingGif2;
    @FXML
    private ImageView SkinImage1,SkinImage2,SkinImage3,SkinImage4,SkinImage5,SkinImage6,SkinImage7,SkinImage8,SkinImage9,SkinImage10;
    @FXML
    private AnchorPane MainAn;
    @FXML
    private Button button;
    @FXML
    private TableView<Data1> table1;
    @FXML
    private TableColumn<Data1,Double> AveragePrice;

    @FXML
    private TableColumn<Data1,String> COF;
    @FXML
    private TableColumn<Data1, String> Collection;

    @FXML
    private TableColumn<Data1,String> Condition;

    @FXML
    private TableColumn<Data1,Double> Cost;

    @FXML
    private TableColumn<Data1, Double> FP;

    @FXML
    private TableColumn<Data1,String> FW;

    @FXML
    private TableColumn<Data1, Integer> HMFTP;

    @FXML
    private TableColumn<Data1, Integer> HMTP;

    @FXML
    private TableColumn<Data1, String> NOF;

    @FXML
    private TableColumn<Data1, String> Name;

    @FXML
    private TableColumn<Data1, Double> Roi;
    @FXML
    private TableColumn<Data1, Void> VisualizeBtn;
    //clicking on button update
    @FXML
    void Update(ActionEvent event) throws IOException{
        MainPane.setVisible(false);
        LoadingPane.setVisible(true);
        Runtime runtime=Runtime.getRuntime();
        try {
        Process process1=runtime.exec("cmd /c py Price_Update.py");
        Thread thread=new Thread(()->{
            try{
                process1.waitFor();
                LoadingPane.setVisible(false);
                MainPane.setVisible(true);
                Updating.setVisible(true);
            }catch(InterruptedException e){
            }
        });
        thread.setDaemon(true);
        thread.start();
    } catch(IOException ioException) {}

    }
    //clicking on button generate on first screen
    @FXML
    void SwitchTo2(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(First.class.getResource("Second.fxml"));
        stage=(Stage)((Node) event.getSource()).getScene().getWindow();
        scene=new Scene(fxmlLoader.load(), 786, 515);
        stage.setScene(scene);
        stage.show();
    }
    //clicking on button go back(2 of those buttons in programm)
    @FXML
    void SwitchTo1(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(First.class.getResource("First.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(fxmlLoader.load(), 600, 380);
        stage.setScene(scene);
        stage.show();
    }
    //clicking button read me
    @FXML
    void SwitchToRead(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(First.class.getResource("ReadMe.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(fxmlLoader.load(), 600, 380);
        stage.setScene(scene);
        stage.show();
    }
    //double clicking on table row after generating
        void ChangeSceneToDetailedDataView(MouseEvent event) throws IOException{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("PopUp.fxml"));
            Parent table1Parent =fxmlLoader.load();
            Scene scene3= new Scene(table1Parent, 800, 750);
            PopUpController controller=fxmlLoader.getController();
            controller.InitData(table1.getSelectionModel().getSelectedItem());
            Stage popUpWindow=new Stage();
            popUpWindow.setScene(scene3);
            Image icon = new Image(getClass().getResourceAsStream("logo.png"));
            popUpWindow.getIcons().add(icon);
            popUpWindow.show();
        }
        //getting data from database into table
    public ObservableList<Data1> getData() throws Exception{
        Name.setCellValueFactory(new PropertyValueFactory<Data1,String >("Name"));
        Collection.setCellValueFactory(new PropertyValueFactory<Data1,String >("Collection"));
        Condition.setCellValueFactory(new PropertyValueFactory<Data1,String>("Condition"));
        AveragePrice.setCellValueFactory(new PropertyValueFactory<Data1,Double>("AveragePrice"));
        HMTP.setCellValueFactory(new PropertyValueFactory<Data1,Integer>("HMTP"));
        NOF.setCellValueFactory(new PropertyValueFactory<Data1,String>("NOF"));
        COF.setCellValueFactory(new PropertyValueFactory<Data1,String>("COF"));
        FW.setCellValueFactory(new PropertyValueFactory<Data1,String>("FW"));
        FP.setCellValueFactory(new PropertyValueFactory<Data1,Double>("FP"));
        HMFTP.setCellValueFactory(new PropertyValueFactory<Data1,Integer>("HMFTP"));
        Cost.setCellValueFactory(new PropertyValueFactory<Data1,Double>("Cost"));
        Roi.setCellValueFactory(new PropertyValueFactory<Data1,Double>("ROI"));

        String pathToDB = "FinalSkinsDb.db";
        ObservableList<Data1> data=FXCollections.observableArrayList();
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + pathToDB);
        String sqlite="SELECT * FROM ProfitableTradeups ORDER BY ROI DESC;";
        Statement statement=connection.createStatement();
        ResultSet result = statement.executeQuery(sqlite);
        while ( result.next()){
           data.add(new Data1(result.getString("NameOfValue"),result.getString("Collection"), result.getString("Wear"),
                   result.getDouble("Price"),result.getInt("HowManyToPutIn"),result.getString("NameOfFiller"),
                   result.getString("CollectionOfFiller"),result.getString("FillerWear"),result.getDouble("FillerPrice"),
                   result.getInt("HowManyFillersToPut"),result.getDouble("Cost"),result.getDouble("ROI")));

        }
    return data;
    }
    //double clicking on table row after generating
    @FXML
    public void clickItem(MouseEvent event)
    {
        if (event.getClickCount() == 2) //Checking double click
        {
            try {
                ChangeSceneToDetailedDataView(event);
            }
            catch(Exception e){

            }
        }
    }
    //clicking on generate button on the second scene
    @FXML
    void Generate(ActionEvent event) throws Exception {
        new Thread(() -> {
            GenerateButton.setDisable(true);
            GoBackButton.setDisable(true);
            LoadingGif2.setVisible(true);
            Generator();
            LoadingGif2.setVisible(false);
            GenerateButton.setDisable(false);
            GoBackButton.setDisable(false);
            try{
                table1.setItems(getData());
            }
            catch(Exception e){

            }
        }).start();
        }
    }
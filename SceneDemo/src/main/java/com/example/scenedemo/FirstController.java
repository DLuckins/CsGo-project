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
    @FXML
    void Update(ActionEvent event) throws IOException{
        LoadingGif.setVisible(true);
        UpdatingButton.setDisable(true);
        Scene2Button.setDisable(true);
        Runtime runtime=Runtime.getRuntime();
        try {
        Process process1=runtime.exec("cmd /c py Price_Update.py");
        Thread thread=new Thread(()->{
            try{
                process1.waitFor();
                LoadingGif.setVisible(false);
                Updating.setVisible(true);
                UpdatingButton.setDisable(false);
                Scene2Button.setDisable(false);
            }catch(InterruptedException e){
            }
        });
        thread.setDaemon(true);
        thread.start();
    } catch(IOException ioException) {}

    }
    @FXML
    void SwitchTo2(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(First.class.getResource("Second.fxml"));
        stage=(Stage)((Node) event.getSource()).getScene().getWindow();
        scene=new Scene(fxmlLoader.load(), 786, 515);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    void SwitchTo1(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(First.class.getResource("First.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(fxmlLoader.load(), 600, 380);
        stage.setScene(scene);
        stage.show();
    }
    //@FXML
   // void OpenPopUp() {
      //  try {
          //  FXMLLoader fxmlLoader = new FXMLLoader(First.class.getResource("PopUp.fxml"));
          //  Stage popupwindow = new Stage();
          //  Scene scene3 = new Scene(fxmlLoader.load(), 600, 380);
          //  popupwindow.setScene(scene3);
          //  popupwindow.show();
      //  } catch (Exception e) {

     //   }
   // }
        void ChangeSceneToDetailedDataView(MouseEvent event) throws IOException{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("PopUp.fxml"));
            Parent table1Parent =fxmlLoader.load();
            Scene scene3= new Scene(table1Parent, 800, 600);
            PopUpController controller=fxmlLoader.getController();
            controller.InitData(table1.getSelectionModel().getSelectedItem());
            Stage popUpWindow=new Stage();
            popUpWindow.setScene(scene3);
            popUpWindow.show();
        }
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
          //  String[] s=new String[10];
           // Image[] image=new Image[10];
           // int Images1=(table1.getSelectionModel().getSelectedItem().getHMTP());
           // for(int i=0;i<Images1;i++){
            //    image[i]=new Image("https://steamcommunity-a.akamaihd.net/economy/image/"+"-9a81dlWLwJ2UUGcVs_nsVtzdOEdtWwKGZZLQHTxDZ7I56KU0Zwwo4NUX4oFJZEHLbXH5ApeO4YmlhxYQknCRvCo04DEVlxkKgpot7HxfDhhwszHeDFH6OO7kYSCgvq6Yu-EwzsIuZIj3uiY99WmiwGx_kc9Zjr6JYHBIwM5MFHX-Fi9w-u-1Ij84soNG54bQA");

          //  }
            //int Images2=(table1.getSelectionModel().getSelectedItem().getHMFTP()+Images1);
           // for(Images1=Images1;Images1<Images2;Images1++){
            //    image[Images1]=new Image("https://steamcommunity-a.akamaihd.net/economy/image/"+"-9a81dlWLwJ2UUGcVs_nsVtzdOEdtWwKGZZLQHTxDZ7I56KU0Zwwo4NUX4oFJZEHLbXH5ApeO4YmlhxYQknCRvCo04DEVlxkKgpot7HxfDhhwszJemkV09-3hpSOm8j4OrzZgiUD7ZJzj7DHoY-iilC1-ENoNWuiI9WWdQI8Z1iE81Tvl7i81J-_6p2b1zI97XPwFCE_");

           // }
           // SkinImage1.setImage(image[0]);
           // SkinImage2.setImage(image[1]);
           // SkinImage3.setImage(image[2]);
           // SkinImage4.setImage(image[3]);
           // SkinImage5.setImage(image[4]);
            //SkinImage6.setImage(image[5]);
           // SkinImage7.setImage(image[6]);
            //SkinImage8.setImage(image[7]);
           // SkinImage9.setImage(image[8]);
            //SkinImage10.setImage(image[9]);


            //resultLabel.setText((table1.getSelectionModel().getSelectedItem().getHMTP())+" of "+(table1.getSelectionModel().getSelectedItem().getName())+ "("+(table1.getSelectionModel().getSelectedItem().getCondition())
                   // +") + "+(table1.getSelectionModel().getSelectedItem().getHMFTP())+ " of " +(table1.getSelectionModel().getSelectedItem().getNOF())+"("+(table1.getSelectionModel().getSelectedItem().getFW())+")");
        }
    }
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
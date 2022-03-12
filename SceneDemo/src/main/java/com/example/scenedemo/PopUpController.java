package com.example.scenedemo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.scenedemo.OutputGenerator.*;

public class PopUpController {
    private Stage stage;
    private Scene scene;
    @FXML
    private ImageView Img1;

    @FXML
    private ImageView Img10;

    @FXML
    private ImageView Img2;

    @FXML
    private ImageView Img3;

    @FXML
    private ImageView Img4;

    @FXML
    private ImageView Img5;

    @FXML
    private ImageView Img6;

    @FXML
    private ImageView Img7;

    @FXML
    private ImageView Img8;

    @FXML
    private ImageView Img9;

    @FXML
    private Pane ItemsPane;
    @FXML
    private Pane ValuesPane;
    @FXML
    private Pane SPOpane;
    @FXML
    private Label TotalCost;
    @FXML
    private Label AverageRecievedCost;
    @FXML
    private Label ProfitAmount;

    @FXML
    private Label name;

    @FXML
    private Label name1;

    @FXML
    private Label name2;

    @FXML
    private Label name3;

    @FXML
    private Label name4;

    @FXML
    private Label name5;

    @FXML
    private Label name6;

    @FXML
    private Label name7;

    @FXML
    private Label name8;

    @FXML
    private Label name9;
    @FXML
    private ImageView Output;

    @FXML
    private ImageView Output1;

    @FXML
    private ImageView Output2;

    @FXML
    private ImageView Output3;

    @FXML
    private ImageView Output4;

    @FXML
    private ImageView Output5;

    @FXML
    private ImageView Output6;

    @FXML
    private ImageView Output7;

    @FXML
    private ImageView Output8;

    @FXML
    private ImageView Output9;
    @FXML
    private ImageView Output10;

    @FXML
    private ImageView Output11;
    @FXML
    private Label OLabel;

    @FXML
    private Label OLabel1;

    @FXML
    private Label OLabel2;

    @FXML
    private Label OLabel3;

    @FXML
    private Label OLabel4;

    @FXML
    private Label OLabel5;

    @FXML
    private Label OLabel6;

    @FXML
    private Label OLabel7;

    @FXML
    private Label OLabel8;

    @FXML
    private Label OLabel9;
    @FXML
    private Label OLabel10;

    @FXML
    private Label OLabel11;

    List <ImageView> imageView ;
    String[] textUnderImage=new String[10];
    Image[] FinImage=new Image[10];
    String popUpPaneStyle;
    public Data1 selectedData;
    public void InitData(Data1 data) throws IOException {
        selectedData=data;
    }
    //clicking on show items button
    public void ShowItems(ActionEvent event) throws SQLException {
        ValuesPane.setVisible(false);
        SPOpane.setVisible(false);
        ItemsPane.setVisible(true);
                int Images1 = selectedData.getHMTP();
                int Images2 = selectedData.getHMFTP() + Images1;
                String[] url=new String[2];
                String pathToDB = "FinalSkinsDb.db";
                try(Connection connection = DriverManager.getConnection("jdbc:sqlite:" + pathToDB)) {
                    String sqlite = "SELECT * FROM Skins WHERE Name=? AND Condition=?";
                    PreparedStatement statement = connection.prepareStatement(sqlite);
                    statement.setString(1, selectedData.getName());
                    statement.setString(2, selectedData.getCondition());
                    ResultSet resultSet = statement.executeQuery();
                    url[0] = resultSet.getString("IconUrl");
                    String Rarity=resultSet.getString("Rarity");
                    //getting rarity color
                    switch(Rarity){
                        case "Classified":
                            popUpPaneStyle="-fx-background-color: #d32ee6;";
                            break;
                        case "Consumer Grade":
                            popUpPaneStyle="-fx-background-color: #b0c3d9;";
                            break;
                        case "Industrial Grade":
                            popUpPaneStyle="-fx-background-color: #5e98d9;";
                            break;
                        case "Mil-Spec Grade":
                            popUpPaneStyle="-fx-background-color: #4b69ff;";
                            break;
                        case "Restricted":
                            popUpPaneStyle="-fx-background-color: #8847ff;";
                            break;
                    }
                    ItemsPane.setStyle(popUpPaneStyle);
                    statement.setString(1, selectedData.getNOF());
                    statement.setString(2, selectedData.getFW());
                    resultSet=statement.executeQuery();
                    url[1]=resultSet.getString("IconUrl");
                    for (int i = 0; i < Images1; i++) {
                        textUnderImage[i] = selectedData.getName() + "\n(" + selectedData.getCondition() + ")\n"+selectedData.getAveragePrice()+"€";
                        FinImage[i] = new Image(url[0]);
                    }
                    for (Images1 = Images1; Images1 < Images2; Images1++) {
                        textUnderImage[Images1] = selectedData.getNOF() + "\n(" + selectedData.getFW() + ")\n"+selectedData.getFP()+"€";
                        FinImage[Images1] = new Image(url[1]);
                    }
                    Img1.setImage(FinImage[0]);
                    Img2.setImage(FinImage[1]);
                    Img3.setImage(FinImage[2]);
                    Img4.setImage(FinImage[3]);
                    Img5.setImage(FinImage[4]);
                    Img6.setImage(FinImage[5]);
                    Img7.setImage(FinImage[6]);
                    Img8.setImage(FinImage[7]);
                    Img9.setImage(FinImage[8]);
                    Img10.setImage(FinImage[9]);
                    name.setText(textUnderImage[0]);
                    name1.setText(textUnderImage[1]);
                    name2.setText(textUnderImage[2]);
                    name3.setText(textUnderImage[3]);
                    name4.setText(textUnderImage[4]);
                    name5.setText(textUnderImage[5]);
                    name6.setText(textUnderImage[6]);
                    name7.setText(textUnderImage[7]);
                    name8.setText(textUnderImage[8]);
                    name9.setText(textUnderImage[9]);
                }
                        catch (SQLException e){
                        System.out.println(e.getErrorCode() + ":" + e.getMessage());
                    }



    }
    //clicking on show value button
    public void ShowValue(ActionEvent event){
        ItemsPane.setVisible(false);
        SPOpane.setVisible(false);
        ValuesPane.setVisible(true);
        DecimalFormat df = new DecimalFormat("###.##");
        double averageReceived= selectedData.getCost()*(selectedData.getROI()/100);
        double profit=averageReceived-selectedData.getCost();
    TotalCost.setText(String.valueOf(df.format(selectedData.getCost()))+"€");
    AverageRecievedCost.setText(String.valueOf(df.format(averageReceived))+"€");
    ProfitAmount.setText(String.valueOf(df.format(profit))+"€");
    }
    //clicking on show outcomes button
    @FXML
    void SPO(ActionEvent event) throws SQLException{
        ValuesPane.setVisible(false);
        ItemsPane.setVisible(false);
        SPOpane.setVisible(true);
        String pathToDB = "FinalSkinsDb.db";
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + pathToDB);
        OutputGenerator output=new OutputGenerator();
        List<String> url = output.ImgSrc(selectedData);
        Image[] outcomes=new Image[12];
        if (url!=null) {
            for (int i = 0; i < url.size(); i++) {
                outcomes[i] = new Image(url.get(i));

                // System.out.println(url.get(i));
            }
        }
        String Rarity=getRarity();
        //getting higher rarity color
        switch(Rarity){
            case "Classified":
                popUpPaneStyle="-fx-background-color: #d32ee6;";
                break;
            case "Covert":
                popUpPaneStyle="-fx-background-color: #eb4b4b;";
                break;
            case "Industrial Grade":
                popUpPaneStyle="-fx-background-color: #5e98d9;";
                break;
            case "Mil-Spec Grade":
                popUpPaneStyle="-fx-background-color: #4b69ff;";
                break;
            case "Restricted":
                popUpPaneStyle="-fx-background-color: #8847ff;";
                break;}
        SPOpane.setStyle(popUpPaneStyle);
        outputCondition=getOutputCondition();
        //setting images into pane SpoPane
        Output.setImage(outcomes[0]);
        Output1.setImage(outcomes[1]);
        Output2.setImage(outcomes[2]);
        Output3.setImage(outcomes[3]);
        Output4.setImage(outcomes[4]);
        Output5.setImage(outcomes[5]);
        Output6.setImage(outcomes[6]);
        Output7.setImage(outcomes[7]);
        Output8.setImage(outcomes[8]);
        Output9.setImage(outcomes[9]);
        Output10.setImage(outcomes[10]);
        Output11.setImage(outcomes[11]);
        String[] imageOutputName= output.getOutputName();
        double[] chancesForItems=new double[12];
        for(int i=0;i<output.getHowManyInNextTier2();i++){
            chancesForItems[i]=output.getChanceFiller();
        }
        for (int i= output.getHowManyInNextTier2();i<output.getHowManyInNextTier2()+output.getHowManyInNextTier1();i++){
            chancesForItems[i]= output.getChanceGood();
        }
        String[] outputPrice=output.getOutputPrice();
        String outputCondition=getOutputCondition();
        String[] outputText=new String[12];
        for(int i=0; i<12;i++){
            outputText[i]=null;
        }
        for(int i=0;i<imageOutputName.length;i++){
            if (imageOutputName[i]!=null){
                outputText[i]=imageOutputName[i]+"\n("+outputCondition+")\n"+outputPrice[i]+"€\n"+chancesForItems[i]+"%";
            }
        }
        //setting text into pane Spopane
        OLabel.setText(outputText[0]);
        OLabel1.setText(outputText[1]);
        OLabel2.setText(outputText[2]);
        OLabel3.setText(outputText[3]);
        OLabel4.setText(outputText[4]);
        OLabel5.setText(outputText[5]);
        OLabel6.setText(outputText[6]);
        OLabel7.setText(outputText[7]);
        OLabel8.setText(outputText[8]);
        OLabel9.setText(outputText[9]);
        OLabel10.setText(outputText[10]);
        OLabel11.setText(outputText[11]);
    }
    }



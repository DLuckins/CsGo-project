package com.example.scenedemo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OutputGenerator {
    String[] outputName=new String[12];
    String[] outputPrice=new String[12];
    int howManyInNextTier1=0;
    int howManyInNextTier2=0;
    double allOutcomes;
    double chanceGood;
    double chanceFiller;
    static String outputCondition;
    static String RarityOutput;
    public  List<String> ImgSrc (Data1 data) {
        String pathToDB = "FinalSkinsDb.db";
        String Rarity="";
        int i=0;
        List<String> url = new ArrayList();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + pathToDB)) {
            //getting selected item from database
            String sqlite = "SELECT * FROM Skins WHERE Name=? AND Condition=?";
            PreparedStatement statement = connection.prepareStatement(sqlite);
            statement.setString(1,data.getName());
            statement.setString(2,data.getCondition());
            ResultSet resultSet=statement.executeQuery();
            while(resultSet.next()) {
                Rarity = resultSet.getString("Rarity");
                howManyInNextTier1 = resultSet.getInt("HowManyInNextTier");
            }
            statement.setString(1,data.getNOF());
            statement.setString(2,data.getFW());
            resultSet=statement.executeQuery();
            while (resultSet.next()) {
                howManyInNextTier2 = resultSet.getInt("HowManyInNextTier");
            }
            System.out.println(howManyInNextTier1+" "+ howManyInNextTier2);
            //counting outcomes chances
            allOutcomes=(howManyInNextTier1*2+howManyInNextTier2*8);
            chanceGood=(Math.round((((2)/allOutcomes))*1000.0)/1000.0)*100;
            chanceFiller=(Math.round((((8)/allOutcomes))*1000.0)/1000.0)*100;
            System.out.println(chanceGood+ " " +chanceFiller);
            //getting all possible items in outcome
            String sqlite2 = "SELECT * FROM Skins WHERE (Collection=? OR Collection =?) AND Condition=? AND Rarity=?";
            PreparedStatement statement2 = connection.prepareStatement(sqlite2);
            statement2.setString(1,data.getCollection());
            statement2.setString(2,data.getCOF());
            System.out.println(data.getFW());
            statement2.setString(3, data.getFW());
            outputCondition= data.getFW();
        switch(Rarity){
            case "Classified":
                statement2.setString(4,"Covert");
                RarityOutput="Covert";
                break;
            case "Consumer Grade":
                statement2.setString(4,"Industrial Grade");
                RarityOutput="Industrial Grade";
                break;
            case "Industrial Grade":
                statement2.setString(4,"Mil-Spec Grade");
                RarityOutput="Mil-Spec Grade";
                break;
            case "Mil-Spec Grade":
                statement2.setString(4,"Restricted");
                RarityOutput="Restricted";
                break;
            case "Restricted":
                statement2.setString(4,"Classified");
                RarityOutput="Classified";
                break;
        }
            for(int j=0;j<12;j++){
                outputName[i]=null;
            }
            resultSet=statement2.executeQuery();
            while (resultSet.next()){
                url.add(resultSet.getString("IconUrl"));
                outputName[i]=resultSet.getString("Name");
                outputPrice[i]=resultSet.getString("Price");
               i++;
            }
            return url;
        } catch (SQLException e) {
            e.getMessage();
        }
        return null;
    }
    String[] getOutputName(){
        return outputName;
    }
    static String getOutputCondition(){
        return outputCondition;
    }
    static String getRarity(){
        return RarityOutput;
    }
    String[] getOutputPrice(){
        return outputPrice;
    }

    public double getChanceGood() {
        return chanceGood;
    }

    public double getChanceFiller() {
        return chanceFiller;
    }

    public int getHowManyInNextTier1(){
        return howManyInNextTier1;
    }
    public int getHowManyInNextTier2(){
        return howManyInNextTier2;
    }
}

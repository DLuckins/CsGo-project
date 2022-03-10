package com.example.scenedemo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OutputGenerator {
    static String[] outputName=new String[10];
    static String outputCondition;
    public static List<String> ImgSrc (Data1 data) {
        String pathToDB = "FinalSkinsDb.db";
        String Rarity="";
        int i=0;
        List<String> url = new ArrayList();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + pathToDB)) {
            String sqlite = "SELECT * FROM Skins WHERE Name=? AND Condition=?";
            PreparedStatement statement = connection.prepareStatement(sqlite);
            statement.setString(1,data.getName());
            statement.setString(2,data.getCondition());
            ResultSet resultSet=statement.executeQuery();
            while (resultSet.next()){
                Rarity=resultSet.getString("Rarity");
            }
            String sqlite2 = "SELECT * FROM Skins WHERE (Collection=? OR Collection =?) AND Condition=? AND Rarity=?";
            PreparedStatement statement2 = connection.prepareStatement(sqlite2);
            statement2.setString(1,data.getCollection());
            statement2.setString(2,data.getCOF());
            switch (data.getFW()){
                case "Factory New":
                    statement2.setString(3,"Minimal Wear");
                    outputCondition="Minimal Wear";
                    break;
                case "Minimal Wear":
                    statement2.setString(3,"Field-Tested");
                    outputCondition="Field-Tested";
                    break;
                case "Field-Tested":
                    statement2.setString(3,"Well-Worn");
                    outputCondition="Well-Worn";
                    break;
               // default:
                   // statement2.setString(3,"Well-Worn");
                   // break;
            }
        switch(Rarity){
            case "Classified":
                statement2.setString(4,"Covert");
                break;
            case "Consumer grade":
                statement2.setString(4,"Industrial grade");
                break;
            case "Industrial grade":
                statement2.setString(4,"Mil-spec");
                break;
            case "Mil-spec":
                statement2.setString(4,"Restricted");
                break;
            case "Restricted":
                statement2.setString(4,"Classified");
                break;
            default:
                statement2.setString(4,"Classified");
                break;
        }
        resultSet=statement2.executeQuery();
            while (resultSet.next()){
                url.add(resultSet.getString("IconUrl"));
                outputName[i]=resultSet.getString("Name");
               i++;
                //System.out.println(resultSet.getString("IconUrl"));
            }
            return url;
        } catch (SQLException e) {
            e.getMessage();
        }
        return null;
    }
    static String[] getOutputName(){
        return outputName;
    }
    static String getOutputCondition(){
        return outputCondition;
    }

}

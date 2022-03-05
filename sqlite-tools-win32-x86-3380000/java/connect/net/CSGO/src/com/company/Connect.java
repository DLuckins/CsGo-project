package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Condition;

public class Connect {
    private static String jbcUrl = "jdbc:sqlite:/C:\\sqlite-tools-win32-x86-3380000\\FinalSkinsDb.db";
    public static List<String> rarities =new ArrayList<>(Arrays.asList("Consumer Grade", "Industrial Grade", "Mil-Spec Grade", "Restricted", "Classified", "Covert"));
    public static List<String> conditions = new ArrayList<>(Arrays.asList("Battle-Scarred", "Well-Worn", "Field-Tested", "Minimal Wear", "Factory new"));


    public static void main(String[] args) {
        {
            try {

                Connection connection;
                connection = DriverManager.getConnection(jbcUrl);
                Statement statement = connection.createStatement();
                String sql = "SELECT * FROM Skins";
                ResultSet result = statement.executeQuery(sql);

                while (result.next()) {
                    updateNextTierPrice(connection, result);
                }
               checkIfProfit(connection);

            } catch (SQLException e) {
                System.out.println("Error connecting to SQLite database");
                e.printStackTrace();
            }
        }
    }


    public static void updateNextTierPrice(Connection connection, ResultSet result) throws SQLException {

        while (result.next()) {
            int id = result.getInt("Id");

            String sqlUpdateNextPrice = "UPDATE Skins SET NextTierPrice = ? WHERE Id == "+id;

            String sqlUpdateValueAdded = "UPDATE Skins SET ValueAdded = ? WHERE Id == "+id;
            String sqlUpdateValueTaken = "UPDATE Skins SET ValueTaken = ? WHERE Id == "+id;
            //String sqlUpdatePrice = "UPDATE AllSkins SET price_buy_orders=trim(price_buy_orders, \"€\") WHERE Id =="+id;

            String collection = result.getString("Collection");
            String rarity = result.getString("Rarity");
            String condition = result.getString("Condition");

            double nextPrice = nextTierPrice(collection, rarity, connection, condition, result);
            nextSameTierPrice(collection, rarity, connection, condition, result);
            double valueAdded = ValueAdded(connection, result);
            double valueTaken = ValueTaken(connection, result);


            PreparedStatement pstmtNextPrice = connection.prepareStatement(sqlUpdateNextPrice);
            PreparedStatement pstmtValueAdded = connection.prepareStatement(sqlUpdateValueAdded);
            PreparedStatement pstmtValueTaken = connection.prepareStatement(sqlUpdateValueTaken);

            pstmtNextPrice.setDouble(1, nextPrice);
            pstmtNextPrice.executeUpdate();

            pstmtValueAdded.setDouble(1, valueAdded);
            pstmtValueAdded.executeUpdate();

            pstmtValueTaken.setDouble(1, valueTaken);
            pstmtValueTaken.executeUpdate();



        }
    }





    public static double nextSameTierPrice(String collection, String rarity, Connection connection, String condition, ResultSet result) throws SQLException {

        Statement statements = connection.createStatement();

        int raritiesNum = 0;
        if (!Objects.equals(rarity, "Covert")) {
            raritiesNum = rarities.indexOf(rarity) + 1;
            String sqlForSameTier = "SELECT * FROM Skins WHERE Collection==\"" + collection + "\" AND Condition==\""+ result.getString("Condition") +"\" AND Rarity==\"" + rarities.get(raritiesNum) + "\"";
            ResultSet resultsForSameTier = statements.executeQuery(sqlForSameTier);
            double allNextTierPrice = 0.0000;
            double howManyInTier = 0.000;
            double allNextSameTierPrice = 0;
            while (resultsForSameTier.next()) {

                double priceForSameTier = getPrecisePrice(resultsForSameTier.getString("Price"));
                allNextSameTierPrice+=priceForSameTier;

                howManyInTier+=1.00000;
            }

            int id = result.getInt("Id");
            String sqlUpdateSamePrice = "UPDATE Skins SET NextTierPriceForSameTier = ? WHERE Id == "+id;
            PreparedStatement pstmtHowManyInNextSameTier = connection.prepareStatement(sqlUpdateSamePrice);
            pstmtHowManyInNextSameTier.setDouble(1, allNextSameTierPrice/howManyInTier);
            pstmtHowManyInNextSameTier.executeUpdate();


            double ret =allNextTierPrice / howManyInTier;
            return ret;
        }
        else return 0;
    }



        public static double nextTierPrice(String collection, String rarity, Connection connection, String condition, ResultSet result) throws SQLException {

            Statement statements = connection.createStatement();

            int raritiesNum = 0;
            String tempCondition;
            if (!Objects.equals(rarity, "Covert")) {
                if (Objects.equals(condition, "Battle-Scarred")){
                    tempCondition = "Field-Tested";
                } else if (Objects.equals(condition, "Well-Worn")){
                    tempCondition = "Minimal Wear";
                } else{
                    tempCondition = "Factory New";
                }

                raritiesNum = rarities.indexOf(rarity) + 1;
                String sql = "SELECT * FROM Skins WHERE Collection==\"" + collection + "\" AND Condition==\""+ tempCondition +"\" AND Rarity==\"" + rarities.get(raritiesNum) + "\"";
                ResultSet results = statements.executeQuery(sql);

                double allNextTierPrice = 0.0000;
                double howManyInTier = 0.000;
                while (results.next()) {

                    double price = getPrecisePrice(results.getString("Price"));

                    allNextTierPrice += price;
                    howManyInTier+=1.00000;
                }

                int id = result.getInt("Id");
                String sqlUpdateHowManyInTier = "UPDATE Skins SET HowManyInNextTier = ? WHERE Id == "+id;
                PreparedStatement pstmtHowManyInNextTier = connection.prepareStatement(sqlUpdateHowManyInTier);
                pstmtHowManyInNextTier.setDouble(1, howManyInTier);
                pstmtHowManyInNextTier.executeUpdate();

                double ret =allNextTierPrice / howManyInTier;
                return ret;
            }
            else return 0;
        }

        private static double ValueAdded(Connection connection, ResultSet result) throws SQLException {
            Statement statements = connection.createStatement();
            double nextPrice = getPrecisePrice(result.getString("NextTierPrice"));

            double price = getPrecisePrice(result.getString("Price"));
            int howManyInNextTier = result.getInt("HowManyInNextTier");
            double valueAdded = ((nextPrice/10f) - price)*howManyInNextTier;
            return valueAdded;
        }

        private static double ValueTaken (Connection connection, ResultSet result) throws SQLException {
            Statement statements = connection.createStatement();
            double nextPrice = getPrecisePrice(result.getString("NextTierPriceForSameTier"));
            double price = getPrecisePrice(result.getString("Price"));
            int howManyInNextTier = result.getInt("HowManyInNextTier");
            double valueTaken = ((price) - ((nextPrice)/10f))*howManyInNextTier;
            return valueTaken;
        }


        private static void checkIfProfit(Connection connection) throws SQLException {
            Statement statement = connection.createStatement();
            String getGoodAdders = "SELECT * FROM Skins WHERE Rarity!= \"Covert\" AND ValueAdded > 0";
            ResultSet results = statement.executeQuery(getGoodAdders);
            while (results.next()) {
                double goodPrice = getPrecisePrice(results.getString("Price"));
                double goodNextPrice =   getPrecisePrice(results.getString("NextTierPrice"));
                int howManyInNextTierGood = results.getInt("HowManyInNextTier");

                String goodRarity = results.getString("Rarity");
                String goodCondition = results.getString("Condition");
                double valueAdded = getPrecisePrice(results.getString("ValueAdded"))/4;
                String tempCondition;

                if (Objects.equals(goodCondition, "Battle-Scarred")){
                    tempCondition = "Field-Tested";
                } else if (Objects.equals(goodCondition, "Well-Worn")){
                    tempCondition = "Minimal Wear";
                } else{
                    tempCondition = "Factory New";
                }

                Statement statements = connection.createStatement();
                String getFill = "SELECT * FROM Skins WHERE Rarity ==\""+goodRarity+"\" AND Condition == \"" + tempCondition + "\" AND HowManyInNextTier <= \"" + howManyInNextTierGood + "\" AND ValueTaken <\"" + valueAdded+"\"";
                ResultSet resultFill = statements.executeQuery(getFill);

                while (resultFill.next()) {

                    int fillHowManyInTier = resultFill.getInt("HowManyInNextTier");
                    double fillPrice = getPrecisePrice(resultFill.getString("Price"));
                    double fillNextTierPrice = getPrecisePrice(resultFill.getString("NextTierPrice"));

                    if (fillHowManyInTier != 0 && howManyInNextTierGood != 0 ) {
                        int allOutcomes = fillHowManyInTier + howManyInNextTierGood;

                        float oneToNineOutcome = (float) (((float)(howManyInNextTierGood / allOutcomes) * goodNextPrice + ((float)(fillHowManyInTier * 9) / allOutcomes) * fillNextTierPrice));
                        float oneToNinePrice = (float) (goodPrice + fillPrice * 9);
                        float ROIforOneToNine = (oneToNineOutcome - oneToNinePrice) / oneToNinePrice;

                        String sqlInsertProfit = "INSERT INTO ProfitableTradeUps(NameOfValue, Collection, Wear, Price, HowManyToPutIn, NameOfFiller, CollectionOfFiller, FillerWear, FillerPrice, HowManyFillersToPut, Cost, ROI) VALUES(?,?,?,?,?,?,?,?,?,?,?, ?)";
                        if (ROIforOneToNine > 5 && ROIforOneToNine < 100) {

                            PreparedStatement insertProfit;
                            insertProfit = connection.prepareStatement(sqlInsertProfit);
                            insertProfit.setString(1, results.getString("name"));
                            insertProfit.setString(2, results.getString("Collection"));
                            insertProfit.setString(3, results.getString("Condition"));
                            insertProfit.setFloat(4, (float) goodPrice);
                            insertProfit.setInt(5, 1);
                            insertProfit.setString(6, resultFill.getString("name"));
                            insertProfit.setString(7, resultFill.getString("Collection"));
                            insertProfit.setString(8, resultFill.getString("Condition"));
                            insertProfit.setDouble(9, fillPrice);
                            insertProfit.setInt(10, 9);
                            insertProfit.setFloat(11, oneToNinePrice);
                            insertProfit.setFloat(12, ROIforOneToNine);
                            insertProfit.executeUpdate();

                        }

                        float oneToEightOutcome = (float) (((float)(howManyInNextTierGood * 2) / allOutcomes) * goodNextPrice + ((float)(fillHowManyInTier * 8) / allOutcomes) * fillNextTierPrice);
                        float oneToEightPrice = (float) (goodPrice * 2 + fillPrice * 8);
                        float ROIforOneToEight = (oneToEightOutcome - oneToEightPrice) / oneToEightPrice;
                        System.out.println(results.getFloat("Price"));

                        if (ROIforOneToEight > 5 && ROIforOneToEight < 100 ) {

                            PreparedStatement insertProfit = connection.prepareStatement(sqlInsertProfit);
                            insertProfit.setString(1, results.getString("name"));
                            insertProfit.setString(2, results.getString("Collection"));
                            insertProfit.setString(3, results.getString("Condition"));
                            insertProfit.setFloat(4, (float) goodPrice);
                            insertProfit.setInt(5, 2);
                            insertProfit.setString(6, resultFill.getString("name"));
                            insertProfit.setString(7, resultFill.getString("Collection"));
                            insertProfit.setString(8, resultFill.getString("Condition"));
                            insertProfit.setDouble(9, fillPrice);
                            insertProfit.setInt(10, 8);
                            insertProfit.setFloat(11, oneToEightPrice);
                            insertProfit.setFloat(12, ROIforOneToEight);
                            System.out.println(sqlInsertProfit);
                            insertProfit.executeUpdate();
                            System.out.println("Profit");

                        }

                    }
                }
            }




        }

        public static double getPrecisePrice (String price){
            if (price==null){
                price="0.00";
            }
            price = price.replaceAll(",", ".");
            price = price.replaceAll(" ", "");
            double priceAsDouble = Double.parseDouble(price);
            return priceAsDouble;
        }
    }


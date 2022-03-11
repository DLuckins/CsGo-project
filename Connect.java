package com.company;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Connect {
    public static List<String> rarities =new ArrayList<>(Arrays.asList("Consumer Grade", "Industrial Grade", "Mil-Spec Grade", "Restricted", "Classified", "Covert"));
    public static List<String> conditions = new ArrayList<>(Arrays.asList("Battle-Scarred", "Well-Worn", "Field-Tested", "Minimal Wear", "Factory New"));


    public static void main(String[] args) {
        {
            try {

                Connection connection;
                String jbcUrl = "jdbc:sqlite:/C:\\sqlite-tools-win32-x86-3380000\\FinalSkinsDbGood.db";
                connection = DriverManager.getConnection(jbcUrl);
                Statement statement = connection.createStatement();
                String sql = "SELECT * FROM Skins";
                ResultSet result = statement.executeQuery(sql);
                //Runs for each item in DB
                while (result.next()) {
                    UpdateNextTierPrice(connection, result);
                }
                //needs to run a second time, since first it updated the prices for tiers.
                sql = "SELECT * FROM Skins";
                result = statement.executeQuery(sql);
                while (result.next()) {
                    UpdateValueAddedAndTaken(connection, result);
                }
                //Responsible for storing profitable tradeups
               CheckIfProfit(connection);

            } catch (SQLException e) {
                System.out.println("Error connecting to SQLite database");
                e.printStackTrace();
            }
        }
    }


    public static void UpdateNextTierPrice(Connection connection, ResultSet result) throws SQLException {

        while (result.next()) {
            //All the updating is done within each function

            String collection = result.getString("Collection");
            String rarity = result.getString("Rarity");
            String condition = result.getString("Condition");
            NextTierPrice(collection, rarity, connection, condition, result);
            NextSameTierPrice(collection, rarity, connection, condition, result);


        }
    }

    public static void UpdateValueAddedAndTaken(Connection connection, ResultSet result) throws SQLException{
        ValueAdded(result, connection);
        ValueTaken(result, connection);
    }


    public static void NextSameTierPrice(String collection, String rarity, Connection connection, String condition, ResultSet result) throws SQLException {
        Statement statements = connection.createStatement();
        //Aint a higher tier than Covert, no need to calculate
        if (!Objects.equals(rarity, "Covert")) {

            int raritiesNum = rarities.indexOf(rarity) + 1;
            String sqlForSameTier = "SELECT * FROM Skins WHERE Collection==\"" + collection + "\" AND Condition==\""+ condition+"\" AND Rarity==\"" + rarities.get(raritiesNum) + "\"";
            ResultSet resultsForSameTier = statements.executeQuery(sqlForSameTier);
            double allNextSameTierPrice = 0;

            while (resultsForSameTier.next()) {
                double priceForSameTier = 0;
                int isObtainable = resultsForSameTier.getInt("IsObtainable");
                if (isObtainable == 0 ) {

                    Statement statementIfNotObtainable = connection.createStatement();
                    String name = resultsForSameTier.getString("Name");
                    String tier = resultsForSameTier.getString("Condition");

                    //If Battle-Scarred, no tier less than it, so out of bounds.
                    if ((!Objects.equals(tier, "Battle-Scarred"))){
                        String sqlIsObtainable = "SELECT * FROM Skins WHERE Collection==\"" + collection + "\" AND Condition==\"" + conditions.get(conditions.indexOf(tier) - 1) + "\" AND Rarity==\"" + rarities.get(raritiesNum) + "\" AND Name == \"" + name + "\"";
                        ResultSet resultIsObtainable = statementIfNotObtainable.executeQuery(sqlIsObtainable);

                        if (resultIsObtainable.isClosed()) {
                            priceForSameTier += 0;
                        } else if (resultIsObtainable.getInt("IsObtainable") == 0){
                            priceForSameTier += 0;
                        } else {
                            priceForSameTier = GetPrecisePrice(resultIsObtainable.getString("Price"));
                        }
                            resultIsObtainable.close();
                        } else {
                            priceForSameTier +=0;
                        }

                } else{

                    priceForSameTier = GetPrecisePrice(resultsForSameTier.getString("Price"));
                }
                allNextSameTierPrice+=priceForSameTier;

            }

            int id = result.getInt("Id");
            String sqlUpdateSamePrice = "UPDATE Skins SET NextTierPriceForSameCondition = ? WHERE Id == "+id;
            PreparedStatement pstmtHowManyInNextSameTier = connection.prepareStatement(sqlUpdateSamePrice);
            int howManyInNextTier = result.getInt("HowManyInNextTier");
            pstmtHowManyInNextSameTier.setDouble(1, allNextSameTierPrice/howManyInNextTier);
            pstmtHowManyInNextSameTier.executeUpdate();

        }
    }



        public static void NextTierPrice(String collection, String rarity, Connection connection, String condition, ResultSet result) throws SQLException {
            Statement statements = connection.createStatement();

            int raritiesNum;
            //No tier higher than Covert, no need to calculate
            if (!Objects.equals(rarity, "Covert")) {
                String tempCondition = GetWearThatIsTwoConditionsUp(condition);

                raritiesNum = rarities.indexOf(rarity) + 1; //Gets a rarity that is one tier up Mil-spec -> Classified etc.

                String sql = "SELECT * FROM Skins WHERE Collection==\"" + collection + "\" AND Condition==\"" + tempCondition + "\" AND Rarity==\"" + rarities.get(raritiesNum) + "\"";
                ResultSet results = statements.executeQuery(sql);

                double allNextTierPrice = 0;

                while (results.next()) {
                    int isObtainable = results.getInt("IsObtainable");
                    double price = 0;
                    if (isObtainable == 0) {

                        Statement statementIfNotObtainable = connection.createStatement();
                        String name = results.getString("Name");
                        String sqlisObtainable = "SELECT * FROM Skins WHERE Collection==\"" + collection + "\" AND Condition==\"" + conditions.get(conditions.indexOf(tempCondition) - 1) + "\" AND Rarity==\"" + rarities.get(raritiesNum) + "\" AND Name == \"" + name + "\"";
                        ResultSet resultIsObtainable = statementIfNotObtainable.executeQuery(sqlisObtainable);

                        if (resultIsObtainable.isClosed()) {
                            price += 0;
                        } else if (resultIsObtainable.getInt("IsObtainable") == 0) {
                            price += 0;
                        } else {
                            price = GetPrecisePrice(resultIsObtainable.getString("Price"));
                        }
                        resultIsObtainable.close();

                    } else {
                        price = GetPrecisePrice(results.getString("Price"));
                    }

                    allNextTierPrice += price;

                }

                //Disabled, Before enabling, look at the function
                /// UpdateNextTierSkinCount(connection, result, collection, raritiesNum);

                int howManyInNextTier = result.getInt("HowManyInNextTier");
                int id = result.getInt("Id");

                String sqlUpdateNextPrice = "UPDATE Skins SET NextTierPrice = ? WHERE Id == "+id;
                double nextPrice = allNextTierPrice / howManyInNextTier;
                PreparedStatement pstmtNextPrice = connection.prepareStatement(sqlUpdateNextPrice);
                pstmtNextPrice.setDouble(1, nextPrice);
                pstmtNextPrice.executeUpdate();

            }
        }


        public static void ValueAdded(ResultSet result, Connection connection) throws SQLException {
            int id = result.getInt("Id");
            String sqlUpdateValueAdded = "UPDATE Skins SET ValueAdded = ? WHERE Id == "+id;
            double nextPrice = GetPrecisePrice(result.getString("NextTierPrice"));

            double price = GetPrecisePrice(result.getString("Price"));
            int howManyInNextTier = result.getInt("HowManyInNextTier");

            double valueAdded = ((nextPrice/10) - price)*howManyInNextTier;

            PreparedStatement pstmtValueAdded = connection.prepareStatement(sqlUpdateValueAdded);
            pstmtValueAdded.setDouble(1, valueAdded);
            pstmtValueAdded.executeUpdate();

        }


        public static void ValueTaken (ResultSet result, Connection connection) throws SQLException {
            int id = result.getInt("Id");
            String sqlUpdateValueTaken = "UPDATE Skins SET ValueTaken = ? WHERE Id == "+id;

            double nextPrice = GetPrecisePrice(result.getString("NextTierPriceForSameCondition"));
            double price = GetPrecisePrice(result.getString("Price"));
            int howManyInNextTier = result.getInt("HowManyInNextTier");

            double valueTaken = (price - ((nextPrice)/10))*howManyInNextTier;

            PreparedStatement pstmtValueTaken = connection.prepareStatement(sqlUpdateValueTaken);
            pstmtValueTaken.setDouble(1, valueTaken);
            pstmtValueTaken.executeUpdate();

        }


        public static void CheckIfProfit(Connection connection) throws SQLException {
            Statement statement = connection.createStatement();

            //For Now Value Added is topped out to lessen the false positives that are made because of false data
            String getGoodAdders = "SELECT * FROM Skins WHERE Rarity!= \"Covert\" AND ValueAdded > 0 AND ValueAdded < 85";

            String deleteOld = "DELETE FROM ProfitableTradeUps";//With New Data, always cleanup is needed
            PreparedStatement pstmtDelete = connection.prepareStatement(deleteOld);
            pstmtDelete.executeUpdate();

            ResultSet results = statement.executeQuery(getGoodAdders);
            while (results.next()) {

                double goodPrice = GetPrecisePrice(results.getString("Price"));
                double goodNextPrice =   GetPrecisePrice(results.getString("NextTierPrice"));

                //Is divided by 4 since ValueTaken is calculated for one result not 8 and ValueAdded calculated for one, helps to filter out data easier
                double valueAdded = GetPrecisePrice(results.getString("ValueAdded"))/4;
                int howManyInNextTierGood = results.getInt("HowManyInNextTier");

                String goodRarity = results.getString("Rarity");
                String goodCondition = results.getString("Condition");
                String tempCondition = GetWearThatIsTwoConditionsUp(goodCondition);

                Statement statements = connection.createStatement();
                //Value taken ValueTaken > -0.95 to filter out false positives
                String getFill = "SELECT * FROM Skins WHERE Rarity ==\""+goodRarity+"\" AND Condition == \"" + tempCondition + "\" AND HowManyInNextTier <= \"" + howManyInNextTierGood + "\" AND ValueTaken <\"" + valueAdded+"\" AND ValueTaken > -0.95 AND ValueTaken != 0";
                ResultSet resultFill = statements.executeQuery(getFill);

                while (resultFill.next()) {

                    int fillHowManyInTier = resultFill.getInt("HowManyInNextTier");
                    double fillPrice = GetPrecisePrice(resultFill.getString("Price"));
                    double fillNextTierPrice = GetPrecisePrice(resultFill.getString("NextTierPriceForSameCondition"));

                    if (fillHowManyInTier != 0 && howManyInNextTierGood != 0 ) {
                        String sqlInsertProfit = "INSERT INTO ProfitableTradeUps(NameOfValue, Collection, Wear, Price, HowManyToPutIn, NameOfFiller, CollectionOfFiller, FillerWear, FillerPrice, HowManyFillersToPut, Cost, ROI, VolumeForMostExpensive  ,MostExpensiveForGood, MostExpensiveForFiller) VALUES(?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        int allOutcomes = fillHowManyInTier*8 + howManyInNextTierGood*2;
                        double oneToEightOutcome = (float) (((float)(howManyInNextTierGood * 2) / allOutcomes) * goodNextPrice + ((float)(fillHowManyInTier * 8) / allOutcomes) * fillNextTierPrice);
                        double oneToEightPrice = Math.round (((float) (goodPrice * 2 + fillPrice * 8)) * 100.0) / 100.0;
                        double ROIforOneToEight =Math.round ((((oneToEightOutcome - oneToEightPrice) / oneToEightPrice)*100 +100)*100.0) / 100.0;
                        double positiveOutcome = (GetPrecisePrice(results.getString("ValueAdded"))*2 - GetPrecisePrice(resultFill.getString("ValueTaken"))*8);

                        //This check could possibly be taken out and only The ROI should be left. Will leave it for now just in case.
                        //If Positive outcome would be less than 0, it should automatically lose money.
                        if ((positiveOutcome > 0) && ROIforOneToEight > 105) {

                            //Still yet to be implemented
//                            Statement statemetMaxFiller = connection.createStatement();
//                            Statement statemetMaxGood = connection.createStatement();
//
//                            String getFillMax = "SELECT * FROM Skins WHERE Rarity ==\""+goodRarity+"\" AND Condition == \"" + tempCondition + "\" AND HowManyInNextTier <= \"" + howManyInNextTierGood + "\" AND ValueTaken <\"" + valueAdded+"\" AND ValueTaken > -0.4 AND ValueTaken != 0";
//                            String getGoodFill = "SELECT * FROM Skins WHERE Rarity ==\""+goodRarity+"\" AND Condition == \"" + tempCondition + "\" AND HowManyInNextTier <= \"" + howManyInNextTierGood + "\" AND ValueTaken <\"" + valueAdded+"\" AND ValueTaken > -0.4 AND ValueTaken != 0";
//Glock-18 | Pink DDPAT	The 2021 Mirage Collection	Field-Tested	0.11	2	P2000 | Amber Fade	The Dust 2 Collection	Factory New	0.39	8	3.33999991416931	2080.73876953125			114.0

                            PreparedStatement insertProfit = connection.prepareStatement(sqlInsertProfit);
                            insertProfit.setString(1, results.getString("name"));
                            insertProfit.setString(2, results.getString("Collection"));
                            insertProfit.setString(3, results.getString("Condition"));
                            insertProfit.setDouble(4, goodPrice);
                            insertProfit.setInt(5, 2);
                            insertProfit.setString(6, resultFill.getString("name"));
                            insertProfit.setString(7, resultFill.getString("Collection"));
                            insertProfit.setString(8, resultFill.getString("Condition"));
                            insertProfit.setDouble(9, fillPrice);
                            insertProfit.setInt(10, 8);
                            insertProfit.setDouble(11, oneToEightPrice);
                            insertProfit.setDouble(12, ROIforOneToEight);
                            insertProfit.setDouble(13, MostExpensiveItemsVolume(resultFill.getString("Collection"), results.getString("Collection"), tempCondition, rarities.get(rarities.indexOf(goodRarity)+1), connection));
                            //Still yet to be implemented
                            //insertProfit.setFloat(12, ROIforOneToEight);//Ads most expensive from next tier
                            //insertProfit.setFloat(12, ROIforOneToEight);//ads most expensive for filler next tier
                            insertProfit.executeUpdate();

                        }

                    }
                }
            }
        }


        //When getting values from DB as floats, they are not correct, that's why we need to get them as string and convert them to be precise.
        public static double GetPrecisePrice (String price){
            if (price==null){
                price="0.00";
            }
            price = price.replaceAll(",", ".");
            price = price.replaceAll(" ", "");
            return Double.parseDouble(price);
        }


        public static String GetWearThatIsTwoConditionsUp(String wear){
        String tempCondition;
            if (Objects.equals(wear, "Battle-Scarred")){
                tempCondition = "Field-Tested";
            } else if (Objects.equals(wear, "Well-Worn")){
                tempCondition = "Minimal Wear";
            } else{
                tempCondition = "Factory New";
            }
            return tempCondition;
        }


        //Function is disabled for now, if HowManyInTier for some reason becomes empty for any fields, Enable this function
        public static void UpdateNextTierSkinCount(Connection connection, ResultSet result, String collection, int raritiesNum) throws SQLException {
                String sqlHowManyInTier = "SELECT DISTINCT Name FROM Skins WHERE Collection==\"" + collection +"\" AND Rarity==\"" + rarities.get(raritiesNum) + "\"";
                Statement asa = connection.createStatement();
                ResultSet resultsHowManyInTier = asa.executeQuery(sqlHowManyInTier);
                int howManyInNextTier = 0;
                while (resultsHowManyInTier.next()) {
                    howManyInNextTier++;
                }

                int id = result.getInt("Id");
                String sqlUpdateHowManyInTier = "UPDATE Skins SET HowManyInNextTier = ? WHERE Id == "+id;
                PreparedStatement pstmtHowManyInNextTier = connection.prepareStatement(sqlUpdateHowManyInTier);
                pstmtHowManyInNextTier.setInt(1, howManyInNextTier);
                pstmtHowManyInNextTier.executeUpdate();
        }


        public static double MostExpensiveItemsVolume (String collectionFiller, String collectionGainer, String wear, String nextTier, Connection connection) throws SQLException {
            String getFillersSql = "SELECT * FROM Skins WHERE collection == \"" + collectionFiller + "\" AND Rarity == \"" + nextTier + "\" AND Condition == \"" + wear + "\"";
            String getGainersSql = "SELECT * FROM Skins WHERE collection == \"" + collectionGainer + "\" AND Rarity == \"" + nextTier + "\" AND Condition == \"" + wear + "\"";
            Statement filler = connection.createStatement();
            Statement gainer = connection.createStatement();
            ResultSet fillerResult = filler.executeQuery(getFillersSql);
            ResultSet gainerResult = gainer.executeQuery(getGainersSql);
            double fillerMaxPrice = 0;
            String fillerName = null;
            while (fillerResult.next()){
                if(!fillerResult.isClosed()) {
                    double fillerPrice = GetPrecisePrice(fillerResult.getString("Price"));
                    if (fillerMaxPrice < fillerPrice && fillerResult.getInt("isObtainable") == 1) {
                        fillerMaxPrice = fillerPrice;
                        fillerName = fillerResult.getString("Name");
                    }
                }
            }
            String gainerName = null;
            double gainerMaxPrice = 0;
            while (gainerResult.next()){
                if(!gainerResult.isClosed()) {
                    double gainerPrice = GetPrecisePrice(gainerResult.getString("Price"));
                    if (gainerMaxPrice < gainerPrice && gainerResult.getInt("isObtainable") == 1) {
                        gainerMaxPrice = gainerPrice;
                        gainerName = gainerResult.getString("Name");
                    }
                }
            }
            String maxName = gainerMaxPrice >= fillerMaxPrice?  gainerName : fillerName;
            String getMaxSql = "SELECT * FROM Skins WHERE name == \"" + maxName + "\" AND Condition == \"" + wear + "\"";
            Statement maxSql = connection.createStatement();
            ResultSet maxResult = maxSql.executeQuery(getMaxSql);
            if(!maxResult.isClosed()) {
                //Isn't a price, but since we could be facing the same problem, we can use this
                double maxVolume = GetPrecisePrice(maxResult.getString("VolumeFor30Days"));
                return maxVolume;
            } else return 0;
        }


    }



package com.company;

import java.sql.*;
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
            //String sqlUpdatePrice = "UPDATE AllSkins SET price_buy_orders=trim(price_buy_orders, \"€\") WHERE Id =="+id;

            String collection = result.getString("Collection");
            String rarity = result.getString("Rarity");
            String condition = result.getString("Condition");
            nextTierPrice(collection, rarity, connection, condition, result);
            nextSameTierPrice(collection, rarity, connection, condition, result);
            ValueTaken(result, connection);


        }
    }


    public static void nextSameTierPrice(String collection, String rarity, Connection connection, String condition, ResultSet result) throws SQLException {
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
                            priceForSameTier = getPrecisePrice(resultIsObtainable.getString("Price"));
                        }
                            resultIsObtainable.close();
                        } else {
                            priceForSameTier +=0;
                        }

                } else{

                    priceForSameTier = getPrecisePrice(resultsForSameTier.getString("Price"));
                }
                allNextSameTierPrice+=priceForSameTier;

            }

            int id = result.getInt("Id");
            String sqlUpdateSamePrice = "UPDATE Skins SET NextTierPriceForSameTier = ? WHERE Id == "+id;
            PreparedStatement pstmtHowManyInNextSameTier = connection.prepareStatement(sqlUpdateSamePrice);
            int howManyInNextTier = result.getInt("HowManyInNextTier");
            pstmtHowManyInNextSameTier.setDouble(1, allNextSameTierPrice/howManyInNextTier);
            pstmtHowManyInNextSameTier.executeUpdate();

        }
    }



        public static void nextTierPrice(String collection, String rarity, Connection connection, String condition, ResultSet result) throws SQLException {
            Statement statements = connection.createStatement();

            int raritiesNum;
            if (!Objects.equals(rarity, "Covert")) {
                String tempCondition = getWearThatIsTwoTearsUp(condition);

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
                            price = getPrecisePrice(resultIsObtainable.getString("Price"));
                        }
                        resultIsObtainable.close();

                    } else {
                        price = getPrecisePrice(results.getString("Price"));
                    }

                    allNextTierPrice += price;

                }

                //Disabled, Before enabling, look at the function
                /// updateNextTierSkinCount(connection, result, collection, raritiesNum);

                int howManyInNextTier = result.getInt("HowManyInNextTier");
                int id = result.getInt("Id");

                String sqlUpdateNextPrice = "UPDATE Skins SET NextTierPrice = ? WHERE Id == "+id;
                double nextPrice = allNextTierPrice / howManyInNextTier;
                PreparedStatement pstmtNextPrice = connection.prepareStatement(sqlUpdateNextPrice);
                pstmtNextPrice.setDouble(1, nextPrice);
                pstmtNextPrice.executeUpdate();

            }
        }


        private static void ValueAdded(ResultSet result, Connection connection) throws SQLException {
            int id = result.getInt("Id");
            String sqlUpdateValueAdded = "UPDATE Skins SET ValueAdded = ? WHERE Id == "+id;
            double nextPrice = getPrecisePrice(result.getString("NextTierPrice"));

            double price = getPrecisePrice(result.getString("Price"));
            int howManyInNextTier = result.getInt("HowManyInNextTier");

            double valueAdded = ((nextPrice/10) - price)*howManyInNextTier;

            PreparedStatement pstmtValueAdded = connection.prepareStatement(sqlUpdateValueAdded);
            pstmtValueAdded.setDouble(1, valueAdded);
            pstmtValueAdded.executeUpdate();

        }


        private static void ValueTaken (ResultSet result, Connection connection) throws SQLException {
            int id = result.getInt("Id");
            String sqlUpdateValueTaken = "UPDATE Skins SET ValueTaken = ? WHERE Id == "+id;

            double nextPrice = getPrecisePrice(result.getString("NextTierPriceForSameTier"));
            double price = getPrecisePrice(result.getString("Price"));
            int howManyInNextTier = result.getInt("HowManyInNextTier");

            double valueTaken = (price - ((nextPrice)/10))*howManyInNextTier;

            PreparedStatement pstmtValueTaken = connection.prepareStatement(sqlUpdateValueTaken);
            pstmtValueTaken.setDouble(1, valueTaken);
            pstmtValueTaken.executeUpdate();

        }


        private static void checkIfProfit(Connection connection) throws SQLException {
            Statement statement = connection.createStatement();

            //For Now Value Added is topped out to lessen the false positives that are made because of false data
            String getGoodAdders = "SELECT * FROM Skins WHERE Rarity!= \"Covert\" AND ValueAdded > 0 AND ValueAdded < 85";

            String deleteOld = "DELETE FROM ProfitableTradeUps";//With New Data, always cleanup is needed
            PreparedStatement pstmtDelete = connection.prepareStatement(deleteOld);
            pstmtDelete.executeUpdate();

            ResultSet results = statement.executeQuery(getGoodAdders);
            while (results.next()) {

                double goodPrice = getPrecisePrice(results.getString("Price"));
                double goodNextPrice =   getPrecisePrice(results.getString("NextTierPrice"));

                //Is divided by 4 since ValueTaken is calculated for one result not 8 and ValueAdded calculated for one, helps to filter out data easier
                double valueAdded = getPrecisePrice(results.getString("ValueAdded"))/4;
                int howManyInNextTierGood = results.getInt("HowManyInNextTier");

                String goodRarity = results.getString("Rarity");
                String goodCondition = results.getString("Condition");
                String tempCondition = getWearThatIsTwoTearsUp(goodCondition);

                Statement statements = connection.createStatement();
                //Value taken ValueTaken > -0.4 to filter out false positives
                String getFill = "SELECT * FROM Skins WHERE Rarity ==\""+goodRarity+"\" AND Condition == \"" + tempCondition + "\" AND HowManyInNextTier <= \"" + howManyInNextTierGood + "\" AND ValueTaken <\"" + valueAdded+"\" AND ValueTaken > -0.4 AND ValueTaken != 0";
                ResultSet resultFill = statements.executeQuery(getFill);

                while (resultFill.next()) {

                    int fillHowManyInTier = resultFill.getInt("HowManyInNextTier");
                    double fillPrice = getPrecisePrice(resultFill.getString("Price"));
                    double fillNextTierPrice = getPrecisePrice(resultFill.getString("NextTierPriceForSameTier"));

                    if (fillHowManyInTier != 0 && howManyInNextTierGood != 0 ) {
                        String sqlInsertProfit = "INSERT INTO ProfitableTradeUps(NameOfValue, Collection, Wear, Price, HowManyToPutIn, NameOfFiller, CollectionOfFiller, FillerWear, FillerPrice, HowManyFillersToPut, Cost, ROI, MostExpensiveForGood, MostExpensiveForFiller) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        int allOutcomes = fillHowManyInTier*8 + howManyInNextTierGood*2;
                        float oneToEightOutcome = (float) (((float)(howManyInNextTierGood * 2) / allOutcomes) * goodNextPrice + ((float)(fillHowManyInTier * 8) / allOutcomes) * fillNextTierPrice);
                        float oneToEightPrice = (float) (goodPrice * 2 + fillPrice * 8);
                        float ROIforOneToEight = (oneToEightOutcome - oneToEightPrice) / oneToEightPrice;
                        double positiveOutcome = (getPrecisePrice(results.getString("ValueAdded"))*2 - getPrecisePrice(resultFill.getString("ValueTaken"))*8);

                        //This check could possibly be taken out and only The ROI should be left. Will leave it for now just in case.
                        //If Positive outcome would be less than 0, it should automatically lose money.
                        if ((positiveOutcome > 0) && ROIforOneToEight > 0.05) {

                            //Still yet to be implemented
//                            Statement statemetMaxFiller = connection.createStatement();
//                            Statement statemetMaxGood = connection.createStatement();
//
//                            String getFillMax = "SELECT * FROM Skins WHERE Rarity ==\""+goodRarity+"\" AND Condition == \"" + tempCondition + "\" AND HowManyInNextTier <= \"" + howManyInNextTierGood + "\" AND ValueTaken <\"" + valueAdded+"\" AND ValueTaken > -0.4 AND ValueTaken != 0";
//                            String getGoodFill = "SELECT * FROM Skins WHERE Rarity ==\""+goodRarity+"\" AND Condition == \"" + tempCondition + "\" AND HowManyInNextTier <= \"" + howManyInNextTierGood + "\" AND ValueTaken <\"" + valueAdded+"\" AND ValueTaken > -0.4 AND ValueTaken != 0";


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
                            insertProfit.setFloat(12, ROIforOneToEight);
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
        public static double getPrecisePrice (String price){
            if (price==null){
                price="0.00";
            }
            price = price.replaceAll(",", ".");
            price = price.replaceAll(" ", "");
            return Double.parseDouble(price);
        }


        public static String getWearThatIsTwoTearsUp(String wear){
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
        public static void updateNextTierSkinCount(Connection connection, ResultSet result, String collection, int raritiesNum) throws SQLException {
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


//        public static double calculateROI(int allOutcomes, int howManyToPutIn, )


    }



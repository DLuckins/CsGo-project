package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Condition;

public class Connect {
    private static String jbcUrl = "jdbc:sqlite:/C:\\sqlite-tools-win32-x86-3380000\\Skins.db";
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

        List<String> collections = new ArrayList<>(Arrays.asList("The Dreams Nightmares Collection" ,
                "The Operation Riptide Collection", "The 2021 Train Collection", "The 2021 Mirage Collection",
                "The 2021 Dust 2 Collection", "The 2021 Vertigo Collection", "The Snakebite Collection",
                "The Operation Broken Fang Collection", "The Control Collection", "The Havoc Collection",
                "The Ancient Collection", "The Fracture Collection", "The Prisma 2 Collection", "The Canals Collection",
                "The Norse Collection", "The Shattered Web Collection", "The St. Marc Collection", "The CS20 Collection",
                "The X-Ray Collection", "The Prisma Collection", "The Clutch Collection", "The Blacksite Collection",
                "The Danger Zone Collection", "The 2018 Inferno Collection", "The 2018 Nuke Collection",
                "The Horizon Collection", "The Spectrum 2 Collection", "Operation Hydra", "The Spectrum Collection",
                "The Glove Collection", "The Gamma 2 Collection", "The Gamma Collection", "The Chroma 3 Collection",
                "The Wildfire Collection", "The Revolver Case Collection", "The Shadow Collection",
                "The Chop Shop Collection", "The Falchion Collection", "The Gods and Monsters Collection",
                "The Rising Sun Collection", "The Chroma 2 Collection", "The Chroma Collection", "The Vanguard Collection",
                "The Cache Collection", "The eSports 2014 Summer Collection", "The Baggage Collection",
                "The Breakout Collection", "The Cobblestone Collection", "The Overpass Collection", "The Bank Collection",
                "The Huntsman Collection", "The Phoenix Collection", "The Arms Deal 3 Collection",
                "The eSports 2013 Winter Collection", "The Winter Offensive Collection", "The Dust 2 Collection",
                "The Italy Collection", "The Lake Collection", "The Mirage Collection", "The Safehouse Collection",
                "The Train Collection", "The Arms Deal 2 Collection", "The Alpha Collection", "The Bravo Collection",
                "The Arms Deal Collection", "The Assault Collection", "The Aztec Collection", "The Dust Collection",
                "The eSports 2013 Collection", "The Inferno Collection", "The Militia Collection", "The Nuke Collection",
                "The Office Collection"));
        List<String> rarities =new ArrayList<>(Arrays.asList("Consumer", "Industrial", "Mil-Spec", "Restricted", "Classified", "Covert"));
        List<String> conditions = new ArrayList<>(Arrays.asList("Exterior: Battle-Scarred", "Exterior: Well-Worn", "Exterior: Field-Tested", "Exterior: Minimal Wear", "Exterior: Factory new"));
        int raritiesNum = 0;
        if (!Objects.equals(rarity, "Covert")) {
            raritiesNum = rarities.indexOf(rarity) + 1;
            String sqlForSameTier = "SELECT * FROM Skins WHERE Collection==\"" + collection + "\" AND Condition==\""+ result.getString("Condition") +"\" AND Rarity==\"" + rarities.get(raritiesNum) + "\"";
            ResultSet resultsForSameTier = statements.executeQuery(sqlForSameTier);
            double allNextTierPrice = 0.0000;
            double howManyInTier = 0.000;
            double allNextSameTierPrice = 0;
            while (resultsForSameTier.next()) {

                String priceInStringForSameTier=resultsForSameTier.getString("Price");
                priceInStringForSameTier =priceInStringForSameTier.replaceAll(",",".");
                priceInStringForSameTier =priceInStringForSameTier.replaceAll(" ","");
                double priceForSameTier = Double.parseDouble(priceInStringForSameTier);
                allNextSameTierPrice+=priceForSameTier;

                howManyInTier+=1.00000;
            }

            int id = result.getInt("Id");
            String sqlUpdateSamePrice = "UPDATE Skins SET NextTierPriceForSameTier = ? WHERE Id == "+id;
            PreparedStatement pstmtHowManyInNextSameTier = connection.prepareStatement(sqlUpdateSamePrice);
            pstmtHowManyInNextSameTier.setDouble(1, allNextSameTierPrice/howManyInTier);
            pstmtHowManyInNextSameTier.executeUpdate();

            System.out.println(allNextTierPrice);
            System.out.println(howManyInTier);
            System.out.println(allNextTierPrice / howManyInTier);


            double ret =allNextTierPrice / howManyInTier;
            return ret;
        }
        else return 0;
    }



        public static double nextTierPrice(String collection, String rarity, Connection connection, String condition, ResultSet result) throws SQLException {

            Statement statements = connection.createStatement();

            List<String> collections = new ArrayList<>(Arrays.asList("The Dreams Nightmares Collection" ,
                    "The Operation Riptide Collection", "The 2021 Train Collection", "The 2021 Mirage Collection",
                    "The 2021 Dust 2 Collection", "The 2021 Vertigo Collection", "The Snakebite Collection",
                    "The Operation Broken Fang Collection", "The Control Collection", "The Havoc Collection",
                    "The Ancient Collection", "The Fracture Collection", "The Prisma 2 Collection", "The Canals Collection",
                    "The Norse Collection", "The Shattered Web Collection", "The St. Marc Collection", "The CS20 Collection",
                    "The X-Ray Collection", "The Prisma Collection", "The Clutch Collection", "The Blacksite Collection",
                    "The Danger Zone Collection", "The 2018 Inferno Collection", "The 2018 Nuke Collection",
                    "The Horizon Collection", "The Spectrum 2 Collection", "Operation Hydra", "The Spectrum Collection",
                    "The Glove Collection", "The Gamma 2 Collection", "The Gamma Collection", "The Chroma 3 Collection",
                    "The Wildfire Collection", "The Revolver Case Collection", "The Shadow Collection",
                    "The Chop Shop Collection", "The Falchion Collection", "The Gods and Monsters Collection",
                    "The Rising Sun Collection", "The Chroma 2 Collection", "The Chroma Collection", "The Vanguard Collection",
                    "The Cache Collection", "The eSports 2014 Summer Collection", "The Baggage Collection",
                    "The Breakout Collection", "The Cobblestone Collection", "The Overpass Collection", "The Bank Collection",
                    "The Huntsman Collection", "The Phoenix Collection", "The Arms Deal 3 Collection",
                    "The eSports 2013 Winter Collection", "The Winter Offensive Collection", "The Dust 2 Collection",
                    "The Italy Collection", "The Lake Collection", "The Mirage Collection", "The Safehouse Collection",
                    "The Train Collection", "The Arms Deal 2 Collection", "The Alpha Collection", "The Bravo Collection",
                    "The Arms Deal Collection", "The Assault Collection", "The Aztec Collection", "The Dust Collection",
                    "The eSports 2013 Collection", "The Inferno Collection", "The Militia Collection", "The Nuke Collection",
                    "The Office Collection"));
            List<String> rarities =new ArrayList<>(Arrays.asList("Consumer", "Industrial", "Mil-Spec", "Restricted", "Classified", "Covert"));
            List<String> conditions = new ArrayList<>(Arrays.asList("Exterior: Battle-Scarred", "Exterior: Well-Worn", "Exterior: Field-Tested", "Exterior: Minimal Wear", "Exterior: Factory new"));
            int raritiesNum = 0;
            String tempCondition;
            if (!Objects.equals(rarity, "Covert")) {
                if (Objects.equals(condition, "Exterior: Battle-Scarred")){
                    tempCondition = "Exterior: Field-Tested";
                } else if (Objects.equals(condition, "Exterior: Well-Worn")){
                    tempCondition = "Exterior: Minimal Wear";
                } else{
                    tempCondition = "Exterior: Factory New";
                }

                raritiesNum = rarities.indexOf(rarity) + 1;
                String sql = "SELECT * FROM Skins WHERE Collection==\"" + collection + "\" AND Condition==\""+ tempCondition +"\" AND Rarity==\"" + rarities.get(raritiesNum) + "\"";
                ResultSet results = statements.executeQuery(sql);

                double allNextTierPrice = 0.0000;
                double howManyInTier = 0.000;
                double allNextSameTierPrice = 0;
                while (results.next()) {
                    String priceInString=results.getString("Price");
                    priceInString =priceInString.replaceAll(",",".");
                    priceInString =priceInString.replaceAll(" ","");
                    double price = Double.parseDouble(priceInString);



                    allNextTierPrice += price;
                    howManyInTier+=1.00000;
                }

                int id = result.getInt("Id");
                String sqlUpdateHowManyInTier = "UPDATE Skins SET HowManyInNextTier = ? WHERE Id == "+id;
                PreparedStatement pstmtHowManyInNextTier = connection.prepareStatement(sqlUpdateHowManyInTier);
                pstmtHowManyInNextTier.setDouble(1, howManyInTier);
                pstmtHowManyInNextTier.executeUpdate();

                System.out.println(allNextTierPrice);
                System.out.println(howManyInTier);
                System.out.println(allNextTierPrice / howManyInTier);


                double ret =allNextTierPrice / howManyInTier;
                return ret;
            }
            else return 0;
        }

        private static double ValueAdded(Connection connection, ResultSet result) throws SQLException {
            Statement statements = connection.createStatement();
            float nextPrice = result.getFloat("NextTierPrice");
            String priceInString=result.getString("Price");
            priceInString =priceInString.replaceAll(",",".");
            priceInString =priceInString.replaceAll(" ","");
            double price = Double.parseDouble(priceInString);
            double valueAdded = (nextPrice/10f) - price;
            return valueAdded;
        }

        private static double ValueTaken (Connection connection, ResultSet result) throws SQLException {
            Statement statements = connection.createStatement();
            float nextPrice = result.getFloat("NextTierPrice");
            String priceInString=result.getString("Price");
            priceInString =priceInString.replaceAll(",",".");
            priceInString =priceInString.replaceAll(" ","");
            double price = Double.parseDouble(priceInString);
            double valueTaken = (price*9f) - ((nextPrice*9f)/10f);
            return valueTaken;
        }





        private static void checkIfProfit(Connection connection) throws SQLException {
            Statement statement = connection.createStatement();
            String getGoodAdders = "SELECT * FROM Skins WHERE Rarity!= \"Covert\"";
            ResultSet results = statement.executeQuery(getGoodAdders);
            int i = 0;
            while (results.next()) {
                i++;
                String priceInStringGood=null;
                priceInStringGood=results.getString("Price");
                priceInStringGood =priceInStringGood.replaceAll(",",".");
                priceInStringGood =priceInStringGood.replaceAll(" ","");
                double goodPrice = Double.parseDouble(priceInStringGood);

                float goodNextPrice = results.getFloat("NextTierPrice");
                int howManyInNextTierGood = results.getInt("HowManyInNextTier");
                String goodRarity = results.getString("Rarity");
                String goodCondition = results.getString("Condition");
                String tempCondition;
                if (Objects.equals(goodCondition, "Exterior: Battle-Scarred")){
                    tempCondition = "Exterior: Field-Tested";
                } else if (Objects.equals(goodCondition, "Exterior: Well-Worn")){
                    tempCondition = "Exterior: Minimal Wear";
                } else{
                    tempCondition = "Exterior: Factory New";
                }

                Statement statements = connection.createStatement();
                String getFill = "SELECT * FROM Skins WHERE Rarity ==\""+goodRarity+"\" AND Condition == \"" + tempCondition + "\"";
                ResultSet resultFill = statements.executeQuery(getFill);

                while (resultFill.next()) {

                    int fillHowManyInTier = resultFill.getInt("HowManyInNextTier");
                    String priceInString = resultFill.getString("Price");
                    priceInString = priceInString.replaceAll(",", ".");
                    priceInString = priceInString.replaceAll(" ", "");
                    double fillPrice = Double.parseDouble(priceInString);
                    System.out.println("Price is " + goodPrice + " And fill price is " + fillPrice + " Bucks");
                    double fillNextTierPrice = resultFill.getFloat("NextTierPriceForSameTier");
                    if (fillHowManyInTier != 0 && howManyInNextTierGood != 0 ) {
                        int allOutcomes = fillHowManyInTier + howManyInNextTierGood;
                        float oneToNineOutcome = (float) (((howManyInNextTierGood / allOutcomes) * goodNextPrice + ((fillHowManyInTier * 9) / allOutcomes) * fillNextTierPrice));
                        float oneToNinePrice = (float) (goodPrice + fillPrice * 9);
                        float ROIforOneToNine = (oneToNineOutcome - oneToNinePrice) / oneToNinePrice;
                        String sqlInsertProfit = "INSERT INTO ProfitableTradeUps(NameOfValue, Collection, Wear, Price, HowManyToPutIn, NameOfFiller, CollectionOfFiller, FillerWear, FillerPrice, HowManyFillersToPut, Cost, ROI) VALUES(?,?,?,?,?,?,?,?,?,?,?, ?)";
                        if (ROIforOneToNine > 0) {

                            PreparedStatement insertProfit;
                            insertProfit = connection.prepareStatement(sqlInsertProfit);
                            insertProfit.setString(1, results.getString("name"));
                            insertProfit.setString(2, results.getString("Collection"));
                            insertProfit.setString(3, results.getString("Condition"));
                            insertProfit.setFloat(4, 3);
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

                        float oneToEightOutcome = (float) (((howManyInNextTierGood * 2) / allOutcomes) * goodNextPrice + ((fillHowManyInTier * 8) / allOutcomes) * fillNextTierPrice);
                        float oneToEightPrice = (float) (goodPrice * 2 + fillPrice * 8);
                        float ROIforOneToEight = (oneToEightOutcome - oneToEightPrice) / oneToEightPrice;
                        System.out.println(results.getFloat("Price"));

                        if (ROIforOneToEight > 0) {

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
    }


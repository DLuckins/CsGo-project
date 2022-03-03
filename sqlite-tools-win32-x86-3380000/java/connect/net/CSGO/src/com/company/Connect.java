package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
            } catch (SQLException e) {
                System.out.println("Error connecting to SQLite database");
                e.printStackTrace();
            }
        }
    }
    public static void updateNextTierPrice(Connection connection, ResultSet result) throws SQLException {

      //  String sqlUpdate = "UPDATE Skins SET NextTierPrice = ? WHERE Id == "+id;

        while (result.next()) {
            int id = result.getInt("Id");

            String sqlUpdateNextPrice = "UPDATE Skins SET NextTierPrice = ? WHERE Id == "+id;

            String sqlUpdateValueAdded = "UPDATE Skins SET ValueAdded = ? WHERE Id == "+id;
            String sqlUpdateValueTaken = "UPDATE Skins SET ValueTaken = ? WHERE Id == "+id;
            String sqlUpdatePrice = "UPDATE AllSkins SET price_buy_orders=trim(price_buy_orders, \"€\") WHERE Id =="+id;

            String collection = result.getString("Collection");
            String rarity = result.getString("Rarity");

            double nextPrice = nextTierPrice(collection, rarity, connection);
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


        public static double nextTierPrice(String collection, String rarity, Connection connection) throws SQLException {

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
            List<String> conditions = new ArrayList<>(Arrays.asList("Battle-Scarred", "Well-Worn", "Field-Tested", "Minimal Wear", "Factory new"));
            int raritiesNum = 0;
            if (!Objects.equals(rarity, "Covert")) {
                raritiesNum = rarities.indexOf(rarity) + 1;
                System.out.println("Rarity passed is : " + rarity + " Next rarity is " + rarities.get(raritiesNum));
                String sql = "SELECT * FROM Skins WHERE Collection==\"" + collection + "\" AND Condition==\"Exterior: Factory New\" AND Rarity==\"" + rarities.get(raritiesNum) + "\"";
                ResultSet results = statements.executeQuery(sql);
                double allNextTierPrice = 0.0000;
                double howManyInTier = 0.000;
                while (results.next()) {
                    String priceInString=results.getString("Price");
                    priceInString =priceInString.replaceAll(",",".");
                    priceInString =priceInString.replaceAll(" ","");
                    double price = Double.parseDouble(priceInString);
                    allNextTierPrice += price;
                    howManyInTier+=1.00000;
                }

                System.out.println("All next price: " + allNextTierPrice + "   How many in tier : " +howManyInTier);
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
    }


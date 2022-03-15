package com.example.scenedemo;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.*;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectTest {
    Connection connection;
    @Before
    public void before(){
        try {

            String jbcUrl = "jdbc:sqlite:TestDB.db";
            connection = DriverManager.getConnection(jbcUrl);
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM Skins";
            ResultSet result = statement.executeQuery(sql);
            //Runs for each item in DB
            while (result.next()) {
                Connect.UpdateNextTierPrice(connection, result);
            }
            //needs to run a second time, since first it updated the prices for tiers.
            sql = "SELECT * FROM Skins";
            result = statement.executeQuery(sql);
            while (result.next()) {
                Connect.UpdateValueAddedAndTaken(connection, result);
            }
            //Responsible for storing profitable tradeups
            Connect.CheckIfProfit(connection);

        } catch (SQLException e) {
            System.out.println("Error connecting to SQLite database");
            e.printStackTrace();
        }
    }
    @After
    public void after() throws SQLException {
        connection.close();
    }


    @Test
    public void ValueAdded() throws SQLException {
        boolean isCorrect = true;
        Statement statementValueAdded = connection.createStatement();
        String sqlValueAdded = "SELECT * FROM Skins";
        ResultSet resultValueAdded = statementValueAdded.executeQuery(sqlValueAdded);
        while (resultValueAdded.next()){
            if (!Objects.equals(resultValueAdded.getString("ValueAdded"), resultValueAdded.getString("ExpectedValueAdded"))){
                isCorrect = false;
                System.out.println(resultValueAdded.getString("Name") + " with condition "+ resultValueAdded.getString("Condition") + " Is not right");
            }
        }
        assertTrue(isCorrect);
    }

    @Test
    public void ValueTaken() throws SQLException {
        boolean isCorrect = true;
        Statement statementValueTaken = connection.createStatement();
        String sqlValueTaken = "SELECT * FROM Skins";
        ResultSet resultValueTaken = statementValueTaken.executeQuery(sqlValueTaken);
        while (resultValueTaken.next()){
            if (!Objects.equals(resultValueTaken.getString("ValueTaken"), resultValueTaken.getString("ExpectedValueTaken"))){
                isCorrect = false;
                System.out.println(resultValueTaken.getString("Name") + " with condition "+ resultValueTaken.getString("Condition") + " Is not right");
            }
        }
        assertTrue(isCorrect);
    }

    @Test
    public void NextTierPrice() throws SQLException {
        boolean isCorrect = true;
        Statement statementNextTierPrice = connection.createStatement();
        String sqlNextTierPrice = "SELECT * FROM Skins";
        ResultSet resultNextTierPrice = statementNextTierPrice.executeQuery(sqlNextTierPrice);
        while (resultNextTierPrice.next()){
            if (!Objects.equals(resultNextTierPrice.getString("NextTierPrice"), resultNextTierPrice.getString("ExpectedNextTierPrice"))){
                isCorrect = false;
                System.out.println(resultNextTierPrice.getString("Name") + " with condition "+ resultNextTierPrice.getString("Condition") + " Is not right");
            }
        }
        assertTrue(isCorrect);
    }

    @Test
    public void NextSameTierPrice() throws SQLException {
        boolean isCorrect = true;
        Statement statementNextSameTierPrice = connection.createStatement();
        String sqlNextSameTierPrice = "SELECT * FROM Skins";
        ResultSet resultNextSameTierPrice = statementNextSameTierPrice.executeQuery(sqlNextSameTierPrice);
        while (resultNextSameTierPrice.next()){
            if (!Objects.equals(resultNextSameTierPrice.getString("NextTierPriceForSameCondition"), resultNextSameTierPrice.getString("ExpectedNextTierForSameCondition"))){
                isCorrect = false;
                System.out.println(resultNextSameTierPrice.getString("Name") + " with condition "+ resultNextSameTierPrice.getString("Condition") + " Is not right");
            }
        }
        assertTrue(isCorrect);
    }

    @Test
    public void CheckIfProfit() throws SQLException {
        boolean isCorrect = true;
        Statement statementNextSameTierPrice = connection.createStatement();
        String sqlNextSameTierPrice = "SELECT count(*) FROM ProfitableTradeUps;";
        ResultSet resultNextSameTierPrice = statementNextSameTierPrice.executeQuery(sqlNextSameTierPrice);
        int rows = resultNextSameTierPrice.getInt("count(*)");
        assertEquals(257, rows);
    }
}
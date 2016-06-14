package com.studenttheironyard;

import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by hoseasandstrom on 6/14/16.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }
    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        conn.close();
        assertTrue(user != null);
    }
    @Test
    public void testPunch() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        Main.insertPunch(conn, "Big Bird", "Donkey Punch", "I don't like bird poop!", 1);
        Punch pnch = Main.selectPunch(conn, 1);
        conn.close();
        assertTrue(pnch != null);
    }
    @Test
    public void testDeletePunch() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        Main.insertPunch(conn, "Big Bird", "Donkey Punch", "I don't like bird poop!", 1);
        Main.deletePunch(conn, 1);
        Punch pnch = Main.selectPunch(conn, 1);
        conn.close();
        assertTrue(pnch == null);
    }


}
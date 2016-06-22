package com.studenttheironyard;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {


    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users(userid IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS punches(punchid IDENTITY, punchname VARCHAR, punchstyle VARCHAR, punchcomment VARCHAR, user_id INT)");
    }

    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES(NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()){
        int id = results.getInt("id");
        String password = results.getString("password");
        return new User(name, password);
        }
        return null;
    }

    public static void insertPunch(Connection conn, String punchname, String punchstyle, String punchcomment, int  userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO punches VALUES(NULL, ?, ?, ?, ?)");
        stmt.setString(1, punchname);
        stmt.setString(2, punchstyle);
        stmt.setString(3, punchcomment);
        stmt.setInt(4, userId);
        stmt.execute();
    }

    public static Punch selectPunch(Connection conn, int userid) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM punches INNER JOIN users ON punches.userid = users.id WHERE users.id = ?");
        stmt.setInt(1, userid);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String punchname = results.getString("punches.punchname");
            String punchstyle = results.getString("punches.punchstyle");
            String punchcomment = results.getString("punches.punchcomment");
            String author = results.getString("users.name");
            return new Punch(userid, punchname, punchstyle, punchcomment, author);
        }
        return null;
    }

    public static ArrayList<Punch> selectPunches(Connection conn, int userid) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM punches");
        ResultSet results = stmt.executeQuery();
        ArrayList<Punch> pnchs = new ArrayList<>();
        while (results.next()) {
            String punchname = results.getString("punches.punchname");
            String punchstyle = results.getString("punches.punchstyle");
            String punchcomment = results.getString("punches.punchcomment");
            String author = results.getString("users.name");
            Punch pnch = new Punch(userid, punchname, punchstyle, punchcomment, author);
        }
        return null;
    }

    public static void deletePunch(Connection conn, int punchid) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM punches WHERE id = users.id");
        stmt.setInt(1, punchid);
        stmt.execute();
    }
    public static void editPunch(Connection conn, int punchid) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT FROM * punches WHERE id = user.id");
        stmt.setInt(1, punchid);
        stmt.execute();

    }
    public static void updatePunch(Connection conn, int userid) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE FROM punches SET newpunchname = ?, newpunchstyle = ?, newpunchcomment = ?, id = ?");
        ResultSet results = stmt.executeQuery();
        ArrayList<Punch> punches = new ArrayList<>();
        while (results.next()) {
            String newpunchname = results.getString("newpunchname");
            String newpunchstyle = results.getString("newpunchstyle");
            String newpunchcomment = results.getString("newpunchcomment");
            String author = results.getString("users.name");
            Punch pnch = new Punch(userid, newpunchname, newpunchstyle, newpunchcomment, author);
        }
    }





    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");


        Spark.staticFileLocation("public");
        Spark.init();

        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    String idStr = request.queryParams("id");
                    int id = 0;
                    if (idStr != null) {
                        id = Integer.valueOf(idStr);
                    }

                    ArrayList<Punch> punches = selectPunches(conn, id);

                    if (username == null) {
                        return new ModelAndView(conn, "login.html");
                    }

                    return new ModelAndView(conn, "index.html");
                },

                new MustacheTemplateEngine()

        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String name = request.queryParams("username");
                    String password = request.queryParams("password");


                    User user = selectUser(conn, name);
                    if (user == null) {
                        insertUser(conn, name, "");
                    }

                    Session session = request.session();
                    session.attribute("username", name);

                    response.redirect("/");
                    return "";
                }
        );
        Spark.post( //entering punch criteria
                "/pick-punch",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null) {
                        response.redirect("/login");
                    }

                    String punchname = request.queryParams("punchname");
                    String punchstyle = request.queryParams("punchstyle");
                    String punchcomment = request.queryParams("punchcomment");
                    if (punchname == null) {
                        throw new Exception("You have to choose something to punch!");
                    }
                    User user = selectUser(conn, username);
                    insertPunch(conn, punchname, punchstyle, punchcomment, user.userid);

                    response.redirect("/");
                    return "";

                }
        );
        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "/";
                }
        );
        Spark.post( //takes selected info and deletes it
                "/delete-punch",
                (request, response) -> {
                    int id = Integer.valueOf(request.queryParams("id"));

                    Session session = request.session();
                    String username = session.attribute("username");
                    Punch userPunch = selectPunch(conn, 1);
                    if (!userPunch.author.equals(username)) {
                        throw new Exception("You cannot delete this!");
                    }

                    deletePunch(conn, id);

                    response.redirect("/");
                    return "";
                }
        );

        Spark.get( //redirect to  update-punch page to edit
                "/edit-punch",
                (request, response) -> {
                    int id = Integer.valueOf(request.queryParams("id"));

                    Session session = request.session();
                    String username = session.attribute("username");

                    User user = selectUser(conn, username);

                    return new ModelAndView(conn, "newpunch.html");
                },
                new MustacheTemplateEngine()
        );
        Spark.post( //redirect to index.html with updated criteria
                "/update-punch",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    String newpunchname = request.queryParams("newpunchname");
                    String newpunchstyle = request.queryParams("newpunchstyle");
                    String newpunchcomment = request.queryParams("newpunchcomment");

                    User user = selectUser(conn, username);


                    response.redirect("/");
                    return "";

                }

        );

    }
}

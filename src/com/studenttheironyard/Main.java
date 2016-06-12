package com.studenttheironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();
    static ArrayList<User> userList = new ArrayList<>();
    static ArrayList<Comment> comments = new ArrayList<>();


    public static void main(String[] args) {
        Spark.staticFileLocation("public");
        Spark.init();

        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    HashMap m = new HashMap();

                    if (username == null) {
                        return new ModelAndView(m, "login.html");
                    } else {
                        User user = users.get(username);
                        m.put("punchlist", user.punchlist);
                        m.put("username", userList);
                    }

                    return new ModelAndView(m, "index.html");
                },

                    new MustacheTemplateEngine()

        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String name = request.queryParams("username");
                    String pass = request.queryParams("password");
                    if (name == null || pass == null) {
                        throw new Exception("Name or pass not sent");
                    }

                    User user = users.get(name);
                    if (user == null) {
                        user = new User(name, pass);
                        users.put(name, user);
                        userList.add(user);
                    } else if (!pass.equals(user.name)) {
                        throw new Exception("Wrong password");
                    }

                    Session session = request.session();
                    session.attribute("username", name);

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

        Spark.post(
                "/pick-punch",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    if (username == null) {
                        throw new Exception("Not logged in");
                    }
                    String punchname = request.queryParams("punchname");
                    String punchstyle = request.queryParams("punchstyle");
                    String punchcomment = request.queryParams("punchcomment");
                    if (punchname == null ||  punchcomment == null) {
                        throw new Exception("Invalid form fields");
                    }
                    User user = users.get(username);
                    if (user == null) {
                        throw new Exception("User does not exist");
                    }
                    Punch p = new Punch(punchname, punchstyle, punchcomment);
                    user.punchlist.add(p);

                    response.redirect("/");
                    return "";

                }
        );

        Spark.post(
                "/delete-punch",
                (request, response) -> {
                    int id = Integer.valueOf(request.queryParams("id"));

                    Session session = request.session();
                    String username = session.attribute("username");
                    ArrayList<User> users = new ArrayList<>(),;
                    if (!Punch.punchcomment.equals(username)) {
                        throw new Exception("You cannot delete this post");
                    }

                    comments.remove(id);
                    int index = 0; //reset ids
                    for (Comment msg : comments) {
                        msg.id = index;
                        index++;
                    }
                    response.redirect("/");
                    return "";
                }
        );
    }
}

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


    public static void main(String[] args) {
        Spark.staticFileLocation("public");
        Spark.init();

        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    String idStr = request.queryParams("replyId");
                    int id = 0;
                    if (idStr != null) {
                        id = Integer.valueOf(idStr);
                    }

                    HashMap m = new HashMap();

                    if (username == null) {
                        return new ModelAndView(m, "login.html");
                    } else {
                        User user = users.get(username);
                        m.put("punchlist", user.punchlist);
                    }

                    return new ModelAndView(m, "index.html");
                },

                new MustacheTemplateEngine()

        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String name = request.queryParams("username");
                    String password = request.queryParams("password");
                    if (name == null || password == null) {
                        throw new Exception("Name or pass not sent");
                    }

                    User user = users.get(name);
                    if (user == null) {
                        user = new User(name, password);
                        users.put(name, user);
                        userList.add(user);
                    } else if (!password.equals(user.name)) {
                        response.redirect("/login");
                        return "";
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
                    User user = users.get(username);
                    if (user == null) {
                        response.redirect("/login");
                        return "";
                    }
                    Punch p = new Punch(punchname, punchstyle, punchcomment);
                    user.punchlist.add(p);

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
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null) {
                        throw new Exception("Not logged in");
                    }

                    int id = Integer.valueOf(request.queryParams("id"));

                    User user = users.get(username);
                    if (id <= 0 || id - 1 >= user.punchlist.size()) {
                    }
                    user.punchlist.remove(id);

                    response.redirect("/");
                    return "";
                }
        );

        Spark.get( //redirect to  update-punch page to edit
                "/edit-punch",
                (request, response) -> {

                    Session session = request.session();
                    String username = session.attribute("username");

                    User user = users.get(username);
                    if(username == null) {
                        throw new Exception("you must log in first");
                    }
                    int id = (Integer.valueOf(request.queryParams("id")));
                    HashMap map = new HashMap();
                    User users = userList.get(id);
                    map.put("punchlist", users);

                    return new ModelAndView(map, "newpunch.html");
                },
                new MustacheTemplateEngine()
        );
        Spark.post( //redirect to index.html with updated criteria
                "/update-punch",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    String punchname = request.queryParams("newpunchname");
                    String punchstyle = request.queryParams("newpunchstyle");
                    String punchcomment = request.queryParams("newpunchcomment");

                    User user = users.get(username);

                    Punch p = new Punch(punchname, punchstyle, punchcomment);
                    user.punchlist.add(p);

                    response.redirect("/");
                    return "";

                }

        );

    }
}

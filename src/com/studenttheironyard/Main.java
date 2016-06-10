package com.studenttheironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> userMap = new HashMap<>();
    static ArrayList<Comment> comments = new ArrayList<>();

    public static void main(String[] args) {
        Spark.init();
        Spark.get(
                "people.html",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    String idStr = request.queryParams("replyId");
                    int replyId = -1;
                    if (idStr != null) {
                        replyId = Integer.valueOf(idStr);
                    }

                    ArrayList<Comment> subset = new ArrayList<>();
                    for (Comment msg : comments) {
                        if (msg.replyId == replyId) {
                            subset.add(msg);
                        }
                    }

                    Comment parentMsg = null;
                    if (replyId >=0) { //could use try,catch
                        parentMsg = comments.get(replyId);
                    }

                    HashMap m = new HashMap();
                    m.put("comments", subset);
                    m.put("username", username);
                    m.put("replyId", replyId);
                    m.put("comment", parentMsg);
                    m.put("isMe", parentMsg != null && username != null && parentMsg.author.equals(username));

                    return new ModelAndView(m, "index.html");
                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/login",
                (request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("userpassword");

                    User user = userMap.get(username);

                    if (username == null || password == null) {
                        throw new Exception("Name or Password not entered");
                    }
                    if (user == null) {
                        user = new User(username, password);
                        userMap.put(username, user);
                    }
                    else if (!password.equals(user.password)){
                        throw new Exception("Invalid password");
                    }

                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect("people.html");
                    return "";
                }
        );
        Spark.post(
                "/create-user",
                (request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("userpassword");

                    User user = userMap.get(username);
                    if (user == null){
                        user = new User(username,"");
                        userMap.put(username, user);
                    }
                    if (password == null) {
                        user = new User(password, "");
                        userMap.put(password, user);

                    }
                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect("people.html");
                    return "";
                }
        );

        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/create-comment",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null) {
                        throw new Exception("Not logged in.");
                    }
                    int replyId = Integer.valueOf(request.queryParams("replyId"));
                    String text = request.queryParams("message");
                    Comment msg = new Comment(comments.size(), replyId, username, text);
                    comments.add(msg);

                    response.redirect(request.headers("Referer"));
                    return "";

                }
        );
        Spark.post(
                "/delete-message",
                (request, response) -> {
                    int id = Integer.valueOf(request.queryParams("id"));

                    Session session = request.session();
                    String username = session.attribute("username");
                    Comment userMsg = comments.get(id);
                    if (!userMsg.author.equals(username)) {
                        throw new Exception("You cannot delete this post");
                    }

                    comments.remove(id);
                    int index = 0; //reset ids
                    for (Comment msg : comments) {
                        msg.id = index;
                        index++;
                    }
                    response.redirect(request.headers("Referrer"));
                    return "";
                }
        );

    }
}

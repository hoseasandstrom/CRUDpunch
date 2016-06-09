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

    static final int LISTPARAMS = 2 ;


    public static void main(String[] args) {

        addTestComments();
        addTestUsers();

        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    int offset = 0;
                    String offsetStr = request.queryParams("offset");
                    if (offsetStr != null) {
                        offset = Integer.valueOf(offsetStr);
                    }

                    ArrayList Comment = new ArrayList<>(comments.subList(offset, offset + LISTPARAMS));

                    HashMap map = new HashMap();
                    map.put("users", comments);
                    map.put("offsetNext", offset + LISTPARAMS);
                    map.put("offsetPrevious", offset - LISTPARAMS);
                    map.put("showPrevious" ,offset > LISTPARAMS);
                    map.put("showNext", offset + LISTPARAMS < comments.size());
                    return new ModelAndView(map, "index.html");
                },
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/",
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

                    Comment parentCmnt  = null;
                    if (replyId >=0) { //could use try,catch
                        parentCmnt = comments.get(replyId);
                    }

                    HashMap m = new HashMap();
                    m.put("comments", subset);
                    m.put("username", username);
                    m.put("replyId", replyId);
                    m.put("comment", parentCmnt);
                    m.put("isMe", parentCmnt != null && username != null && parentCmnt.author.equals(username));

                    return new ModelAndView(m, "people.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String username = request.queryParams("username");
                    if (username == null) {
                        throw new Exception("Login name not found.");

                    }
                    User user = userMap.get(username);
                    if (user == null){
                        user = new User(username,"");
                        userMap.put(username, user);

                    }
                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect(request.headers("Referer"));
                    return "";
                }
        );

        Spark.post(
                "/create-user",
                (request, response) -> {
                    String name = request.queryParams("username");
                    String password = request.queryParams("userpassword");

                    User user = userMap.get(name);
                    if (name == null || password == null) {
                        throw new Exception("Name or Password not entered");
                    }
                    if (user == null) {
                        user = new User(name, password);
                        userMap.put(name, user);
                    }
                    else if (!password.equals(user.password)){
                        throw new Exception("Invalid password");
                    }
                    Session session = request.session();
                    session.attribute("username", name);

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
                "/delete-comment",
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
                    for (Comment cmnt : comments) {
                        cmnt.id = index;
                        index++;
                    }
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
                    return "";
                }
        );

    }

    static void addTestUsers() {
        userMap.put("Alice", new User("Alice", ""));
        userMap.put("Bob", new User("Bob", ""));
        userMap.put("Charlie", new User("Charlie", ""));
    }

    static void addTestComments() {
        comments.add(new Comment(0, -1, "Alice", "Hello world!"));
        comments.add(new Comment(1, -1, "Bob", "This is a new thread!"));
        comments.add(new Comment(2, 0, "Charlie", "Cool thread, Alice!" ));
    }
}

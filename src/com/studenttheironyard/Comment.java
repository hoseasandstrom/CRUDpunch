package com.studenttheironyard;

/**
 * Created by hoseasandstrom on 6/9/16.
 */
public class Comment {
    int replyId;
    int id;
    String text;
    String author;


    public Comment(int replyId, int id, String text, String author) {
        this.replyId = replyId;
        this.id = id;
        this.text = text;
        this.author = author;
    }
}

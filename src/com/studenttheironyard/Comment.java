package com.studenttheironyard;

/**
 * Created by hoseasandstrom on 6/9/16.
 */
public class Comment {
    int id;
    int replyId;
    String author;
    String text;

    public Comment(int id, int replyId, String author, String text) {
        this.id = id;
        this.replyId = replyId;
        this.author = author;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReplyId() {
        return replyId;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", replyId=" + replyId +
                ", author='" + author + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}

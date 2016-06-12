package com.studenttheironyard;

/**
 * Created by hoseasandstrom on 6/9/16.
 */
public class Punch {
    String punchname;
    String punchstyle;
    String punchcomment;


    public Punch(String punchname, String punchstyle, String punchcomment) {
        this.punchname = punchname;
        this.punchstyle = punchstyle;
        this.punchcomment = punchcomment;
    }

    public String getPunchname() {
        return punchname;
    }

    public void setPunchname(String punchname) {
        this.punchname = punchname;
    }

    public String getPunchstyle() {
        return punchstyle;
    }

    public void setPunchstyle(String punchstyle) {
        this.punchstyle = punchstyle;
    }

    public String getPunchcomment() {
        return punchcomment;
    }

    public void setPunchcomment(String punchcomment) {
        this.punchcomment = punchcomment;
    }

}


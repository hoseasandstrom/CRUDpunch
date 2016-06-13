package com.studenttheironyard;

import java.util.ArrayList;

/**
 * Created by hoseasandstrom on 6/9/16.
 */
public class User {
    String name;
    String password;
    ArrayList<Punch> punchlist = new ArrayList<>();

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
}

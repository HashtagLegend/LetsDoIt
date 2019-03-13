package com.example.letsdoit.Model;

public class User {

    private String usernname;
    private String password;

    public User(){

    }

    public User(String usernname, String password) {
        this.usernname = usernname;
        this.password = password;
    }

    public String getUsernname() {
        return usernname;
    }

    public void setUsernname(String usernname) {
        this.usernname = usernname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

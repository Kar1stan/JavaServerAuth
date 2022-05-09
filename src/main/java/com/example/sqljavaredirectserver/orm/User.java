package com.example.sqljavaredirectserver.orm;

public class User {
    private String id;
    private String login;
    private String salt;

    public User() {
    }

    public User(String id, String login, String salt) {
        this.id = id;
        this.login = login;
        this.salt = salt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}

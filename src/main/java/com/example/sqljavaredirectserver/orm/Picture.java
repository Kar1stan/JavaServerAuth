package com.example.sqljavaredirectserver.orm;


import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

// ORM for Gallery Table
public class Picture {
    private String id;
    private String description;
    private String picture;
    private String userId;
    private java.sql.Date moment;
    private java.sql.Date deleted;

    public Picture( ResultSet res ) throws SQLException {
        this.id          = res.getString("id");
        this.description = res.getString("description");
        this.picture     = res.getString("picture");
        this.userId      = res.getString("user_id");
        this.moment      = res.getDate("moment");
        this.deleted     = res.getDate("deleted");
    }

    public Picture(String description, String picture, String userId) {
        this.description = description;
        this.picture = picture;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getMoment() {
        return moment;
    }

    public void setMoment(Date moment) {
        this.moment = moment;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }
}

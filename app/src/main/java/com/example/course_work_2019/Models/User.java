package com.example.course_work_2019.Models;

public class User {
    private String user_id;
    private String phone;
    private String link;
    private String name;

    public User(){}

    public User(String _user_id, String _phone, String _link, String _name) {
        user_id = _user_id;
        phone = _phone;
        link = _link;
        name = _name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

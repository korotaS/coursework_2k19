package com.example.course_work_2019.Models;

public class Offer  {
    private String user_id;
    private String offer_id;
    private String name;
    private String description;
    private String category;
    private String city;
    private String pers_name;
    private String phone;
    private String soc_netw;
    private Long numPhotos;
    private String main_photo;
    private String[] photos;
    private boolean published;
    private String date;
    private boolean find_or_offer; //true - find, false = offer

    public boolean isFind_or_offer() {
        return find_or_offer;
    }

    public void setFind_or_offer(boolean find_or_offer) {
        this.find_or_offer = find_or_offer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public String getMain_photo() {
        return main_photo;
    }

    public void setMain_photo(String main_photo) {
        this.main_photo = main_photo;
    }

    public Long getNumPhotos() {
        return numPhotos;
    }

    public void setNumPhotos(Long numPhotos) {
        this.numPhotos = numPhotos;
    }

    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String designation) {
        this.description = designation;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSoc_netw() {
        return soc_netw;
    }

    public void setSoc_netw(String soc_netw) {
        this.soc_netw = soc_netw;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPers_name() {
        return pers_name;
    }

    public void setPers_name(String pers_name) {
        this.pers_name = pers_name;
    }
}

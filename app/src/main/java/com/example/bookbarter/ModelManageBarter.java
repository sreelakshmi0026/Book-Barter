package com.example.bookbarter;

public class ModelManageBarter {
    String bookname,authorname,description,imageuri,genre,city,id,postedby;
    float rating;
    Double longitude,latitude;

    public String getBookname() {
        return bookname;
    }
    public ModelManageBarter()
    {

    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getPostedby() {
        return postedby;
    }

    public void setPostedby(String postedby) {
        this.postedby = postedby;
    }

    public ModelManageBarter(String bookname, String authorname, String description, String imageuri, String genre, String city, String id, Double longitude, Double latidude, String postedby, float rating) {
        this.bookname = bookname;
        this.authorname = authorname;
        this.description = description;
        this.imageuri = imageuri;
        this.genre = genre;
        this.city = city;
        this.id = id;
        this.longitude = longitude;
        this.latitude = latidude;
        this.postedby = postedby;
        this.rating = rating;
    }


    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

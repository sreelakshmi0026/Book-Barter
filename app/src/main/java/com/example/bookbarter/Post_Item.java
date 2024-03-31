package com.example.bookbarter;

import java.net.URI;
import java.util.List;

public class Post_Item {
    String bookname,authorname,description,imageuri,genre,city,id,postedby;
    float rating;
    List<String> interestedpeople;

    public String getPostedby() {
        return postedby;
    }

    public List<String> getInterestedpeople() {
        return interestedpeople;
    }

    public void setInterestedpeople(List<String> interestedpeople) {
        this.interestedpeople = interestedpeople;
    }

    public Post_Item(String bookname, String authorname, String description, String imageuri, String genre, String city, String id, String postedby, float rating, List<String> interestedpeople) {
        this.bookname = bookname;
        this.authorname = authorname;
        this.description = description;
        this.imageuri = imageuri;
        this.genre = genre;
        this.city = city;
        this.id = id;
        this.postedby = postedby;
        this.rating = rating;
        this.interestedpeople=interestedpeople;
    }

    public void setPostedby(String postedby) {
        this.postedby = postedby;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthorname() {
        return authorname;
    }
    public Post_Item(){}

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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

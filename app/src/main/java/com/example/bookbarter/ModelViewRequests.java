package com.example.bookbarter;

import java.util.ArrayList;
import java.util.List;

public class ModelViewRequests {
    String name,email,phonenumber,address;
    List<String> wishlist,booksposted;
    public static List<String> ArrCheck=new ArrayList<>();
    public static List<String> idArr=new ArrayList<>();
    public static String userid;

    public String getAddress() {
        return address;
    }

    public ModelViewRequests(String name, String email, String phonenumber, String address, List<String> wishlist, List<String> booksposted) {
        this.name = name;
        this.email = email;
        this.phonenumber = phonenumber;
        this.address = address;
        this.wishlist = wishlist;
        this.booksposted = booksposted;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ModelViewRequests()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public List<String> getWishlist() {
        return wishlist;
    }

    public void setWishlist(List<String> wishlist) {
        this.wishlist = wishlist;
    }

    public List<String> getBooksposted() {
        return booksposted;
    }

    public void setBooksposted(List<String> booksposted) {
        this.booksposted = booksposted;
    }
}

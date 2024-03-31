package com.example.bookbarter;

import java.util.List;

public class UserModel {
    String name,email,phonenumber,address,userid,profilepic;
    List<String> wishlist,booksposted;

    public String getName() {
        return name;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public UserModel()
    {

    }
    public UserModel(String name, String email, String phonenumber, String address, String userid, List<String> wishlist, List<String> booksposted,String profilepic) {
        this.name = name;
        this.email = email;
        this.phonenumber = phonenumber;
        this.address = address;
        this.userid = userid;
        this.wishlist = wishlist;
        this.booksposted = booksposted;
        this.profilepic=profilepic;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

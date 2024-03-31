package com.example.bookbarter;

import java.util.List;

public class ModelWishList {
    public String title;
    public Integer image;
    public Boolean isSelected=false;
    public static int count=0;

    List<String> wishlist;
    public ModelWishList(String title,Integer image)
    {
        this.title=title;
        this.image=image;
    }
    public String getTitle() {
        return title;
    }
    public Integer getImage() {
        return image;
    }
    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
    public Boolean isSelected()
    {
        return isSelected;
    }

    public void addWishList(String genre)
    {
        wishlist.add(genre);
    }

    public List<String> getWishlist() {
        return wishlist;
    }
}

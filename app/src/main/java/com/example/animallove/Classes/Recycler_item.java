package com.example.animallove.Classes;

/**
 * Created by 이승헌 on 2017-12-01.
 */

public class Recycler_item {
    private int image;
    private String title;

    public int getImage(){
        return this.image;
    }
    public String getTitle(){
        return this.title;
    }

    public Recycler_item(int image, String title){
        this.image=image;
        this.title=title;
    }
}

package com.example.animallove.Classes;

/**
 * Created by 이승헌 on 2017-12-01.
 */

public class Recycler_item {
    private String image,name, region, gender, kind, desc;

    public String getImage(){
        return this.image;
    }
    public String getName(){
        return this.name;
    }
    public String getRegion(){
        return this.region;
    }
    public String getGender(){
        return this.gender;
    }
    public String getKind(){
        return this.kind;
    }
    public String getDesc(){
        return this.desc;
    }

    public Recycler_item(String image, String name, String region, String gender, String kind, String desc){
        this.image=image;
        this.name=name;
        this.region=region;
        this.gender=gender;
        this.kind=kind;
        this.desc=desc;
    }
}

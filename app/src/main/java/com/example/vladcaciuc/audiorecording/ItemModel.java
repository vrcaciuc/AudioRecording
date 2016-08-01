package com.example.vladcaciuc.audiorecording;


/**
 * Created by vladcaciuc on 7/31/16.
 */
public class ItemModel{
    private static String mName, nDuration;

    public ItemModel(String name, String duration) {
        this.mName = name;
        this.nDuration = duration;
    }

    public String getName() {
        return mName;
    }
    public String getDuration() {
        return nDuration;
    }

}

package com.example.konstantin.locator.JSON_model;

import android.util.Log;

import java.util.List;

/**
 * Created by Konstantin on 15.05.2017.
 */

// POJO класс-структура для десериализации JSON
public class ParsedJSON {

    private Photos photos;
    private String stat;

    private final String TAG = "Parsed JSON info";

    public ParsedJSON(Photos photos, String stat) {
        this.photos = photos;
        this.stat = stat;
    }

    // печать в консоль информации о распарсенных данных
    public void printParsedToLog () {

        Log.i(TAG, "Stat=" + stat + "\nPage " + photos.page + " of " + photos.pages + ", " + photos.perpage + " photos on page");

        for (int i = 0; i < photos.photo.size(); i++) {
            logInfo(photos.photo.get(i), i);
        }
    }

    public void logInfo(Photo photo, int i) {
        Log.i(TAG, i + " ____________________________________________________________");
        Log.i(TAG, "id = " + photo.id + " || " + " owner = " + photo.owner + " || " + " is public = " + photo.ispublic);
        Log.i(TAG, "caption = " + photo.caption + " url: " + photo.url);
        Log.i(TAG, "Latitude = " + photo.latitude + " Longitude: " + photo.longitude);
        Log.i(TAG, "______________________________________________________________");
    }

    public List<Photo> getPhotoList() {
        return this.photos.photo;
    }


}

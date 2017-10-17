package com.example.konstantin.locator.JSON_model;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Konstantin on 11.05.2017.
 *
 * Класс модели объекта. Содержит заголовок, ID, URL
 * Данные парсятся и беруться из JSON, передаваемого сервером
 *
 */

public class Photo {

    @SerializedName("title")
    public String caption;
    @SerializedName("owner")
    public String owner;
    @SerializedName("id")
    public String id;
    @SerializedName("ispublic")
    public Integer ispublic;
    @SerializedName("url_s")
    public String url;

    // гео-метки фотографии
    @SerializedName("latitude")
    public double latitude;
    @SerializedName("longitude")
    public double longitude;


    public Photo(String caption, String owner, String id, Integer ispublic, String url) {
        this.caption = caption;
        this.owner = owner;
        this.id = id;
        this.ispublic = ispublic;
        this.url = url;
    }

    // пустой конструктор
    public Photo() {  }

    public LatLng getLatLngPosition() {
        return new LatLng(latitude, longitude);
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIspublic() {
        return ispublic;
    }

    public void setIspublic(Integer ispublic) {
        this.ispublic = ispublic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongtitude(double longtitude) {
        this.longitude = longtitude;
    }

    // возвращает Url картинки, генерируя его по схеме
    // "http://www.flickr.com/photos/" + "/owner" + "/id"
    public Uri getPhotoPageUri() {
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build();
    }
}

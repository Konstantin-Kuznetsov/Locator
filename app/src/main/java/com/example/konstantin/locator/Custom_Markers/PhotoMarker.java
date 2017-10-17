package com.example.konstantin.locator.Custom_Markers;

import android.net.Uri;

import com.example.konstantin.locator.Custom_Markers.AbstractMarker;
import com.example.konstantin.locator.JSON_model.Photo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Konstantin on 13.07.2017.
 *
 * Маркер - положение фотографии на карте.
 */

public class PhotoMarker extends AbstractMarker {

    private final String title;
    private final String thumbnailURL; // ссылка на миниатюру с предпросмотром
    private final Uri photoPageUri; // ссылка на страницу просмотра фото в полном размере

    public PhotoMarker(Photo photo) {
        markerPosition = photo.getLatLngPosition();
        title = photo.getCaption();
        thumbnailURL = photo.getUrl();
        photoPageUri = photo.getPhotoPageUri();
    }

    @Override
    public MarkerOptions getMarkerOptions() {
        // создаем и конфигурируем маркеры для показа местоположения пользователя и ближайшей картинки
        return new MarkerOptions()
                .position(markerPosition)
                .title(title);
    }

    @Override
    public Marker addMarkerOnMap(GoogleMap map) {
        return map.addMarker(getMarkerOptions());
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public Uri getPhotoPageUri() {
        return photoPageUri;
    }

    @Override
    public String getTitle() {
        return title;
    }
}

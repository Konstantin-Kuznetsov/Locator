package com.example.konstantin.locator.Custom_Markers;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Konstantin on 13.07.2017.
 *
 * Маркер - текущее положение устройства.
 */

public class MyPositionMarker extends AbstractMarker {

    private final String title = "Текущее метоположение";

    public MyPositionMarker(LatLng position) {
        this.markerPosition = position;
    }

    @Override
    public MarkerOptions getMarkerOptions() {
        return new MarkerOptions()
                .position(markerPosition)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)); // желтый цвет маркера
    }

    @Override
    public Marker addMarkerOnMap(GoogleMap map) {
        return map.addMarker(getMarkerOptions());
    }

    @Override
    public String getTitle() {
        return title;
    }
}

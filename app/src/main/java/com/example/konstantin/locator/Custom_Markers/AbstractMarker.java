package com.example.konstantin.locator.Custom_Markers;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Konstantin on 13.07.2017.
 *
 * Абстрактный класс, реализующий интерфейс ClusterItem ( LatLng getPosition(), String getTitle(), String getSnippet() )
 *
 * От этого класса будут наследоваться все остальные классы маркеров, кастомизируя при необходимости,
 * например, внешний вид через конструктор MarkerOptions() и внешний вид InfoWindow
 */

public abstract class AbstractMarker implements ClusterItem {

    public LatLng markerPosition = null;
    //GoogleMap googleMap = null;

    @Override
    public LatLng getPosition() {
        return this.markerPosition;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    // переопределить в наследниках в зависимости от желаемого вида маркера
    public abstract MarkerOptions getMarkerOptions();

    // тоже конфигурируется в наследниках в зависимости от желаемых опций поведения маркера
    public abstract Marker addMarkerOnMap(GoogleMap map);
}

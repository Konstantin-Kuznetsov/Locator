package com.example.konstantin.locator.Custom_Markers;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Konstantin on 14.07.2017.
 *
 * Кастомизированный обработчик прорисовки кластеров маркеров.
 */

public class CustomClasterRenderer extends DefaultClusterRenderer<AbstractMarker> {

    public CustomClasterRenderer(Context context, GoogleMap map, ClusterManager<AbstractMarker> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(AbstractMarker item, MarkerOptions markerOptions) {
        // желтый цвет маркера собственного местоположения. (Все остальные имеют вид по умолчанию)
        if (item instanceof MyPositionMarker) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }
    }
}

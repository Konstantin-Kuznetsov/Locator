package com.example.konstantin.locator.UI;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konstantin.locator.Custom_Markers.AbstractMarker;
import com.example.konstantin.locator.Custom_Markers.CustomClasterRenderer;
import com.example.konstantin.locator.Custom_Markers.MyPositionMarker;
import com.example.konstantin.locator.Custom_Markers.PhotoMarker;
import com.example.konstantin.locator.FlickrFetchr;
import com.example.konstantin.locator.JSON_model.Photo;
import com.example.konstantin.locator.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Konstantin on 06.07.2017.
 */

public class LocatorFragment extends SupportMapFragment {

    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;
    //
    private List<Photo> itemsList;
    private Location currentLocation;
    //
    private ClusterManager<AbstractMarker> clusterManager;
    private AbstractMarker chosenMarker; // последний выбранный маркер
    private Cluster<AbstractMarker> chosenCluster; // последний выбранный кластер маркеров
    public boolean shouldRender;
    //
    private final String TAG = "LocatorFragment";
    private final int REQUEST_FINE_LOCATION_PERMISSION = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // включаем меню

        // Создаем и настраиваем экземпляр-клиент GoogleApiClient
        // для использования с API геолокации
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        // соединение установлено
                        getActivity().invalidateOptionsMenu();
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                        // соединение приостановлено
                    }
                } )
                .build();

        // назначаем все необходимые слушатели событий и кластеризацию по готовности карты
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                // элементы управления зумирования
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                // Настройка кластеризации маркеров. Инициализация ClusterManager
                clusterManager = new ClusterManager<AbstractMarker>(getActivity(), googleMap);
                // устанавливаем кастомизированный обработчик событий и вида кластеров
                // (алгоритм по умолчанию - Distance-based Clustering)
                clusterManager.setRenderer(new CustomClasterRenderer(getActivity(), googleMap, clusterManager));

                // клик на маркере, перезапись текущего маркера
                clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<AbstractMarker>() {
                    @Override
                    public boolean onClusterItemClick(AbstractMarker item) {
                        chosenMarker = item;
                        return false;
                    }
                });

                // клик на кластере, перезапись текущего кластера, наводим фокус камеры на маркеры текущего кластера
                clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<AbstractMarker>() {
                    @Override
                    public boolean onClusterClick(Cluster<AbstractMarker> cluster) {
                        chosenCluster = cluster;

                        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
                        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);

                        for (AbstractMarker absMarker: chosenCluster.getItems()) {
                            latLngBuilder.include(absMarker.getPosition());
                        }

                        CameraUpdate zoomToFitClusterItems = CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(), margin);
                        googleMap.animateCamera(zoomToFitClusterItems);

                        return true;
                    }
                });

                // Клик на всплывающем информационном окне. Если в объекте-маркере доступна ссылка
                // на страницу, формируем интент, и передаем Uri страницы WebView
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (chosenMarker instanceof PhotoMarker) {
                            PhotoMarker p = (PhotoMarker) chosenMarker;
                            if (p.getPhotoPageUri() != null) {
                                Intent openPhoto = PhotoPageActivity.newIntent(getActivity(), p.getPhotoPageUri());
                                startActivity(openPhoto);
                            } else {
                                Toast.makeText(getActivity(), "Просмотр страницы недоступен", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                // обработка нажатий на маркеры передаетсся ClusterManager
                googleMap.setOnMarkerClickListener(clusterManager);

                // ставим слушателя перемешения камеры по карте для перерисовки маркеров
                googleMap.setOnCameraIdleListener(clusterManager);

                // передаем управление InfoWindow к ClusterManager
                googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());

//                 При добавлении слоя с границами стран тормозит.
//                try {
//                    GeoJsonLayer layer = new GeoJsonLayer(googleMap, R.raw.country_borders_low_res, getActivity());
//
//                    GeoJsonPolygonStyle style = layer.getDefaultPolygonStyle();
//                    //style.setFillColor(Color.MAGENTA);
//                    style.setStrokeColor(Color.MAGENTA);
//                    style.setStrokeWidth(1F);
//
//                    layer.addLayerToMap();
//                } catch (IOException | JSONException e) {
//                    e.printStackTrace();
//                }


                updateUI();
            }
        });
    }

    // Google рекомендует всегда подключаться к клиенту в методе onStart() и отключаться в onStop().
    @Override
    public void onStart() {
        super.onStart();
        // обновляем меню в соответствии с наличием(отсутствием) сервися Google. Обновляется в onCreateOptionsMenu()
        getActivity().invalidateOptionsMenu();
        googleApiClient.connect(); // подключаемся к клиенту
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect(); // отключаемся
    }

    @Override
    // формируем меню из файла XML
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_locator, menu);

        // обновляем видимость в соответствии со статусом подключения
        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(googleApiClient.isConnected());
    }

    // обработка нажатия на пункт меню с поиском картинки в меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                findImage(); // ищем картинку по текущему геотегу
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static LocatorFragment newInstance() {
        return new LocatorFragment();
    }

    // формируем запрос к геослужбе Google. Важные параметры:
    //
    // smallest displacement – при каком минимальном смещении устройства (в метрах) должно
    // инициироваться обновление местоположения
    //
    // priority - как следует поступать в ситуации выбора между расходом заряда аккумулятора
    // и точностью выполнения запроса
    // Interval - частота повторных запросов местоположения. (0 - как можно чаще)
    private void findImage() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
        locationRequest.setInterval(0);

        // проверяем разрещения для доступа к геолокации
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Устанавливаем слушатель на обновления местоположения.
            // Запрашиваем изменение положения, передавая API LocationRequest и экземпляр GoogleAPIClient
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i(TAG, "Получено перемещение от Location API: " + location);
                    new SearchTask().execute(location); // фоном грузим фотографию
                }
            });
        } else {
            // если разрешения нет, проверяем, следует ли вывести запрос на разрешение для пользователя
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // ???????
                } else {
                    // нет необходимости для сообщения с объяснением, можно запросить разрешение
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
                }
        }
    }

    // масштабирование карты в соответиствии с полученными текущими координатами и нанесение маркеров
    private void updateUI() {
        if (googleMap == null || itemsList == null) {
            // карта не готова
        }
        else {
            // очищаем карту
            googleMap.clear();

            // сначала наносим маркер с собственной позицией
            LatLng myPointOnMap = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()); // мое собственное
            clusterManager.addItem(new MyPositionMarker(myPointOnMap));

            // A LatLngBounds instance represents a rectangle in geographical coordinates,
            // including one that crosses the 180 degrees longitudinal meridian.
            // Для обпеделения границ условного прямоугольника можно указать его размеры, либо,
            // как показано ниже перечислить в конструкторе список точек, которые должны в него попасть
            // методом include(LatLng)
            LatLngBounds.Builder builderLatLng = new LatLngBounds.Builder();

            builderLatLng.include(myPointOnMap); // сначала добавляем собственную позицию

            for (Photo p: itemsList) {
                clusterManager.addItem(new PhotoMarker(p));
                // добавляем в LatLngBounds все маркеры для дальнейшего позиционирования карты и корректного масштабирования
                builderLatLng.include(p.getLatLngPosition());
            }

            // устанавливаем обработчик нажатий еа каждый маркер в коллекции кластеров ClusterManager
            clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MarkerInfoWindow());

            // отступ для оформления берем из ресурсов
            int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);

            // Класс CameraUpdateFactory содержит разнообразные статические методы для построения
            // различных видов объектов CameraUpdate, которые изменяют позицию, уровень увеличения
            // и другие свойства участка, отображаемого на карте.
            clusterManager.cluster();

            // В данном случае камера наводится на объект LatLngBounds bounds
            CameraUpdate camAnimation = CameraUpdateFactory.newLatLngBounds(builderLatLng.build(), margin);

            // Карта обновляется одним из двух способов: методом moveCamera(CameraUpdate)
            // или animateCamera(CameraUpdate).
            googleMap.animateCamera(camAnimation);

        }
    }

    // Адаптер InfoWindow (окна, появляющегопя по нажатию на маркер)
    private class MarkerInfoWindow implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoContents(Marker marker) {
            if (chosenMarker != null) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);

                // Широта и долгота (формат DDD:MM:SS.SSSSS)
                LatLng latLng = marker.getPosition();
                TextView titleLatitude = (TextView) view.findViewById(R.id.latitude_popup_map);
                titleLatitude.setText(Location.convert(latLng.latitude, Location.FORMAT_SECONDS));
                TextView titleLongitude = (TextView) view.findViewById(R.id.longitude_popup_map);
                titleLongitude.setText(Location.convert(latLng.longitude, Location.FORMAT_SECONDS));

                // Заголовок
                TextView titleTextPopUp = (TextView) view.findViewById(R.id.title_popup_map);
                if (chosenMarker.getTitle().length() < 50) {
                    titleTextPopUp.setText(chosenMarker.getTitle());
                } else {
                    titleTextPopUp.setText(chosenMarker.getTitle().trim().substring(0, 46).concat("..."));
                }


                // Фото
                ImageView imagePopUp = (ImageView) view.findViewById(R.id.image_popup_map);

                if (chosenMarker instanceof MyPositionMarker) {
                    imagePopUp.setImageResource(R.drawable.my_position_ico);
                } else {
                    // Т.к типов маркеров всего два(собственая позиция и маркер с фото), то
                    // загружаем картинку фоновым потоком в Picasso и устанавливаем
                    // соответствующему ImageView (просто обновляем его после загрузки)
                    // Обновление происходит в коллбэке InfoWindowRefresher, в OnSuccess()
                    PhotoMarker photoMarker = (PhotoMarker) chosenMarker;
                    if (photoMarker.getThumbnailURL() != null) {
                        Picasso.with(getActivity())
                                .load(photoMarker.getThumbnailURL())
                                .placeholder(R.drawable.ic_photo_black_48px)
                                .into(imagePopUp, new InfoWindowRefresher(marker));
                    }
                }
                return view;
            }
            else return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }

    // в фоновом потоке грузим по метоположению массив фотографий
    private class SearchTask extends AsyncTask<Location, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Загрузка данных...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Location... locations) {
            currentLocation = locations[0]; // текущее местоположение
            FlickrFetchr flickrFetchr = new FlickrFetchr();
            itemsList = flickrFetchr.searchPhotos(locations[0]); // запрос по геоположению

            return null;
        }

        // Устанавливаем скачанное фото на картинку
        @Override
        protected void onPostExecute(Void aVoid) {
            updateUI();
            progressDialog.dismiss(); // отключаем показ диалога
        }
    }

    // Picasso's callback, по завершении загрузки фото, обновляет InfoWindow
    private class InfoWindowRefresher implements Callback {
        Marker markerToRefresh = null;

        private InfoWindowRefresher(Marker marker) {
            this.markerToRefresh = marker;
        }

        @Override
        public void onSuccess() {
            if (markerToRefresh != null && markerToRefresh.isInfoWindowShown()) {
                markerToRefresh.hideInfoWindow();
                markerToRefresh.showInfoWindow();
            }
        }

        @Override
        public void onError() {
            Log.e(TAG, "Ошибка загрузки изображения в InfoWindow");
        }
    }

    // Результат запроса разрешения. В зависимости от результата- выполняем действия
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            // обработка запроса на геолокацию
            case (REQUEST_FINE_LOCATION_PERMISSION): {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Разрешение на геопозиционирование получено", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Для корректной работы приложения необходимо \n разрешение на геопозиционирование", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

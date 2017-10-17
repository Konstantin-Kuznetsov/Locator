package com.example.konstantin.locator;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.example.konstantin.locator.JSON_model.ParsedJSON;
import com.example.konstantin.locator.JSON_model.Photo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konstantin on 10.05.2017.
 */

public class FlickrFetchr {

    private static final String TAG = "PhotoGallery";
    private ParsedJSON parsedJSON;
    private static final String API_KEY = "18cc5b282dc1f5f0b2973a4075fdf3bc";
    private List<Photo> items = new ArrayList<>(); // массив под результат

    // поиск по строке
    private static final String SEARCH_METHOD = "flickr.photos.search";

    // заготовка под запрос фото
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s,geo") // добавляем в ответ ссылку на фото и геоданные
            .build();

    // конструктор пустой
    public FlickrFetchr() {}

    // Принимает Url строкой и возвращает строку, сформированную из массива байтов, который в свою очередь
    // сформирован методом getUrlBytes из потока
    //
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<Photo> searchPhotos(Location location) {
        String url = buildUrl(location);
        return downloadGalleryItems(url);
    }

    // метод возвращает массив объектов Photo, готовых для дальнейшей работы
    private List<Photo> downloadGalleryItems(String url) {

        try {
            // формируем запрос к API Flickr
            // сформированный запрос к API сервера в виде строки
            String jsonString = getUrlString(url);

            // логгирование
            Log.i(TAG, "Received JSON: " + jsonString);

            // создаем объект JSON из строки, содержащей все данные
            // для даьлнейшего парсинга данных в метода parseJSON
            // и массив под результат
            JSONObject jsonObject = new JSONObject(jsonString);
            //parseJSON(items, jsonObject); // парсим с помощью JSON
            parseGSON(jsonObject); // парсим с помощью GSON


        } catch (JSONException j) {
            Log.e(TAG, "Ошибка парсинга JSON: ", j);
        } catch (IOException io) {
            Log.e(TAG, "Ошибка ввода/вывода ", io);
        }

        return items;
    }

    // передается методы тип метода и строка со строкой запроса(в случае если запрос с поиском)
    private String buildUrl(Location location) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", SEARCH_METHOD)
                .appendQueryParameter("lat", "" + location.getLatitude())
                .appendQueryParameter("lon", "" + location.getLongitude());

        return uriBuilder.build().toString();
    }


    // Парсинг с помощью библиотеки GSON
    private void parseGSON(JSONObject jsonObject) {
        Gson gson = new GsonBuilder().create();
        parsedJSON = gson.fromJson(jsonObject.toString(), ParsedJSON.class);
        parsedJSON.printParsedToLog();
        items.addAll(parsedJSON.getPhotoList()); // добавляем к списку новые элементы
    }



    public byte[] getUrlBytes(String urlSpec) throws IOException {

        // открываем подключение по заданному строкой адресу
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            // если с подключением все не ОК, формируем и пробрасываем ошибку
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            // читаем из потока блоками по 1024 байта
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray(); // метод возвращает поток, преобразованный в массив байтов
        } finally {
            connection.disconnect();
        }
    }
}

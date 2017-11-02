### Locator
> **Использованные в проекте библиотеки:** 
> - Play services - location(FusedLocationProvider), maps
> - Picasso
> - GSON
> - UI - Constraint layout, WebView
> - работа с RequestPermission
> - Т.к это приложеие было сделано до моего знакомства с 'Retrofit', то запросы в к серверу Flickr производятся через 'HttpURLConnection', просто JSON считывается блоками байтов и парсится c `GSON`.

![screen](https://user-images.githubusercontent.com/18750579/32324651-29eb9e8e-bfdd-11e7-95ef-1961a340144b.gif)

Приложение сделано по мотивам того, что объясняется в книге	Android Programming: The Big Nerd Ranch Guide в последних главах про геопозиционирование.

Идея использовать сайт Flickr.com как источник фотографий с привязкой к геопозиции взята из книги. Но идея дополнена, я кастомизировал маркеры на карте, разобрался с Cluster manager и т.д

![screen-2](https://user-images.githubusercontent.com/18750579/32324913-ffaaa6c8-bfdd-11e7-9de5-b522b1a8dae5.gif)

Помимо этого, по клику на маркер фотографии, открывается активити с фрагментом с WebView, с полной страницей фотографии на сайте Flickr.com. 
Во время загрузки показывается индикатор загрузки.
С этой страницы можно переходить дальше по ссылкам, кнопка back работает, по событию нажатия сначала проверяется backstack у `WebView` и, если возможно, переходит на предыдущую страницу, иначе вызывается просто `onBackPressed()` у активити.

```java
public void onBackPressed() {
        if (photoPageFragment.getWebView().canGoBack()) {
            photoPageFragment.getWebView().goBack();
        }
        else { super.onBackPressed(); }
    }
```

Для определения местоположения используется `FusedLocationProvider`. При запросе местоположения корректно обрабатываются `RequestPermission` (ACCESS_COARSE_LOCATION, ACCESS_NETWORK_STATE, INTERNET...).
После того, как местоположение получено, с Flickr.com запрашивается 250 ближайших по геопозиции фотографий, размещаются на карте маркерами.
Маркеры сделаны разных типов- маркер собственного местоположения, желтый, со своим `InfoWindow` и маркер фотографии, красный, со своим `InfoWindow`, в котором показывается краткое название фото и подгружается миниатюра фото, с помощью `Picasso`. 
По окончании загрузки фото, коллбэк обновляет `InfoWindow`, заменяет картинку-заглушку скачанным фото и скрывает/показывает окошко. 

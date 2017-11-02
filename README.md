### Locator
> **Использованные в проекте библиотеки:** 
> - Play services - location(FusedLocationProvider), maps
> - Picasso
> - GSON
> - UI - Constraint layout, WebView
> - работа с RequestPermission

Приложение сделано по мотивам того, что объясняется в книге	Android Programming: The Big Nerd Ranch Guide в последних главах про геопозиционирование.

Идея использовать сайт Flickr.com как источник фотографий с привязкой к геопозиции взята из книги. Но идея дополнена, я кастомизировал маркеры на карте, разобрался с Cluster manager и т.д

Помимо этого, по клику на маркер фотографии, открывается активити с фрагментом с WebView, с полной страницей фотографии на сайте Flickr. 
Во время загрузки показывается индикатор загрузки.
С этой страницы можно переходить дальше по ссылкам, кнопка back работает, по событию нажатия сначала проверяется backstack у WebView и, если возможно, переходит на предыдущую страницу, иначе вызывается просто onBackPressed() у активити.

```java
public void onBackPressed() {
        if (photoPageFragment.getWebView().canGoBack()) {
            photoPageFragment.getWebView().goBack();
        }
        else { super.onBackPressed(); }
    }
```

Для определения местоположения используется FusedLocationProvider. При запросе местоположения корректно обрабатывается RequestPermission.
После того, как местоположение получено, с Flickr.com запрашивается 250 ближайших по геопозиции фотографий, размещаются на карте маркерами.

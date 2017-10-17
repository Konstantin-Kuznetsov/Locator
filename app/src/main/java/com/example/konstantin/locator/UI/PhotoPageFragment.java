package com.example.konstantin.locator.UI;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.konstantin.locator.R;

/**
 * Created by Konstantin on 15.07.2017.
 */

public class PhotoPageFragment extends Fragment {

    private WebView webView;
    private Uri uri;
    private ProgressBar progressBar;
    private static final String ARG_URI = "photopage_url"; // адрес страницы с фотографией

    // Возвращает экземпляр PhotoPageFragment с переданной в метод ссылкой в аргументах
    public static PhotoPageFragment newInstance(Uri uri) {
        Bundle pageUrlBundle = new Bundle();
        pageUrlBundle.putParcelable(ARG_URI, uri);
        PhotoPageFragment instance = new PhotoPageFragment();
        instance.setArguments(pageUrlBundle);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uri = getArguments().getParcelable(ARG_URI);
    }

    public WebView getWebView() {
        return webView;
    }

    @Nullable
    @Override
    @SuppressLint("setJavaScriptEnabled") // отключение предупреждения о возможных JavaScript ошибках
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_page, container, false);

        // находим и настраиваем ProgressBar для отображения хода загрузки
        progressBar = (ProgressBar) view.findViewById(R.id.fragment_photo_page_progress_bar);
        progressBar.setMax(100);

        webView = (WebView) view.findViewById(R.id.fragment_photo_page_web_viev);

        // включаем поддержку JavaScript для отображения страницы (по умолчанию отключена)
        webView.getSettings().setJavaScriptEnabled(true);

        // включаем zoom, но без кнопок зумирования + и - в углу экрана
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        // WebChromeClient - это вторая точка обратного вызова, для коллбэков из WebView,
        // например, обновление статуса загрузки и другие события.
        // тут обрабатываем изменения процента загрузки, отображая на прогрессбаре,
        // по окончании загрузки закрываем его
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }

            // передаем из WebView в активити заголовок загружаемой страницы и
            // отображаем надпись в ActionBar
            @Override
            public void onReceivedTitle(WebView view, String title) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();

                if (activity != null && activity.getSupportActionBar() != null) {
                    activity.getSupportActionBar().setSubtitle(title);
                }
            }
        });



        // настройка параметров вывода страницы в WebView
        // shouldOverrideUrlLoading - указывает как обрабатывать клик по Url внутри этого WebViev
        // если возвращает true - WebViev его самостоятельно НЕ обрабатывает, предоставляя возможность
        // как-то обработать запрос на загрузку, если false - то элемент самостоятельно открывает
        // страницу по ссылке в том же окне.
        webView.setWebViewClient(new WebViewClient() {
            // Вызовется в API < 24
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // открываем сами
                if (url.toUpperCase().startsWith("HTTP") | url.toUpperCase().startsWith("HTTPS")) {
                    return false;
                }
                //else позволяем открыть ссылку сторонним приложением
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }

            // Вызовется в API >= 24. Обновленная сигнатура метода
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().toUpperCase().startsWith("HTTP") | request.getUrl().toString().toUpperCase().startsWith("HTTPS")) {
                    return false;
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                    startActivity(intent);
                    return true;
                }
            }
        });

        // загружаем картинку по ссылке
        webView.loadUrl(uri.toString());

        return view;
    }
}

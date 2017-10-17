package com.example.konstantin.locator.UI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by Konstantin on 15.07.2017.
 */

public class PhotoPageActivity extends SingleFragmentActivity {

    PhotoPageFragment photoPageFragment;

    @Override
    protected Fragment createFragment() {
        photoPageFragment = PhotoPageFragment.newInstance(getIntent().getData());
        return photoPageFragment;
    }

    // возвращает интент с ссылкой на страницу с фотографией, с открытием PhotoPageActivity
    // Вызывается при клике на InfoWindow на карте
    public static Intent newIntent(Context context, Uri photoPageUri) {
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(photoPageUri);
        return intent;
    }

    @Override
    // обработка нажатия кнопки назад, через обращение к элементу WebView фрагмента
    public void onBackPressed() {
        if (photoPageFragment.getWebView().canGoBack()) {
            photoPageFragment.getWebView().goBack();
        }
        else { super.onBackPressed(); }
    }
}

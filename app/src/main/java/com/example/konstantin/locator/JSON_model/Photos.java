package com.example.konstantin.locator.JSON_model;

import java.util.List;

/**
 * Created by Konstantin on 15.05.2017.
 */

public class Photos {
    public Integer page;
    public Integer pages;
    public Integer perpage;
    public Integer total;
    public List<Photo> photo = null;

    public Photos(Integer page, Integer pages, Integer perpage, Integer total, List<Photo> photo) {
        this.page = page;
        this.pages = pages;
        this.perpage = perpage;
        this.total = total;
        this.photo = photo;
    }
}

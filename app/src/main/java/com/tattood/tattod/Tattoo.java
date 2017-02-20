package com.tattood.tattod;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by eksi on 13/02/17.
 */

public class Tattoo {
    private int owner_id;
    private int tattoo_id;
    private ArrayList<String> tags;
    private String path;
    private Bitmap image;

    public Tattoo(int tid, int oid) {
        tattoo_id = tid;
        owner_id = oid;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap bmp) {
        image = bmp;
    }
}

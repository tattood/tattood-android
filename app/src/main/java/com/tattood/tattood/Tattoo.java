package com.tattood.tattood;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by eksi on 13/02/17.
 */

public class Tattoo implements Serializable {
    public final String owner_id;
    public final String tattoo_id;
    public ArrayList<String> tags;
    public boolean is_private;
//    private String path;
    private Bitmap image;

    public Tattoo(String tid, String oid) {
        tattoo_id = tid;
        owner_id = oid;
        tags = new ArrayList<>();
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap bmp) {
        image = bmp;
    }
}

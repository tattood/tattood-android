package com.tattood.tattood;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by eksi on 13/02/17.
 */

public class Tattoo implements Serializable {
    public String owner_id;
    public final String tattoo_id;
    public ArrayList<String> tags;
    public boolean is_private;
//    private String path;
    public Bitmap image;

    public Tattoo() {
        this(null, null);
    }

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

package com.tattood.tattood;

import android.net.Uri;

/**
 * Created by eksi on 27/04/17.
 */

public class BasicUser {
    public String username;
    public Uri url;
    public BasicUser(String s) {
        this(s, null);
    }
    public BasicUser(String s, String u) {
        username = s;
        url = Uri.parse(u);
    }
}

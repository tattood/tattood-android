package com.tattood.tattood;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by eksi on 26/04/17.
 */

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.example.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;
    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

    @Override
    public boolean onCreate() {
        return super.onCreate();
    }
}

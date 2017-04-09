package com.tattood.tattood;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

/**
 * Created by eksi on 22/02/17.
 */

public class OnListFragmentInteractionListener implements View.OnClickListener {
    private final Context context;
    private final String token;
    public OnListFragmentInteractionListener(Context c, String t) {
        context = c;
        token = t;
    }

    void onListFragmentInteraction(Tattoo item) {
        Intent myIntent = new Intent(context, TattooEditActivity.class);
        myIntent.putExtra("tattoo_id", item.tattoo_id);
        myIntent.putExtra("owner_id", item.owner_id);
        myIntent.putExtra("token", token);
        context.startActivity(myIntent);
    }

    @Override
    public void onClick(View v) {
        Log.d("Tattoo", "Profile Page");
    }
}

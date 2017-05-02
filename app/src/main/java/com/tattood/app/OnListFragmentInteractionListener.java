package com.tattood.app;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

/**
 * Created by eksi on 22/02/17.
 */

public class OnListFragmentInteractionListener implements View.OnClickListener {
    private final Context context;
    public OnListFragmentInteractionListener(Context c) {
        context = c;
    }

    void onListFragmentInteraction(Tattoo item) {
        Intent myIntent = new Intent(context, TattooEditActivity.class);
        myIntent.putExtra("tattoo_id", item.tattoo_id);
        myIntent.putExtra("owner_id", item.owner_id);
        context.startActivity(myIntent);
    }

    @Override
    public void onClick(View v) {
        Log.d("Tattoo", "Profile Page");
    }
}

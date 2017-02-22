package com.tattood.tattod;

import android.util.Log;
import android.view.View;

/**
 * Created by eksi on 22/02/17.
 */

public class OnListFragmentInteractionListener implements View.OnClickListener {
    // TODO: Update argument type and name
    void onListFragmentInteraction(Tattoo item) {
        Log.d("Tattoo", "Clicked");
    }

    @Override
    public void onClick(View v) {
        Log.d("Tattoo", "Clicked");
    }
}

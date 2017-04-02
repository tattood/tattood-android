package com.tattood.tattod;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tattood.tattod.OnListFragmentInteractionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Tattoo} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TattooRecyclerViewAdapter extends RecyclerView.Adapter<TattooRecyclerViewAdapter.ViewHolder>
        implements View.OnClickListener {

    private final Tattoo mValues[];
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    private final RecyclerView mRecyclerView;

    public TattooRecyclerViewAdapter(OnListFragmentInteractionListener listener,
                                     Context context, RecyclerView recyclerView, int count) {
        mValues = new Tattoo[count];
        mListener = listener;
        mContext = context;
        mRecyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tattoo_item, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    private Bitmap getTattooImage(int index) {
        Tattoo t = mValues[index];
        if (t == null || t.getImage() == null) {
            return null;
//            return BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
        }
        return t.getImage();
    }

    public void setTattooImage(int index, Bitmap image) {
        if (mValues[index] == null)
            return;
        mValues[index].setImage(image);
    }

    public void setTattoo(int index, Tattoo t) {
        mValues[index] = t;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.image.setImageBitmap(getTattooImage(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                } else {
                    Log.d("Tattoo", "DIscovery Page");
                    Intent myIntent = new Intent(mContext, TattooActivity.class);
//                    myIntent.putExtra("token", mValues[position].tattoo_id);
                    myIntent.putExtra("tid",   mValues[position].tattoo_id);
                    mContext.startActivity(myIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mRecyclerView.getChildLayoutPosition(v);
        Log.d("Tattoo", "onCLick()");
        Tattoo item = mValues[itemPosition];
        Toast.makeText(mContext, item.toString(), Toast.LENGTH_LONG).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView image;
        public Tattoo mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            image = (ImageView) view.findViewById(R.id.item_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '";
        }
    }

    public void set_data(String token, JSONObject obj) {
        try {
//            JSONArray arr;
            obj = obj.getJSONObject("data");
            Iterator<String> keys = obj.keys();
            while( keys.hasNext() ) {
                String key = keys.next();
                int i = Integer.parseInt(key);
                JSONArray tattooJSON = obj.getJSONArray(key);
                String tattoo_id = tattooJSON.getString(0);
                String owner_id = tattooJSON.getString(1);
                this.setTattoo(i, new Tattoo(tattoo_id, owner_id));
                Server.getTattooImage(mContext, tattoo_id, i, token,
                        new Server.ResponseCallback() {
                            @Override
                            public void run(String id, int i) {
                                try {
                                    String name = id + ".jpg";
                                    FileInputStream stream = mContext.openFileInput(name);
                                    Bitmap img = BitmapFactory.decodeStream(stream);
                                    setTattooImage(i, img);
                                    notifyDataSetChanged();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
}

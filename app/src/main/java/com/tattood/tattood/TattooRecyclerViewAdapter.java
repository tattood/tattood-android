package com.tattood.tattood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Tattoo} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TattooRecyclerViewAdapter extends RecyclerView.Adapter<TattooRecyclerViewAdapter.ViewHolder>
        implements View.OnClickListener {

    public ArrayList<Tattoo> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    private final RecyclerView mRecyclerView;

    public TattooRecyclerViewAdapter(Context context, RecyclerView recyclerView) {
        this(context, recyclerView, null);
    }

    public TattooRecyclerViewAdapter(Context context, RecyclerView recyclerView,
                                     OnListFragmentInteractionListener listener) {
        mValues = null;
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
        if (index >= mValues.size() || mValues.get(index) == null)
            return null;
        return mValues.get(index).getImage();
    }

    public void setTattooImage(int index, Bitmap image) {
        if (index >= mValues.size() || mValues.get(index) == null)
            return;
        mValues.get(index).setImage(image);
    }

    public void setTattoo(int index, Tattoo t) {
        mValues.set(index, t);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.image.setImageBitmap(getTattooImage(position));
        final int pos = holder.getAdapterPosition();
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onListFragmentInteraction(mValues.get(pos));
                } else {
                    Intent myIntent = new Intent(mContext, TattooActivity.class);
                    myIntent.putExtra("tid",   mValues.get(pos).tattoo_id);
                    mContext.startActivity(myIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mValues == null) ? 0 : mValues.size();
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mRecyclerView.getChildLayoutPosition(v);
        Log.d("Tattoo", "onCLick()");
        Tattoo item = mValues.get(itemPosition);
        Toast.makeText(mContext, item.toString(), Toast.LENGTH_LONG).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView image;
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

    public void set_data(JSONObject obj) {
        try {
            obj = obj.getJSONObject("data");
            mValues = new ArrayList<>(obj.length());
            for (int i = 0; i < obj.length(); i++)
                mValues.add(null);
            Iterator<String> keys = obj.keys();
            while( keys.hasNext() ) {
                String key = keys.next();
                final int index = Integer.parseInt(key);
                JSONArray tattooJSON = obj.getJSONArray(key);
                String tattoo_id = tattooJSON.getString(0);
                String owner_id = tattooJSON.getString(1);
                this.setTattoo(index, new Tattoo(tattoo_id, owner_id));
                Server.getTattooImage(mContext, mValues.get(index),
                        new Server.ResponseCallback() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public Tattoo getTattoo(String tid) {
        for (int i = 0; i < mValues.size(); i++)
            if (mValues.get(i).tattoo_id.equals(tid))
                return mValues.get(i);
        return null;
    }

    public void addTattoo(Tattoo t) {
        mValues.add(t);
        setData(mValues);
    }

    public void removeTattoo(Tattoo t) {
        Log.d("REMOVE", String.valueOf(t.tattoo_id));
        for (int i = 0; i < mValues.size(); i++) {
            Log.d("REMOVE", String.valueOf(mValues.get(i).tattoo_id));
            Log.d("REMOVE2", String.valueOf(mValues.get(i) == t));
        }
        mValues.remove(t);
        setData(mValues);
    }

    public ArrayList<Tattoo> getData() {
        return mValues;
    }

    public void setData(ArrayList<Tattoo> data) {
        mValues = data;
        for (int i = 0; i < data.size(); i++)
            setTattooImage(i, data.get(i).image);
        notifyDataSetChanged();
    }
}

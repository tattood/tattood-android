package com.tattood.tattood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

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
public class TattooRecyclerViewAdapter extends RecyclerView.Adapter<TattooRecyclerViewAdapter.ViewHolder> {

    public ArrayList<Tattoo> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;

    public TattooRecyclerViewAdapter(Context context) {
        this(context, null);
    }

    public TattooRecyclerViewAdapter(Context context, OnListFragmentInteractionListener listener) {
        mValues = null;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tattoo_item, parent, false);
        return new ViewHolder(view);
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
        final int pos = holder.getAdapterPosition();
        final Tattoo tattoo = mValues.get(pos);
        if (tattoo != null)
            Server.getTattooImage2(tattoo, holder.image);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onListFragmentInteraction(tattoo);
                } else {
                    Intent myIntent = new Intent(mContext, TattooActivity.class);
                    myIntent.putExtra("tid",   tattoo.tattoo_id);
                    mContext.startActivity(myIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mValues == null) ? 0 : mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final SimpleDraweeView image;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            image = (SimpleDraweeView) view.findViewById(R.id.item_image);
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
            Log.d("DATA", String.valueOf(obj.length()));
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

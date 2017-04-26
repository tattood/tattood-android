package com.tattood.tattood;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    public ArrayList<BasicUser> mValues;
    private final Context mContext;

    public UserRecyclerViewAdapter(Context context) {
        mValues = new ArrayList<>(0);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final int pos = holder.getAdapterPosition();
        final BasicUser user = mValues.get(pos);
        if (user == null)
            return;
        holder.image.setImageURI(user.url);
        holder.label.setText(user.username);
        Log.d("USER", user.username);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(mContext, UserActivity.class);
                myIntent.putExtra("username", user.username);
                mContext.startActivity(myIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final SimpleDraweeView image;
        public final TextView label;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            image = (SimpleDraweeView) view.findViewById(R.id.item_image);
            label = (TextView) view.findViewById(R.id.item_label);
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
                String id = tattooJSON.getString(0);
                String url = tattooJSON.getString(1);
                mValues.set(index, new BasicUser(id, url));
            }
            notifyDataSetChanged();
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<BasicUser> getData() {
        return mValues;
    }

    public void setData(ArrayList<BasicUser> data) {
        mValues = data;
//        for (int i = 0; i < data.size(); i++)
//            setTattooImage(i, data.get(i).image);
        notifyDataSetChanged();
    }
}

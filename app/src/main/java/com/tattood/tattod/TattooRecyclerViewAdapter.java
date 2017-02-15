package com.tattood.tattod;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tattood.tattod.DiscoveryFragment.OnListFragmentInteractionListener;
import com.tattood.tattod.dummy.DummyContent.Tattoo;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Tattoo} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TattooRecyclerViewAdapter extends RecyclerView.Adapter<TattooRecyclerViewAdapter.ViewHolder>
        implements View.OnClickListener {

    private final List<Tattoo> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    private final RecyclerView mRecyclerView;

    public TattooRecyclerViewAdapter(List<Tattoo> items,
                                     OnListFragmentInteractionListener listener,
                                     Context context,
                                     RecyclerView recyclerView) {
        mValues = items;
        mListener = listener;
        mContext = context;
        mRecyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_list, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.image.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mRecyclerView.getChildLayoutPosition(v);
        Log.d("click", "clicked");
        Tattoo item = mValues.get(itemPosition);
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
}

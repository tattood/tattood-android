package com.tattood.tattood;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProfileFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private TattooRecyclerViewAdapter adapter;
    private String source;

    public static ProfileFragment getInstance(String title) {
        ProfileFragment fra = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("source", title);
        fra.setArguments(bundle);
        return fra;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        source = bundle.getString("source");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), 2));
        OnListFragmentInteractionListener listener = null;
        if (source.equals("Public")) {
            listener = new OnListFragmentInteractionListener(getContext());
        } else if (source.equals("Private")) {
            listener = new OnListFragmentInteractionListener(getContext());
        }
        adapter = new TattooRecyclerViewAdapter(mRecyclerView.getContext(), listener);
        mRecyclerView.setAdapter(adapter);
        if (source.equals("Public")) {
            User.getInstance().setPublicView(getContext(), mRecyclerView);
        } else if (source.equals("Private")) {
            User.getInstance().setPrivateView(getContext(), mRecyclerView);
        }
        else if(source.equals("Liked")) {
            User.getInstance().setLikedView(getContext(), mRecyclerView);
        }
        return v;
    }
}

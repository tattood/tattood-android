package com.tattood.tattood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by eksi on 05/04/17.
 */

public class TagItemAdapter extends BaseAdapter implements ListAdapter {
    public ArrayList<TattooTag> list = new ArrayList<>();
    private final Context context;
    private final Tattoo tattoo;

    public TagItemAdapter(Context context, ArrayList<TattooTag> list, Tattoo t) {
        this.list = list;
        this.context = context;
        this.tattoo = t;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tag_edit_layout, parent, false);
        }
        TextView listItemText = (TextView)view.findViewById(R.id.tag_name);
        listItemText.setText(list.get(position).text);
        Button tag_delete = (Button)view.findViewById(R.id.tag_delete);
        tag_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
                tattoo.tags = list;
            }
        });
        return view;
    }
}

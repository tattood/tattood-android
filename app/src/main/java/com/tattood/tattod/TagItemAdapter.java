package com.tattood.tattod;

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
    public ArrayList<String> list = new ArrayList<>();
    private Context context;
    private Tattoo tattoo;
    private String token;

    public TagItemAdapter(Context context, String token, ArrayList<String> list, Tattoo t) {
        this.list = list;
        this.context = context;
        this.token = token;
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
//        return ((Tattoo)(list.get(pos))).tattoo_id;
        return pos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tag_edit_layout, parent, false);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.tag_name);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button tag_delete = (Button)view.findViewById(R.id.tag_delete);

        tag_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Server.editTag();
                list.remove(position); //or some other task
                notifyDataSetChanged();
                tattoo.tags = list;
                Server.updateTattoo(context, token, tattoo);
            }
        });

        return view;
    }
}

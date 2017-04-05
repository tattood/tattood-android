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
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;



    public TagItemAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
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
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tag_edit_layout, null);
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
            }
        });

        return view;
    }
}

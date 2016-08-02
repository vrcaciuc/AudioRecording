package com.example.vladcaciuc.audiorecording;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by vladcaciuc on 7/31/16.
 */
public class ItemsAdapter extends ArrayAdapter<ItemModel> {

    private ArrayList<ItemModel> myItems;
    private static LayoutInflater inflater=null;

    public ItemsAdapter(ArrayList<ItemModel> itemList, Context context) {
        super(context, 0, itemList);
        this.myItems = itemList;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (myItems != null)
            return myItems.size();
        return 0;
    }

    @Override
    public ItemModel getItem(int position) {
        return super.getItem(position);
    }

    public class ViewHolder{
        TextView tvName;
    }

    @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View v= convertView;
        ItemModel item = getItem(position);
        if (v == null) {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.list_item,parent,false);
            holder.tvName = (TextView) v.findViewById(R.id.tv_record_name);
            v.setTag(holder);
        } else
            holder = (ViewHolder) v.getTag();

        holder.tvName.setText(item.getName());

        return v;
    }
}

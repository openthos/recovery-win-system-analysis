package com.example.junzhen.systemrecovery;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xly on 17-5-4.
 */

public class PartitionsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mList;
    private ArrayList<String> deny_part;
    private int mSelectPos = -1;
    private String mDevPart;

    public PartitionsAdapter(Context context, ArrayList<String> list, ArrayList<String> deny_pos) {
        mContext = context;
        mList = list;
        deny_part = deny_pos;
    }

    public void setmSelectPos(int pos, String devPart) {
        mSelectPos = pos;
        mDevPart = devPart;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        int deny_flag = 0;
        if (convertView == null)  {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item1, viewGroup, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.name.setText(mList.get(i));



            if (mSelectPos == i) {
                viewHolder.name.setTextColor(Color.BLACK);
                viewHolder.name.setBackgroundResource(R.color.gray);
            } else {
                viewHolder.name.setTextColor(Color.BLACK);
                viewHolder.name.setBackgroundResource(R.color.white);
            }

        return convertView;
    }

    public static class ViewHolder {
        public TextView name;
        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.itemText1);
        }
    }
}

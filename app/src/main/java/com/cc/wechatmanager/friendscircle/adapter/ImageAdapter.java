package com.cc.wechatmanager.friendscircle.adapter;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cc.wechatmanager.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> data;

    public ImageAdapter(Context context, List<String> list) {
        this.context = context;
        data = new ArrayList<>();
        if (list != null) {
            data.addAll(list);
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_image, null);
            holder.image = convertView.findViewById(R.id.images);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(context).load(getItem(position)).into(holder.image);
        return convertView;
    }

    private static class ViewHolder {
        private ImageView image;
    }
}

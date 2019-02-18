package com.cc.wechatmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.cc.core.data.db.model.Fridend;
import java.util.List;

public class ContactsAdapter extends BaseAdapter {
    List<Fridend> contacts;
    public ContactsAdapter(List<Fridend> contacts) {
        this.contacts = contacts;
    }

    @Override public int getCount() {
        return contacts == null ? 0 : contacts.size();
    }

    @Override public Fridend getItem(int position) {
        if (contacts == null || position >= contacts.size()) {
            return null;
        } else {
            return contacts.get(position);
        }
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        }

        Fridend item = getItem(position);
        if (item == null) {
            return convertView;
        }

        ImageView avatar = convertView.findViewById(R.id.avatar);
        Glide.with(convertView.getContext()).load(item.getAvatar()).into(avatar);
        TextView nameView = convertView.findViewById(R.id.name);
        nameView.setText(item.getNickname());
        TextView wechatView = convertView.findViewById(R.id.wechatId);
        wechatView.setText(item.getWechatId());
        return convertView;
    }
}

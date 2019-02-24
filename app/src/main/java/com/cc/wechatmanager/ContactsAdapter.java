package com.cc.wechatmanager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.cc.core.wechat.model.user.Friend;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    List<Friend> contacts;
    public ContactsAdapter(List<Friend> contacts) {
        this.contacts = contacts;
    }

    public Friend getItem(int position) {
        if (contacts == null || position >= contacts.size()) {
            return null;
        } else {
            return contacts.get(position);
        }
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        return new ContactsAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_contact, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder viewHolder, int position) {
        viewHolder.bindView(getItem(position));
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView nameView;
        TextView wechatView;
        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            nameView = itemView.findViewById(R.id.name);
            wechatView = itemView.findViewById(R.id.wechatId);
        }

        void bindView(Friend friend) {
            Glide.with(avatar.getContext()).load(friend.getAvatar()).into(avatar);
            nameView.setText(friend.getNickname());
            wechatView.setText(friend.getWechatId());
        }
    }
}

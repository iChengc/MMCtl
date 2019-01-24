package com.cc.wechatmanager.rpc;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Message implements Parcelable {
    @SerializedName("time")
    private long time;
    @SerializedName("type")
    private String type;
    @SerializedName("data")
    private String data;

    public Message(){}

    protected Message(Parcel in) {
        time = in.readLong();
        type = in.readString();
        data = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeString(type);
        dest.writeString(data);
    }

    public static Message from(String msg) {
        Gson gson = new Gson();
        return gson.fromJson(msg, Message.class);
    }

    public static class MessageType {
        public static final String COMMAND = "command";
    }
}

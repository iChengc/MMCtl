package com.cc.core.rpc;

import android.os.Parcel;
import android.os.Parcelable;

import com.cc.core.actions.Actions;
import com.cc.core.utils.StrUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class RpcArgs implements Parcelable {
    @SerializedName("time")
    private long time;
    @SerializedName("id")
    private String id;
    @SerializedName("type")
    private String type;
    @SerializedName("data")
    private String data;

    public RpcArgs(){}

    protected RpcArgs(Parcel in) {
        time = in.readLong();
        id = in.toString();
        type = in.readString();
        data = in.readString();
    }

    public static final Creator<RpcArgs> CREATOR = new Creator<RpcArgs>() {
        @Override
        public RpcArgs createFromParcel(Parcel in) {
            return new RpcArgs(in);
        }

        @Override
        public RpcArgs[] newArray(int size) {
            return new RpcArgs[size];
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(data);
    }

    public static RpcArgs from(String msg) {
        return StrUtils.fromJson(msg, RpcArgs.class);
    }

    public static class CallType {
        public static final String EXECUTE_ACTION = "command";
        public static final String EXECUTE_DB = "db";
    }

    public static RpcArgs newMessage(Actions.RawAction action) {
        RpcArgs rpcArgs = new RpcArgs();
        rpcArgs.time = System.currentTimeMillis();
        rpcArgs.type = CallType.EXECUTE_ACTION;
        rpcArgs.data = StrUtils.toJson(action);
        rpcArgs.id = action.getActionId();

        return rpcArgs;
    }
}

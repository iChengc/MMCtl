package com.cc.core.command;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Command {
    @SerializedName("id")
    private String id;
    @SerializedName("key")
    private String key;
    @SerializedName("args")
    private List<? extends Object> args;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<? extends Object> getArgs() {
        return args;
    }

    public void setArgs(List<? extends Object> args) {
        this.args = args;
    }
}

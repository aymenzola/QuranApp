package com.app.dz.quranapp.Api;

import com.google.gson.annotations.SerializedName;

public class DefaultResponse {

    @SerializedName("error")
    private boolean err;

    @SerializedName("message")
    private String msg;

    @SerializedName("type")
    private String type;

    public DefaultResponse() {
    }

    public DefaultResponse(boolean err, String msg, String type) {
        this.err = err;
        this.msg = msg;
        this.type = type;
    }

    public boolean isErr() {
        return err;
    }

    public String getMsg() {
        return msg;
    }

    public String getType() {
        return type;
    }
}

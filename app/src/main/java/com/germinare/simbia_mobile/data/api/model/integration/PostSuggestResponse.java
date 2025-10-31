package com.germinare.simbia_mobile.data.api.model.integration;

import java.util.List;

public class PostSuggestResponse {

    private String msg;
    private List<Long> data;

    public String getMsg() {
        return msg;
    }

    public List<Long> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "PostSuggestResponse{" +
                "msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}

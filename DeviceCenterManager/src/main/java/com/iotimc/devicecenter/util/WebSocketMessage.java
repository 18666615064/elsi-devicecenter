package com.iotimc.devicecenter.util;

import com.alibaba.fastjson.JSONObject;

public class WebSocketMessage {
    private String event;
    private Object content;

    public WebSocketMessage(String event, Object content) {
        this.event = event;
        this.content = content;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        JSONObject obj = (JSONObject) JSONObject.parse("{\"event\":\"" + this.event + "\"}");
        obj.put("content", this.content);
        return obj.toJSONString();
    }
}

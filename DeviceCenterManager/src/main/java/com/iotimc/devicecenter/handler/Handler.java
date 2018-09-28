package com.iotimc.devicecenter.handler;

import com.alibaba.fastjson.JSONObject;

public interface Handler {
    public boolean filter(JSONObject data);
    public void handle(JSONObject data);
}

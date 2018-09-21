package com.iotimc.devicecenter.handler;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HandlerFactory {
    private static Map<Integer, Handler> handlerMap = new HashMap<Integer, Handler>();
    public static void register(Handler handler) {
        handlerMap.put(handler.hashCode(), handler);
    }

    public static void remove(Handler handler) {
        if(handlerMap.containsKey(handler.hashCode()))
            handlerMap.remove(handler.hashCode());
    }

    public static void handleAll(JSONObject data) {
        for(Map.Entry<Integer, Handler> item : handlerMap.entrySet()) {
            Handler handler = item.getValue();
            try{
                if(handler.filter(data))handler.handle(data);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}

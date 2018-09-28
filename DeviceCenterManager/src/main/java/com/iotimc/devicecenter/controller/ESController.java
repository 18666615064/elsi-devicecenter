package com.iotimc.devicecenter.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.util.RedisUtil;
import com.iotimc.devicecenter.util.WebSocketMessage;
import com.iotimc.elsi.auth.annotation.NoneAuthorize;
import com.iotimc.elsi.auth.bean.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

@RestController
@RequestMapping("/es")
@NoneAuthorize
@Slf4j
public class ESController {
    private static Map<String, JSONObject> devicelist = new HashMap<>();

    @Autowired
    private RedisUtil redisUtil_prv;

    private static RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        redisUtil = redisUtil_prv;
    }

    @RequestMapping(value = "listen", method= RequestMethod.GET, produces = "text/event-stream;charset=UTF-8")
    public void listen(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="events", required = false)String eventstr, @RequestParam("imei")String imei) {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("utf-8");
        try {
            JSONObject device = getDevicelist().get(imei);
            PrintWriter out = response.getWriter();
            if(device == null) {
                // 新添加
                device = new com.alibaba.fastjson.JSONObject();
                device.put("messagelist", new JSONArray());
                getDevicelist().put(imei, device);
            }
            JSONArray events = null;
            if(eventstr == null || eventstr.equals("")) {
                events = new JSONArray();
            } else {
                events = (JSONArray) JSONArray.parse(eventstr);
            }
            device.put("time", new Date().getTime());
            device.put("events", events);
            // 遍历消息列表发回前台
            for(Object message : device.getJSONArray("messagelist")) {
                WebSocketMessage messageobj = (WebSocketMessage)message;
                out.println("id:" + System.currentTimeMillis());
                out.println("data:" + JSONObject.toJSONString(messageobj.getContent()));
                out.println("event:" + messageobj.getEvent());
                out.println();
            }
            device.getJSONArray("messagelist").clear();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void send(String imei, WebSocketMessage message) {
        send(new String[]{imei}, message);
    }

    public static void send(String[] imeis, WebSocketMessage message) {
        try {
            String event = message.getEvent();
            if (imeis[0].equals("all")) {
                imeis = getDevicelist().keySet().toArray(imeis);
            }
            for (String tk : imeis) {
                JSONObject item = getDevicelist().get(tk);
                if (item != null && item.getJSONArray("events").indexOf(event) > -1) {
                    item.getJSONArray("messagelist").add(message);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, JSONObject> getDevicelist() {
        return devicelist;
    }
}
class LifeCycle implements Runnable {

    @Override
    public void run() {
        try {
            while(true) {
                //定时处理消息列表的数据
                long now = new Date().getTime();
                Iterator<Map.Entry<String, JSONObject>> iters = ESController.getDevicelist().entrySet().iterator();
                while(iters.hasNext()) {
                    Map.Entry<String, JSONObject> item = iters.next();
                    long time = item.getValue().getLong("time");
                    if(now - time > 5 * 60 * 1000) {
                        // 超时移除
                        iters.remove();
                    }
                }
                Thread.sleep(60 * 1000);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

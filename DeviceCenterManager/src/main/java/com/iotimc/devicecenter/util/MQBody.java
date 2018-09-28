package com.iotimc.devicecenter.util;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class MQBody implements Serializable{
    private String event;
    private String imei;
    private JSONObject data;
    private Integer queue;
    private boolean persistent = false;
    private boolean istopic = false;

    public MQBody(Integer queue) {
        this.queue = queue;
    }

    public MQBody(Integer queue, String event, String imei) {
        this.queue = queue;
        this.event = event;
        this.imei = imei;
    }

    public MQBody(Integer topic, String event, String imei, JSONObject data) {
        this(topic, event, imei);
        this.data = data;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public Integer getQueue() {
        return queue;
    }

    public void setQueue(Integer queue) {
        this.queue = queue;
    }

    public boolean getPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public static MQBody parse(String body) {
        return JSONObject.toJavaObject((JSONObject)JSONObject.parse(body), MQBody.class);
    }

    public boolean getIstopic() {
        return istopic;
    }

    public void setIstopic(boolean istopic) {
        this.istopic = istopic;
    }
}

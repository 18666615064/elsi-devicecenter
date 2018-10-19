package com.iotimc.devicecenter.service;

import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.domain.DevSensorlogEntity;

import java.util.List;
import java.util.Map;

public interface SensorlogService {
    List<Map> getTop(String imei, int size, String name, String value);
    List<Map> getLast(String imei, int size, String name);
    List<Map> getLastGroup(String imei, int size, String name, String starttime, String endtime);
    List<Map> getSensorlog(String starttime, String endtime, String imei, String name);
    String save(JSONObject data);
}

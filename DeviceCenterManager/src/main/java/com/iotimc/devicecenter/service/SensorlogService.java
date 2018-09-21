package com.iotimc.devicecenter.service;

import com.iotimc.devicecenter.domain.DevSensorlogEntity;

import java.util.List;
import java.util.Map;

public interface SensorlogService {
    List<Map> getTop(String imei, int size, String name);
    List<Map> getLast(String imei, int size, String name);
}

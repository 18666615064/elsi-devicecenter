package com.iotimc.devicecenter.service;

import java.util.List;
import java.util.Map;

public interface LoginlogService {
    public List<Map> getTop(String starttime, String endtime, String imei, Integer size);
}

package com.iotimc.devicecenter.service;

import com.iotimc.devicecenter.domain.DevControllogEntity;

import java.util.List;

public interface ControllogService {
    public List<DevControllogEntity> getTop(String imei, int size);
}

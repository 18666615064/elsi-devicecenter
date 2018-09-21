package com.iotimc.devicecenter.service.impl;

import com.iotimc.devicecenter.dao.DevControllogRepository;
import com.iotimc.devicecenter.domain.DevControllogEntity;
import com.iotimc.devicecenter.service.ControllogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ControlServiceImpl implements ControllogService {
    @Autowired
    private DevControllogRepository devControllogRepository;

    @Override
    public List<DevControllogEntity> getTop(String imei, int size) {
        return devControllogRepository.getTop(imei, size);
    }
}

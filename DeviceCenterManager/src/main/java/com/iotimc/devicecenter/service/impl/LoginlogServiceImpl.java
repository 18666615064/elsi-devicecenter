package com.iotimc.devicecenter.service.impl;

import com.iotimc.devicecenter.dao.DevLoginlogRepository;
import com.iotimc.devicecenter.service.LoginlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LoginlogServiceImpl implements LoginlogService {

    @Autowired
    private DevLoginlogRepository devLoginlogRepository;

    @Override
    public List<Map> getTop(String starttime, String endtime, String imei, Integer size) {
        if(size == null) size = 10;
        return devLoginlogRepository.getList(starttime, endtime, imei, size);
    }
}

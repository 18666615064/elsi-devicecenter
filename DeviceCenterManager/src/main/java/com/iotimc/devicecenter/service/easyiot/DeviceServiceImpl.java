package com.iotimc.devicecenter.service.easyiot;

import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.service.DeviceService;
import org.springframework.stereotype.Service;

//TODO: 未实现easyiot的接口
@Service("easyiot")
public class DeviceServiceImpl implements DeviceService {
    @Override
    public String addDevice(JSONObject data) {
        return "-1";
    }

    @Override
    public String delDevice(Integer id) {
        return "-1";
    }

    @Override
    public String modDevice(JSONObject data) {
        return "-1";
    }

    @Override
    public String send(JSONObject data) {
        return null;
    }

    @Override
    public String syncSend(JSONObject data) {
        return null;
    }

    @Override
    public String read(JSONObject data) {
        return null;
    }

    @Override
    public String syncRead(JSONObject data) {
        return null;
    }

    @Override
    public String getStatus(String imei, String platformid) {
        return null;
    }
}

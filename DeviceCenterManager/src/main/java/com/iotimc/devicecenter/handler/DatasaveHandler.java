package com.iotimc.devicecenter.handler;

import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.dao.DevLoginlogRepository;
import com.iotimc.devicecenter.dao.DevSensorlogRepository;
import com.iotimc.devicecenter.domain.*;
import com.iotimc.devicecenter.listener.ConfigListener;
import com.iotimc.devicecenter.listener.DeviceListener;
import com.iotimc.devicecenter.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@Service
@Slf4j
public class DatasaveHandler implements Handler{
    @Autowired
    private MQUtil mqUtil;

    @Autowired
    private DevSensorlogRepository devSensorlogRepository;

    @Autowired
    private DevLoginlogRepository loginlogRepository;

    private ScriptEngineManager manager = new ScriptEngineManager();

    private ScriptEngine engine = manager.getEngineByName("javascript");

    @Value("${config.es.data-event}")
    private static String esDataEvent;

    @Value("${config.es.login-event}")
    private static String esLoginEvent;

    @PostConstruct
    public void init() {
        HandlerFactory.register(this);
    }

    @Override
    public boolean filter(JSONObject data) {
        return data.getInteger("type") == 1 || data.getInteger("type") == 2;
    }

    @Override
    public void handle(JSONObject data) {
        log.info("接收到传感器数据: {}", data.toJSONString());
        DeviceCache device = DeviceListener.getDeviceByPlatformid(data.getInteger("dev_id"));
        if (device == null) {
            log.error("非法设备接入：{}", data.getString("dev_id"));
            return;
        }
        if (data.getInteger("type") == 1) {
            // 数据录入
            String value = data.getString("value");
            String dsid = data.getString("ds_id");
            DevProductdtlEntity prop = ConfigListener.getPropByDevImeiDsid(device.getImei(), Integer.parseInt(dsid.split("_")[0]),
                    Integer.parseInt(dsid.split("_")[2]), Byte.parseByte(dsid.split("_")[1]));
            if(prop == null) {
                log.error("保存设备[{}]属性[{}]值失败，找不到属性映射配置", device.getImei(), dsid);
                return;
            }
            value = Tool.reciveConvert(value, prop.getType(), prop.getIshex());
            // 针对数据内容进行纠偏
            if (prop.getCorrect() != null) {
                String correct = prop.getCorrect();
                if(prop.getType().equalsIgnoreCase("string")) {
                    correct = correct.replaceAll("\\{\\{value\\}\\}", "\"" + value + "\"");
                } else {
                    correct = correct.replaceAll("\\{\\{value\\}\\}", value);
                }
                try {
                    value = String.valueOf(engine.eval(correct));
                } catch(Exception ex) {}
            }
            DevSensorlogEntity entity = new DevSensorlogEntity();
            entity.setCretime(Tool.long2timestamp(data.getLong("at")));
            entity.setDevicefk(device.getId());
            entity.setDsid(dsid);
            entity.setImei(device.getImei());
            entity.setName(prop.getName());
            entity.setProductdtlfk(prop.getId());
            entity.setValue(value);
            devSensorlogRepository.save(entity);
            MQBody body = new MQBody(MQUtil.DATATOPIC, "", device.getImei());
            body.setData((JSONObject) JSONObject.toJSON(entity));
            body.setPersistent(true);
            body.setIstopic(true);
            mqUtil.send(body);
            //发送es
            WebSocketMessage message = new WebSocketMessage(esDataEvent, ((JSONObject)JSONObject.toJSON(entity)));
            WebsocketUtil.send(device.getImei(), message);
        } else {
            // 上下线数据
            DevLoginlogEntity entity = new DevLoginlogEntity();
            entity.setCretime(Tool.long2timestamp(data.getLong("at")));
            entity.setDevicefk(device.getId());
            entity.setImei(device.getImei());
            entity.setType(data.getByte("status"));
            loginlogRepository.save(entity);
            MQBody body = new MQBody(MQUtil.LOGINTOPIC, "", device.getImei());
            body.setData((JSONObject) JSONObject.toJSON(entity));
            body.setPersistent(true);
            body.setIstopic(true);
            mqUtil.send(body);
            //发送es
            WebSocketMessage message = new WebSocketMessage(esLoginEvent, body.getData());
            WebsocketUtil.send(device.getImei(), message);
        }
        // 同时发送mq消息
    }
}

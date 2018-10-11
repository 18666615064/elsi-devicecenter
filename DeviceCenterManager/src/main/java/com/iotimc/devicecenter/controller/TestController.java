package com.iotimc.devicecenter.controller;

import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.domain.*;
import com.iotimc.devicecenter.listener.ConfigListener;
import com.iotimc.devicecenter.listener.DeviceListener;
import com.iotimc.devicecenter.util.*;
import com.iotimc.elsi.auth.annotation.NoneAuthorize;
import com.iotimc.elsi.msg.common.HandleEntitySuccessMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@NoneAuthorize
public class TestController {
    @Autowired
    private MQUtil mqUtil;

    @RequestMapping(value = "/getdevicebyimei", method = RequestMethod.GET)
    public ResponseEntity<DeviceCache> getdevicebyImei(@RequestParam("imei") String imei) {
        return ResponseEntity.ok(DeviceListener.getDeviceByImei(imei));
    }

    @RequestMapping(value = "/getdevicebyid", method = RequestMethod.GET)
    public ResponseEntity<DeviceCache> getdevicebyId(@RequestParam("id") Integer id) {
        return ResponseEntity.ok(DeviceListener.getDeviceById(id));
    }

    @RequestMapping(value = "/getdevicebyplatformid", method = RequestMethod.GET)
    public ResponseEntity<DeviceCache> getdevicebyPlatformid(@RequestParam("id") Integer id) {
        return ResponseEntity.ok(DeviceListener.getDeviceByPlatformid(id));
    }

    @RequestMapping(value = "/getCompanyByDevid", method = RequestMethod.GET)
    public ResponseEntity<CompanyConfig> getCompanyByDevid(@RequestParam("id") Integer id) {
        return ResponseEntity.ok(ConfigListener.getCompanyByDevId(id));
    }

    @RequestMapping(value = "/getProductByDevid", method = RequestMethod.GET)
    public ResponseEntity<ProductConfig> getProductByDevid(@RequestParam("id") Integer id) {
        return ResponseEntity.ok(ConfigListener.getProductByDevId(id));
    }

    @RequestMapping(value = "/getProp", method = RequestMethod.GET)
    public ResponseEntity<DevProductdtlEntity> getProp(@RequestParam("id") Integer id, @RequestParam("name") String name) {
        return ResponseEntity.ok(ConfigListener.getPropByDevIdName(id, name));
    }

    @RequestMapping(value = "/sendmq", method = RequestMethod.GET)
    public ResponseEntity<HandleEntitySuccessMsg> sendMQ() {
        MQBody body = new MQBody(MQUtil.DATATOPIC, "", "testimei");
        DevDeviceEntity entity = new DevDeviceEntity();
        entity.setCode("1");
        entity.setCompanyfk(1);
        entity.setId(1);
        entity.setImei("testimei");
        entity.setStatus("P");
        JSONObject data = (JSONObject) JSONObject.toJSON(entity);
        //body.setPersistent(true);
        body.setData(data);
        body.setIstopic(true);
        mqUtil.send(body);
        return ResponseEntity.ok(new HandleEntitySuccessMsg("1", "success"));
    }

    @RequestMapping(value = "/sendws", method = RequestMethod.GET)
    public ResponseEntity<HandleEntitySuccessMsg> sendWS(@RequestParam("value")String value, @RequestParam("name")String name) {
        DevSensorlogEntity entity = new DevSensorlogEntity();
        entity.setCretime(Tool.long2timestamp(1537431316000L));
        entity.setDevicefk(4);
        entity.setDsid("31000_0_41005");
        entity.setImei("868194030006524");
        entity.setName(name);
        entity.setProductdtlfk(41);
        entity.setValue(value);
        WebSocketMessage message = new WebSocketMessage("dataLog", ((JSONObject)JSONObject.toJSON(entity)));
        WebsocketUtil.send("868194030006524", message);
        return ResponseEntity.ok(new HandleEntitySuccessMsg("0"));
    }
}

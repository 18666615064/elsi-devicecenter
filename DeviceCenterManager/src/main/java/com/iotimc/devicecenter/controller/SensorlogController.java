package com.iotimc.devicecenter.controller;

import com.iotimc.devicecenter.domain.DevSensorlogEntity;
import com.iotimc.devicecenter.service.SensorlogService;
import com.iotimc.elsi.auth.annotation.NoneAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@NoneAuthorize
@RestController
@RequestMapping("/sensor")
public class SensorlogController {
    @Autowired
    private SensorlogService sensorlogService;

    /**
     * 根据时间段获取相应设备的相应属性的读数数据
     */
    public void getSensorlog() {}

    /**
     * 获取读数数据统计信息
     */
    public void getSensorlogCount() {}

    /**
     * 获取最新的n组读数数据
     */
    @RequestMapping(value = "/top", method = RequestMethod.GET)
    public List<Map> getTopSensorlog(@RequestParam(value = "size", required = false) Integer size, @RequestParam("imei")String imei, @RequestParam(value = "name")String name) {
        size = size == null? 1 : size;
        return sensorlogService.getTop(imei, size, name);
    }

    @RequestMapping(value = "/last", method = RequestMethod.GET)
    public List<Map> getLastSensorlog(@RequestParam(value = "size", required = false) Integer size, @RequestParam("imei")String imei, @RequestParam(value = "name")String name) {
        size = size == null? 1 : size;
        return sensorlogService.getLast(imei, size, name);
    }

    /**
     * 获取时间段内登录信息
     */
    public void getLoginlog() {

    }
}

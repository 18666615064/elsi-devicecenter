package com.iotimc.devicecenter.controller;

import com.iotimc.devicecenter.domain.DevControllogEntity;
import com.iotimc.devicecenter.service.ControllogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/controllog")
public class ControllogController {
    @Autowired
    private ControllogService controllogService;

    /**
     * 获取前n条控制记录
     * @param size
     */
    @RequestMapping(value = "/top", method = RequestMethod.GET)
    public List<DevControllogEntity> getTopControllog(@RequestParam(value = "size", required = false) Integer size, @RequestParam("imei")String imei) {
        size = size == null? 3 : size;
        return controllogService.getTop(imei, size);
    }
}

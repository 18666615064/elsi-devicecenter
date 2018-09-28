package com.iotimc.devicecenter.controller;

import com.iotimc.devicecenter.domain.DevDeviceEntity;
import com.iotimc.devicecenter.domain.DeviceCache;
import com.iotimc.devicecenter.listener.DeviceListener;
import com.iotimc.elsi.auth.annotation.NoneAuthorize;
import com.iotimc.elsi.msg.common.HandleEntitySuccessMsg;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@NoneAuthorize
@RestController
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping(value = "/getdevice/{imei}", method = RequestMethod.GET)
    public ResponseEntity<HandleEntitySuccessMsg> getDevice(@PathVariable("imei") String imei, HttpServletRequest request, HttpServletResponse response) {
        DeviceCache entity = DeviceListener.getDeviceByImei(imei);
        if(entity == null)
            return ResponseEntity.ok(new HandleEntitySuccessMsg("获取信息失败", "-1"));
        try {
            request.getRequestDispatcher("/device/info/" + entity.getId()).forward(request, response);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 通过imei获取设备对应模板页面
     * @param imei
     */
    @RequestMapping(value = "/gettemplate/{imei}", method = RequestMethod.GET)
    public void getTemplate(@PathVariable("imei")String imei) {}


}

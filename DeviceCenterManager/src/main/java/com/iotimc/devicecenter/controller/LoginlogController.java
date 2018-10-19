package com.iotimc.devicecenter.controller;

import com.iotimc.devicecenter.service.LoginlogService;
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
@RequestMapping("/loginlog")
public class LoginlogController {
    @Autowired
    private LoginlogService loginlogService;

    @RequestMapping(value = "/top", method = RequestMethod.GET)
    public List<Map> getTopLoginlog(@RequestParam(name="size", required = false) Integer size,
                               @RequestParam(name="imei") String imei,
                               @RequestParam(name="starttime", required = false) String starttime,
                               @RequestParam(name="endtime", required = false) String endtime) {
        return loginlogService.getTop(starttime, endtime, imei, size);
    }
}

package com.iotimc.devicecenter.controller;

import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.util.Tool;
import org.springframework.web.bind.annotation.*;

/**
 * 模板配置
 */
@RestController
@RequestMapping("/template")
public class TemplateController {
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public void add(@RequestBody JSONObject data) {
        String[] empty = Tool.isBlanks(data, new String[] {"html"});

    }

    @RequestMapping(value = "/mod", method = RequestMethod.PUT)
    public void mod(@RequestBody JSONObject data) {

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void del(@PathVariable("id")Integer id) {

    }

    @RequestMapping(value = "/bydeviceid", method = RequestMethod.GET)
    public void byDeviceid(@RequestParam("id")Integer id) {

    }

    @RequestMapping(value = "/byimei", method = RequestMethod.GET)
    public void byImei(@RequestParam("imei")String imei) {

    }

}

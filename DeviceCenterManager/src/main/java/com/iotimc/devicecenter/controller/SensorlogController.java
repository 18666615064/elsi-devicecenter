package com.iotimc.devicecenter.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.domain.DevProductdtlEntity;
import com.iotimc.devicecenter.listener.ConfigListener;
import com.iotimc.devicecenter.service.SensorlogService;
import com.iotimc.devicecenter.util.Tool;
import com.iotimc.elsi.auth.annotation.NoneAuthorize;
import com.iotimc.elsi.msg.common.HandleEntitySuccessMsg;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @RequestMapping(value = "/sensorlog", method = RequestMethod.GET)
    public List<Map> getSensorlog(@RequestParam("starttime") String starttime, @RequestParam("endtime") String endtime, @RequestParam("imei") String imei, @RequestParam(value = "name", required = false) String name) {
        return sensorlogService.getSensorlog(starttime, endtime, imei, name);
    }

    /**
     * 获取读数数据统计信息
     */
    public void getSensorlogCount() {}

    /**
     * 获取最新的n组读数数据
     */
    @RequestMapping(value = "/top", method = RequestMethod.GET)
    public List<Map> getTopSensorlog(@RequestParam(value = "size", required = false) Integer size, @RequestParam("imei")String imei, @RequestParam(value = "name")String name, @RequestParam(required = false, value = "value")String value) {
        size = size == null? 1 : size;
        return sensorlogService.getTop(imei, size, name, value);
    }

    @RequestMapping(value = "/last", method = RequestMethod.GET)
    public List<Map> getLastSensorlog(@RequestParam(value = "size", required = false) Integer size, @RequestParam("imei")String imei, @RequestParam(value = "name")String name) {
        size = size == null? 1 : size;
        return sensorlogService.getLast(imei, size, name);
    }

    /**
     *
     * @param size 数据组数
     * @param imei 设备imei
     * @param name 分组依据
     * @return
     */
    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public List<Map> getLastSensorlogGroup(@RequestParam(value = "size", required = false) Integer size,
                                           @RequestParam("imei") String imei,
                                           @RequestParam(value = "name") String name,
                                           @RequestParam(value = "starttime", required = false) String starttime,
                                           @RequestParam(value = "endtime", required = false) String endtime) {
        size = size == null ? 10 : size;
        // 开始时间大于结束时间直接返回空
        if(!StringUtils.isBlank(starttime) && !StringUtils.isBlank(endtime) && starttime.compareTo(endtime) < 0) return new ArrayList<>();
        return sensorlogService.getLastGroup(imei, size, name, starttime, endtime);
    }

    /**
     * 保存传感器值
     * @param data
     * 参数
     * {
     *       "devicefk": 设备id,
     *      "props":[{
     *          "id": "数据id", //可不传,如果有id则为修改数据,如果没有id则为创建数据
     *          // "devicefk": 设备id,
     *          "name": "属性名",
     *          "value": "值",
     *          "cretime": 创建时间戳
     *       }]
     *  }
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<HandleEntitySuccessMsg> save(@RequestBody JSONObject data) {
        if(StringUtils.isBlank(data.getString("devicefk"))) return ResponseEntity.ok(new HandleEntitySuccessMsg("保存失败,设备id为空", "-1"));
        String[] checklist = new String[] {"name", "value", "cretime"};
        JSONArray props = data.getJSONArray("props");
        if(props == null || props.isEmpty()) return ResponseEntity.ok(new HandleEntitySuccessMsg("保存失败，未提供需要保存的属性值", "-1"));
        int idx = 0;
        for(Object prop : props) {
            idx++;
            JSONObject item = (JSONObject) prop;
            String[] empty = Tool.isBlanks(item, checklist);
            if(empty.length != 0)
                return ResponseEntity.ok(new HandleEntitySuccessMsg("保存失败，第[" + idx + "]个属性以下值为空[" + Tool.joinString(empty) + "]", "-1"));
            DevProductdtlEntity dtl = ConfigListener.getPropByDevIdName(data.getInteger("devicefk"), item.getString("name"));
            if(dtl == null) return ResponseEntity.ok(new HandleEntitySuccessMsg("保存失败，属性[" + idx + "]当前设备无[" + item.getString("name") + "]属性"));
            else if(dtl.getEditable() == 0) return ResponseEntity.ok(new HandleEntitySuccessMsg("保存失败，第[" + idx + "]个属性中[" + dtl.getName() + "]不允许写入", "-1"));
        }
        String result = sensorlogService.save(data);
        return ResponseEntity.ok(result.startsWith("[")?new HandleEntitySuccessMsg("保存成功", result):new HandleEntitySuccessMsg("保存失败：" + result, "-1"));
    }
}

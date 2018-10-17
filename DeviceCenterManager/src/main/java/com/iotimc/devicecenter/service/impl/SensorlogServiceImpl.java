package com.iotimc.devicecenter.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.dao.DevLoginlogRepository;
import com.iotimc.devicecenter.dao.DevSensorlogRepository;
import com.iotimc.devicecenter.domain.DevProductdtlEntity;
import com.iotimc.devicecenter.domain.DevSensorlogEntity;
import com.iotimc.devicecenter.domain.DeviceCache;
import com.iotimc.devicecenter.listener.ConfigListener;
import com.iotimc.devicecenter.listener.DeviceListener;
import com.iotimc.devicecenter.service.SensorlogService;
import com.iotimc.devicecenter.util.Tool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class SensorlogServiceImpl implements SensorlogService {
    @Autowired
    private DevSensorlogRepository devSensorlogRepository;

    @Autowired
    private DevLoginlogRepository devLoginlogRepository;

    @Override
    public List<Map> getTop(String imei, int size, String name, String value) {
        return devSensorlogRepository.getTop(imei, name, value, size);
    }

    @Override
    public List<Map> getLast(String imei, int size, String name) {
        List<Map> list = devSensorlogRepository.getLast(imei, name, size);
        List<String> daylist = new ArrayList<>();
        if(list.isEmpty()) return list;
        String mincretime = (String)list.get(list.size() - 1).get("cretimestr");
        mincretime = mincretime.split(" ")[0];
        daylist = Tool.createRangeMonth(Tool.strToDate(mincretime), Tool.getNowDate(), size, true);
        Iterator<String> its = daylist.iterator();
        List<Map> result = new ArrayList<>();
        while(its.hasNext()) {
            String day = its.next();
            String daymap = day.split("-")[0] + "-" + day.split("-")[1];
            int idx = -1;
            for(int i=list.size() - 1; i>=0; i--) {
                if(list.get(i).get("cretimestr").toString().indexOf(daymap) > -1) {
                    i = -1;
                    idx = -1;
                } else if(list.get(i).get("cretimestr").toString().compareTo(daymap) < 1) {
                    idx = i;
                } else {
                    i = -1;
                }
            }
            if(idx > -1) {
                Map item = new HashMap();
                item.put("id", "0");
                item.put("imei", imei);
                item.put("name", name);
                item.put("cretime", Tool.getTimestampLong(Tool.strToDate(day)));
                item.put("cretimestr", day + " 00:00:00");
                item.put("value", list.get(idx - 1).get("value"));
                list.add(idx, item);
            }
        }
        return list;
    }

    @Override
    public List<Map> getLastGroup(String imei, int size, String name, String starttime, String endtime) {
        size += 1;
        List<Map> list = null;
        if(!StringUtils.isBlank(starttime)) {
            if(starttime.length() <= 10)
                starttime += " 00:00:00";
            if(StringUtils.isBlank(endtime)) endtime = Tool.getNowDateTimeStr();
            else if(endtime.length() <= 10) endtime += " 23:59:59";
            list = devSensorlogRepository.getLastGroupByTime(imei, name, starttime, endtime);
        } else {
            list = devSensorlogRepository.getLastGroup(imei, name, size);
        }
        List<Map> result = new ArrayList<>();
        Map<String, Object> tmp = new HashMap<>();
        int listSize = list.size();
        for(int idx = 0; idx < listSize; idx++) {
            Map item = list.get(idx);
            if(tmp.containsKey("TMP_")) {
                long tmp_ = Long.parseLong(String.valueOf(tmp.get("TMP_")));
                long cretime = Long.parseLong(String.valueOf(item.get("cretime")));
                tmp_ = cretime - tmp_;
                // 时间差
                // log.info("[{}:{}]", tmp_, tmp_ / 1000 / 60 / 2);
                tmp.put("TMP_", cretime);
                if(tmp_ / 1000 / 60 / 1 > 0) {
                    tmp.remove("TMP_");
                    result.add(tmp);
                    tmp = new HashMap<>();
                    idx--;
                    continue;
                    // 如果前后记录时间差超过1分钟就强制分组
                } else {
                    tmp.put(item.get("name").toString(), item);
                }
            } else {
                // 存放上一条记录的时间
                tmp.put("TMP_", item.get("cretime"));
                tmp.put(item.get("name").toString(), item);
            }
            if(item.get("name").toString().equalsIgnoreCase(name) || idx == listSize - 1) {
                tmp.remove("TMP_");
                result.add(tmp);
                tmp = new HashMap<>();
            }
        }
        if(StringUtils.isBlank(starttime)) {
            Collections.reverse(result);
            result = result.subList(0, size - 1);
            Collections.reverse(result);
        }
        return result;
    }

    @Override
    public List<Map> getLoginlog(String starttime, String endtime, String imei) {
        return devLoginlogRepository.getList(starttime, endtime, imei);
    }

    @Override
    public List<Map> getSensorlog(String starttime, String endtime, String imei, String name) {
        return devSensorlogRepository.getList(starttime, endtime, imei, name);
    }

    @Override
    public String save(JSONObject data) {
        // 判断是否存在当前device
        DeviceCache device = DeviceListener.getDeviceById(data.getInteger("devicefk"));
        if(device == null)
            return "设备不存在";
        JSONArray props = data.getJSONArray("props");
        List<String> result = new ArrayList<>();
        for(Object o : props) {
            JSONObject prop = (JSONObject) o;
            DevProductdtlEntity propentity = ConfigListener.getPropByProductidName(device.getProductfk(), prop.getString("name"));
            Integer id = prop.getInteger("id");
            DevSensorlogEntity entity = null;
            // 判断是否id为空
            if(id != null) {
                Optional optional = devSensorlogRepository.findById(id);
                if(optional != null)
                    entity = (DevSensorlogEntity) optional.get();
            }
            if(entity == null) entity = new DevSensorlogEntity();
            entity.setValue(prop.getString("value"));
            entity.setDsid(propentity.getObjid() + "_" + propentity.getInsid() + "_" + propentity.getResid());
            entity.setName(propentity.getName());
            entity.setProductdtlfk(propentity.getId());
            entity.setImei(device.getImei());
            entity.setCretime(prop.getTimestamp("cretime"));
            entity.setDevicefk(device.getId());
            devSensorlogRepository.save(entity);
            result.add(String.valueOf(entity.getId()));
        }
        String[] rs = new String[0];
        return "[" + Tool.joinString(result.toArray(rs)) + "]";
    }

}

package com.iotimc.devicecenter.service.impl;

import com.iotimc.devicecenter.dao.DevSensorlogRepository;
import com.iotimc.devicecenter.domain.DevSensorlogEntity;
import com.iotimc.devicecenter.service.SensorlogService;
import com.iotimc.devicecenter.util.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SensorlogServiceImpl implements SensorlogService {
    @Autowired
    private DevSensorlogRepository devSensorlogRepository;

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
}

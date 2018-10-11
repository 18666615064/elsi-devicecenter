package com.iotimc.devicecenter.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.dao.DevControllogRepository;
import com.iotimc.devicecenter.domain.CompanyConfig;
import com.iotimc.devicecenter.domain.DevControllogEntity;
import com.iotimc.devicecenter.domain.DeviceCache;
import com.iotimc.devicecenter.domain.ProductConfig;
import com.iotimc.devicecenter.listener.ConfigListener;
import com.iotimc.devicecenter.listener.DeviceListener;
import com.iotimc.devicecenter.service.onenet.util.OnenetUtil;
import com.iotimc.devicecenter.util.RedisUtil;
import com.iotimc.devicecenter.util.Tool;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * 针对onenet使用的离线命令状态读取
 * easyiot默认使用的是离线命令
 */
@Component
@Slf4j
public class CommandTask implements Job {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DevControllogRepository devControllogRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //从redis取出缓存
        Map<Object, Object> imeis = redisUtil.hmget(DeviceListener.COMMCACHE);
        for(Map.Entry<Object, Object> item : imeis.entrySet()) {
            JSONObject value = (JSONObject)item.getValue();
            // 通过接口获取执行状态
            String imei = item.getKey().toString();
            JSONArray controllist = value.getJSONArray("controllist");
            for(int idx = controllist.size() - 1; idx >= 0; idx--) {
                JSONObject val = controllist.getJSONObject(idx);
                DevControllogEntity entity = null;
                Optional<DevControllogEntity> tempEntity = devControllogRepository.findById(val.getInteger("controllogid"));
                if(tempEntity != null) entity = tempEntity.get();
                Tool.execute(new Task(imei, val, entity));
            }
        }
    }
}

@Slf4j
class Task implements Runnable {
    private String imei = null;
    private JSONObject value = null;
    private DevControllogEntity entity = null;
    public Task(String imei, JSONObject value, DevControllogEntity entity) {
        this.imei = imei;
        this.value = value;
        this.entity = entity;
    }

    @Override
    public void run() {
        DeviceCache device = DeviceListener.getDeviceByImei(this.imei);
        CompanyConfig company = ConfigListener.getCompanyById(device.getCompanyfk());
        ProductConfig product = ConfigListener.getProductById(device.getProductfk());
        String uuid = this.value.getString("uuid");
        String restfulResultStr = OnenetUtil.getCacheInstruction(company.getBaseurl(), product.getApikey(), uuid, this.imei);
        JSONObject restfulResult = (JSONObject) JSONObject.parse(restfulResultStr);
        if(restfulResult.getString("errno").equalsIgnoreCase("0")) {
            JSONObject data = restfulResult.getJSONObject("data");
            Integer status = data.getInteger("send_status");
            Integer confirmStatus = data.getInteger("confirm_status");
            restfulResult.put("confirm_status_cn", Status.getConfirmStatus(confirmStatus));
            switch (status) {
                case 2:
                    // 命令被取消

                    break;
                case 4:
                    // 命令过期
                    break;
                case 5:
                    // 执行成功
                    break;
                case 6:
                    // 失效
                case 7:
                    // 未知问题
                    break;
            }
            if(status != 1 && status != 3) {
                // 除了在等待和已发往设备的状态，其他都需要删除缓存
                DeviceListener.removeCommandCache(imei, this.value.getString("uuid"));
            }
        } else {
            log.error("获取离线命令执行情况失败[{}]:{}", uuid, restfulResult.getString("message"));
        }
    }
}

class Status {
    /**
     * 执行状态
     */
    public final static String STATUS_1 = "命令等待";
    public final static String STATUS_2 = "命令取消";
    public final static String STATUS_3 = "命令已发往设备";
    public final static String STATUS_4 = "命令过期";
    public final static String STATUS_5 = "命令下发成功";
    public final static String STATUS_6 = "命令下发失败";
    public final static String STATUS_7 = "其他未知错误";

    /**
     * 执行明细
     */
    public final static String CONFIRM_0 = "命令执行成功";
    public final static String CONFIRM_1 = "对象或资源不允许该操作";
    public final static String CONFIRM_2 = "终端未注册";
    public final static String CONFIRM_3 = "未发现该对象或资源";
    public final static String CONFIRM_4 = "设备响应码错误等";
    public final static String CONFIRM_5 = "设备响应超时";
    public final static String CONFIRM_6 = "请求参数错误";
    public final static String CONFIRM_7 = "设备响应报文错误";
    public final static String CONFIRM_8 = "访问权限不允许";
    public final static String CONFIRM_9 = "请求格式错误，如少参数或编码等";
    public final static String CONFIRM_10 = "无任何首选的报文格式可以返回";
    public final static String CONFIRM_11 = "指定的报文格式不支持";

    public static String getStatus(int status) {
        switch(status) {
            case 1:
                return STATUS_1;
            case 2:
                return STATUS_2;
            case 3:
                return STATUS_3;
            case 4:
                return STATUS_4;
            case 5:
                return STATUS_5;
            case 6:
                return STATUS_6;
            case 7:
                return STATUS_7;
        }
        return "";
    }

    public static String getConfirmStatus(int status) {
        switch(status) {
            case 0:
                return CONFIRM_0;
            case 1:
                return CONFIRM_1;
            case 2:
                return CONFIRM_2;
            case 3:
                return CONFIRM_3;
            case 4:
                return CONFIRM_4;
            case 5:
                return CONFIRM_5;
            case 6:
                return CONFIRM_6;
            case 7:
                return CONFIRM_7;
            case 8:
                return CONFIRM_8;
            case 9:
                return CONFIRM_9;
            case 10:
                return CONFIRM_10;
            case 11:
                return CONFIRM_11;
        }
        return "";
    }
}

package com.iotimc.devicecenter.service;

import com.alibaba.fastjson.JSONObject;

public interface DeviceService {
    /**
     * 添加设备数据，同时在相应第三方接入平台进行添加
     * @param data
     * @return
     */
    String addDevice(JSONObject data);

    /**
     * 删除设备
     * @param id
     * @return
     */
    String delDevice(Integer id);

    /**
     * 修改设备
     * @param data
     * @return
     */
    String modDevice(JSONObject data);

    /**
     * 异步发送
     * @param data
     * @return
     */
    String send(JSONObject data);

    /**
     * 同步发送
     * @param data
     * @return
     */
    String syncSend(JSONObject data);

    /**
     * 异步读取
     * @param data
     * @return
     */
    String read(JSONObject data);

    /**
     * 同步读取
     * @param data
     * @return
     */
    String syncRead(JSONObject data);

    /**
     * 获取状态
     * @param imei
     * @param platformid
     * @return
     */
    String getStatus(String imei, String platformid);

    /**
     * 获取状态，提供给没有缓存设备用的
     * @param imei
     * @param platformid
     * @param companyid
     * @param productid
     * @return
     */
    String getStatus(String imei, String platformid, int companyid, int productid);
}

package com.iotimc.devicecenter.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.domain.DevProductEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    /**
     * 根据公司id获取所有产品信息
     * @param id
     * @return
     */
    List<DevProductEntity> listByCompany(Integer id);

    /**
     * 根据公司id获取产品分页信息
     * @return
     */
    Page<DevProductEntity> pageByCompany(Integer id);

    /**
     * 根据id获取产品信息
     * @param id
     * @return
     */
    JSONObject info(Integer id);

    /**
     * 添加产品
     * @param data
     * @return
     */
    String add(JSONObject data);

    /**
     * 修改产品
     * @param data
     * @return
     */
    String mod(JSONObject data);

    /**
     * 删除产品
     * @param id
     * @return
     */
    String del(Integer id);

    /**
     * 根据设备id获取产品
     * @param id
     * @return
     */
    JSONObject getProductByDevid(Integer id);

    /**
     * 为产品增加属性
     * @param datalist
     * @return
     */
    String addProps(JSONObject datalist);
}

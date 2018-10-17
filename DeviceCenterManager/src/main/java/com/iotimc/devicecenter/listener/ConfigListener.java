package com.iotimc.devicecenter.listener;

import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.dao.DevProductEntityRepository;
import com.iotimc.devicecenter.domain.*;
import com.iotimc.devicecenter.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ConfigListener implements InitializingBean{
    private static JSONObject companyList = new JSONObject();
    @Autowired
    private DevProductEntityRepository devProductEntityRepository_prv;

    @Autowired
    private RedisUtil redisUtil_priv;

    private static RedisUtil redisUtil;

    private static DevProductEntityRepository devProductEntityRepository;
    @Override
    public void afterPropertiesSet() throws Exception {
        devProductEntityRepository = devProductEntityRepository_prv;
        redisUtil = redisUtil_priv;
        new Thread(){
            @Override
            public void run() {
                try {
                    while(true) {
                        readProductConfig(null);
                        // 一小时刷新一次
                        Thread.sleep(60 * 60 * 1000);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 读取厂商配置
     */
    public static void readProductConfig(Integer productid) {
        List<Map> list = devProductEntityRepository.getAll(productid);
        // 缓存
        if(productid == null)
            companyList.clear();
        list.forEach(item -> {
            CompanyConfig company = null;
            Map<Integer, ProductConfig> productlist = null;
            ProductConfig product = null;
            if(!companyList.containsKey(String.valueOf(item.get("companyfk")))) {
                company = new CompanyConfig();
                productlist = new HashMap<>();
                company.setProductList(productlist);
                // 存放其他信息
                company.setId((Integer) item.get("companyfk"));
                company.setName((String) item.get("companyname"));
                company.setAlias((String) item.get("companyalias"));
                company.setBaseurl((String) item.get("baseurl"));
                company.setUsername((String) item.get("username"));
                company.setPassword((String) item.get("password"));
                company.setApikey((String) item.get("companyapikey"));
                company.setToken((String) item.get("companytoken"));
                company.setSystemname((String) item.get("systemname"));
                companyList.put(String.valueOf(company.getId()), company);
            } else {
                company = (CompanyConfig) companyList.get(item.get("companyfk"));
                productlist = company.getProductList();
            }
            if(!productlist.containsKey(item.get("roofid"))) {
                product = new ProductConfig();
                product.setId((Integer) item.get("roofid"));
                product.setName((String) item.get("roofname"));
                product.setAlias((String) item.get("roofalias"));
                product.setToken((String) item.get("rooftoken"));
                product.setApikey((String) item.get("roofapikey"));
                product.setCompanyfk((Integer) item.get("companyfk"));
                productlist.put(product.getId(), product);
                Map<String, DevProductdtlEntity> dtlmap = new HashMap<>();
                product.setDtllist(dtlmap);
            } else {
                product = productlist.get(item.get("roofid"));
            }
            // 存放属性
            DevProductdtlEntity prop = new DevProductdtlEntity();
            prop.setId((Integer) item.get("dtlid"));
            prop.setObjid((Integer) item.get("objid"));
            prop.setResid((Integer) item.get("resid"));
            prop.setInsid((Byte) item.get("insid"));
            prop.setName((String) item.get("name"));
            prop.setType((String) item.get("type"));
            prop.setAlias((String) item.get("alias"));
            prop.setWriteable((String.valueOf(item.get("writeable")).equals("true")?Byte.parseByte("1"):Byte.parseByte("0")));
            //log.info("[{}][{}]:[{}]----[{}]", prop.getObjid(),prop.getName(),prop.getWriteable(),item.get("writeable"));
            prop.setIshex((Byte)item.get("ishex"));
            prop.setEditable((String.valueOf(item.get("editable")).equals("true")?Byte.parseByte("1"):Byte.parseByte("0")));
            prop.setCorrect((String) item.get("correct"));
            product.getDtllist().put(prop.getName(), prop);
        });
    }

    /**
     * 根据设备id获取产品配置
     * @param id
     * @return
     */
    public static ProductConfig getProductByDevId(Integer id) {
        DeviceCache entity = DeviceListener.getDeviceById(id);
        if(entity != null) {
            return getProductById(entity.getProductfk());
        }
        return null;
    }

    /**
     * 根据设备id获取公司配置
     * @param id
     * @return
     */
    public static CompanyConfig getCompanyByDevId(Integer id) {
        DeviceCache entity = DeviceListener.getDeviceById(id);
        if(entity != null) {
            return getCompanyById(entity.getCompanyfk());
        }
        return null;
    }

    /**
     * 根据设备imei获取公司配置
     * @param imei
     * @return
     */
    public static CompanyConfig getCompanyByImei(String imei) {
        DeviceCache entity = DeviceListener.getDeviceByImei(imei);
        if(entity != null) {
            return getCompanyById(entity.getCompanyfk());
        }
        return null;
    }

    /**
     * 根据接入系统的平台名称获取公司配置
     * @param systemname
     * @return
     */
    public static CompanyConfig getCompanyBySystemname(String systemname) {
        for(String key : companyList.keySet()) {
            CompanyConfig company = (CompanyConfig) companyList.get(key);
            if(company.getSystemname().equalsIgnoreCase(systemname)) {
                return company;
            }
        }
        return null;
    }

    /**
     * 根据产品id获取产品配置
     * @param id
     * @return
     */
    public static ProductConfig getProductById(Integer id) {
        ProductConfig result = null;
        for(String key : companyList.keySet()) {
            CompanyConfig value = (CompanyConfig) companyList.get(key);
            if(value.getProductList().containsKey(id)) {
                result = value.getProductList().get(id);
            }
        }
        return result;
    }

    /**
     * 根据公司id获取公司配置
     * @param id
     * @return
     */
    public static CompanyConfig getCompanyById(Integer id) {
        CompanyConfig result = null;
        if(companyList.containsKey(String.valueOf(id))) {
            result = (CompanyConfig) companyList.get(String.valueOf(id));
        }
        return result;
    }

    public static DevProductdtlEntity getPropByDevIdName(Integer id, String name) {
        ProductConfig product = getProductByDevId(id);
        if(product != null) {
            return product.getDtllist().get(name);
        }
        return null;
    }

    public static DevProductdtlEntity getPropByDevImeiName(String imei, String name) {
        DeviceCache entity = DeviceListener.getDeviceByImei(imei);
        return getPropByDevIdName(entity.getId(), name);
    }

    public static DevProductdtlEntity getPropByDevImeiDsid(String imei, Integer objid, Integer resid, Byte insid) {
        DeviceCache entity = DeviceListener.getDeviceByImei(imei);
        ProductConfig product = getProductByDevId(entity.getId());
        Map<String, DevProductdtlEntity> dtllist = product.getDtllist();
        for(String key : dtllist.keySet()) {
            DevProductdtlEntity dtl = dtllist.get(key);
            if(dtl.getObjid().equals(objid) && dtl.getResid().equals(resid) && dtl.getInsid().equals(insid)) return dtl;
        }
        return null;
    }

    public static DevProductdtlEntity getPropByProductidName(Integer productfk, String name) {
        ProductConfig product = getProductById(productfk);
        if(product != null) {
            Map<String, DevProductdtlEntity> dtllist = product.getDtllist();
            if(dtllist != null)
                return dtllist.get(name);
        }
        return null;
    }

}

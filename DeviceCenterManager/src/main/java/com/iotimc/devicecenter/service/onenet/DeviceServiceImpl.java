package com.iotimc.devicecenter.service.onenet;

import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.dao.DevDeviceEntityRepository;
import com.iotimc.devicecenter.domain.*;
import com.iotimc.devicecenter.listener.ConfigListener;
import com.iotimc.devicecenter.listener.DeviceListener;
import com.iotimc.devicecenter.service.DeviceService;
import com.iotimc.devicecenter.service.onenet.util.OnenetUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("onenet")
public class DeviceServiceImpl implements DeviceService{
    @Autowired
    private DevDeviceEntityRepository devDeviceEntityRepository;

    @Override
    public String addDevice(JSONObject data) {
        ProductConfig product = ConfigListener.getProductById(data.getInteger("productid"));
        CompanyConfig company = ConfigListener.getCompanyById(data.getInteger("companyid"));
        String resultStr = OnenetUtil.addDevice(company.getBaseurl(), product.getApikey(), data.getString("imei"), data.getString("imsi"));
        JSONObject result = (JSONObject)JSONObject.parse(resultStr);
        int errno = result.getInteger("errno");
        if(errno != 0)
            return result.getString("error");
        DevDeviceEntity entity = new DevDeviceEntity();
        entity.setStatus("P");
        entity.setImei(data.getString("imei"));
        entity.setImsi(data.getString("imsi"));
        entity.setCompanyfk(company.getId());
        entity.setCode(data.getString("code"));
        entity.setName(data.getString("name"));
        entity.setProductfk(product.getId());
        entity.setPlatformid(result.getJSONObject("data").getString("device_id"));
        entity = devDeviceEntityRepository.save(entity);
        // 刷新redis数据
        DeviceListener.refreshCache(entity.getId());
        return String.valueOf(entity.getId());
    }

    @Override
    public String delDevice(Integer id) {
        DeviceCache device = DeviceListener.getDeviceById(id);
        ProductConfig product = ConfigListener.getProductByDevId(id);
        CompanyConfig company = ConfigListener.getCompanyById(product.getCompanyfk());

        String resultStr = OnenetUtil.delete(company.getBaseurl(), product.getApikey(), device.getPlatformid());
        JSONObject result = (JSONObject) JSONObject.parse(resultStr);
        if(result.getInteger("errno") != 0) {
            return result.getString("error");
        }
        DeviceListener.delCacheDeviceById(device.getId());
        devDeviceEntityRepository.deleteById(id);
        return String.valueOf(device.getId());
    }

    @Override
    public String modDevice(JSONObject data) {
        // 只能修改imsi
        DevDeviceEntity device = devDeviceEntityRepository.findById(data.getInteger("id")).get();
        device.setImsi(data.getString("imsi"));
        devDeviceEntityRepository.save(device);
        return String.valueOf(device.getId());
    }

    @Override
    public String send(JSONObject data) {
        DeviceCache device = null;
        if(StringUtils.isBlank(data.getString("imei"))) {
            device = DeviceListener.getDeviceById(data.getInteger("id"));
        } else {
            device = DeviceListener.getDeviceByImei(data.getString("imei"));
        }
        CompanyConfig company = ConfigListener.getCompanyById(device.getCompanyfk());
        ProductConfig product = ConfigListener.getProductById(device.getProductfk());
        DevProductdtlEntity prop = ConfigListener.getPropByDevImeiName(device.getImei(), data.getString("name"));
        if(prop == null) return "找不到属性";
        if(prop.getWriteable() == 0) return "属性不可写";
        String resultStr = OnenetUtil.send(company.getBaseurl(), product.getApikey(), device.getImei(), prop.getObjid(), prop.getResid(), prop.getInsid(), data.get("value"),
                data.containsKey("timeout")?data.getInteger("timeout"):null);
        JSONObject result = (JSONObject) JSONObject.parse(resultStr);
        return result.getString("errno").equalsIgnoreCase("0")?result.getJSONObject("data").getString("uuid"):result.getString("error");
    }

    @Override
    public String syncSend(JSONObject data) {
        DeviceCache device = null;
        if(StringUtils.isBlank(data.getString("imei"))) {
            device = DeviceListener.getDeviceById(data.getInteger("id"));
        } else {
            device = DeviceListener.getDeviceByImei(data.getString("imei"));
        }
        CompanyConfig company = ConfigListener.getCompanyById(device.getCompanyfk());
        ProductConfig product = ConfigListener.getProductById(device.getProductfk());
        DevProductdtlEntity prop = ConfigListener.getPropByDevImeiName(device.getImei(), data.getString("name"));
        if(prop == null) return "找不到属性";
        if(prop.getWriteable() == 0) return "属性不可写";
        String resultStr = OnenetUtil.sendSync(company.getBaseurl(), product.getApikey(), device.getImei(), prop.getObjid(), prop.getResid(), prop.getInsid(), data.get("value"));
        JSONObject result = (JSONObject) JSONObject.parse(resultStr);
        return result.getString("errno").equalsIgnoreCase("0")?"0":result.getString("error");
    }

    @Override
    public String read(JSONObject data) {
        DeviceCache device = null;
        if(StringUtils.isBlank(data.getString("imei"))) {
            device = DeviceListener.getDeviceById(data.getInteger("id"));
        } else {
            device = DeviceListener.getDeviceByImei(data.getString("imei"));
        }
        CompanyConfig company = ConfigListener.getCompanyById(device.getCompanyfk());
        ProductConfig product = ConfigListener.getProductById(device.getProductfk());
        DevProductdtlEntity prop = ConfigListener.getPropByDevImeiName(device.getImei(), data.getString("name"));
        if(prop == null) return "找不到属性";
        String resultStr = OnenetUtil.readProps(company.getBaseurl(), product.getApikey(), device.getImei(), prop.getObjid(), prop.getResid(), prop.getInsid());
        JSONObject result = (JSONObject) JSONObject.parse(resultStr);
        return result.getString("errno").equalsIgnoreCase("0")?result.getJSONObject("data").getString("uuid"):result.getString("error");
    }

    @Override
    public String syncRead(JSONObject data) {
        DeviceCache device = null;
        if(StringUtils.isBlank(data.getString("imei"))) {
            device = DeviceListener.getDeviceById(data.getInteger("id"));
        } else {
            device = DeviceListener.getDeviceByImei(data.getString("imei"));
        }
        CompanyConfig company = ConfigListener.getCompanyById(device.getCompanyfk());
        ProductConfig product = ConfigListener.getProductById(device.getProductfk());
        DevProductdtlEntity prop = ConfigListener.getPropByDevImeiName(device.getImei(), data.getString("name"));
        if(prop == null) return "找不到属性";
        String resultStr = OnenetUtil.readPropsSync(company.getBaseurl(), product.getApikey(), device.getImei(), prop.getObjid(), prop.getResid(), prop.getInsid(),
                data.containsKey("timeout")?data.getInteger("timeout"):null);
        JSONObject result = (JSONObject) JSONObject.parse(resultStr);
        return result.getString("errno").equalsIgnoreCase("0")?"0":result.getString("error");
    }

    @Override
    public String getStatus(String imei, String platformid) {
        DeviceCache device = DeviceListener.getDeviceByImei(imei);
        return getStatus(imei, platformid, device.getCompanyfk(), device.getProductfk());
    }

    @Override
    public String getStatus(String imei, String platformid, int companyid, int productid) {
        ProductConfig product = ConfigListener.getProductById(productid);
        CompanyConfig company = ConfigListener.getCompanyById(companyid);
        return OnenetUtil.getDeviceStatus(company.getBaseurl(), product.getApikey(), platformid);
    }
}

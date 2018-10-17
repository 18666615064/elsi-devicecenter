package com.iotimc.devicecenter.controller;

import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.domain.CompanyConfig;
import com.iotimc.devicecenter.domain.DevDeviceEntity;
import com.iotimc.devicecenter.domain.DeviceCache;
import com.iotimc.devicecenter.domain.ProductConfig;
import com.iotimc.devicecenter.listener.ConfigListener;
import com.iotimc.devicecenter.listener.DeviceListener;
import com.iotimc.devicecenter.service.DeviceNormalService;
import com.iotimc.devicecenter.service.DeviceService;
import com.iotimc.devicecenter.util.Tool;
import com.iotimc.elsi.auth.annotation.NoneAuthorize;
import com.iotimc.elsi.bean.PageRequestBean;
import com.iotimc.elsi.bean.SortRequestBean;
import com.iotimc.elsi.msg.common.HandleEntitySuccessMsg;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


@NoneAuthorize
@RestController
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    @Qualifier("onenet")
    private DeviceService onenetDeviceService;

    @Autowired
    @Qualifier("easyiot")
    private DeviceService easyiotDeviceService;

    @Autowired
    private DeviceNormalService deviceNormalService;

    /**
     * 添加设备
     * 上传格式:
     * {
     *     "productid": 设备类型id,
     *     "imei": "xxx",
     *     "imsi": "xxx",
     *     "code": "编号",
     *     "name": "名称",
     *     "companyid": 运营商id
     * }
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<HandleEntitySuccessMsg> addDevice(@RequestBody JSONObject data) {
        String id = "";
        try {
            if (!(StringUtils.isBlank(data.getString("productid")) || StringUtils.isBlank(data.getString("imei")) ||
                    StringUtils.isBlank(data.getString("imsi")) || StringUtils.isBlank(data.getString("code")) ||
                     StringUtils.isBlank(data.getString("name")) || StringUtils.isBlank(data.getString("companyid")))) {
                ProductConfig product = ConfigListener.getProductById(data.getInteger("productid"));
                if (product == null) throw new RuntimeException("找不到产品配置信息");
                CompanyConfig company = ConfigListener.getCompanyById(product.getCompanyfk());
                data.put("companyid", product.getCompanyfk());
                if(company.getSystemname().equalsIgnoreCase("onenet")) {
                    id = onenetDeviceService.addDevice(data);
                } else if(company.getSystemname().equalsIgnoreCase("easyiot")) {
                    id = easyiotDeviceService.addDevice(data);
                }
                if(!Tool.isNumber(id)) throw new RuntimeException(id);
            } else {
                throw new RuntimeException("数据上传不完整");
            }
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new HandleEntitySuccessMsg("添加失败，错误：" + e.getMessage(), "-1"));
        }
        return ResponseEntity.ok(new HandleEntitySuccessMsg("添加成功", id));
    }

    /**
     * 删除设备
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<HandleEntitySuccessMsg> delDevice(@PathVariable("id") Integer id) {
        String result = null;
        ProductConfig product = ConfigListener.getProductByDevId(id);
        if(product == null) {
            return ResponseEntity.ok(new HandleEntitySuccessMsg("找不到产品配置信息或当前设备不存在", "-1"));
        }
        CompanyConfig company = ConfigListener.getCompanyById(product.getCompanyfk());
        if(company.getSystemname().equalsIgnoreCase("onenet")) {
            result = onenetDeviceService.delDevice(id);
        } else if(company.getSystemname().equalsIgnoreCase("easyiot")) {
            result = easyiotDeviceService.delDevice(id);
        }
        return ResponseEntity.ok(Tool.isNumber(result)?new HandleEntitySuccessMsg("删除成功", result):new HandleEntitySuccessMsg("删除失败：" + result, "-1"));
    }

    /**
     * 修改设备属性
     */
    @RequestMapping(value = "/mod/id", method = RequestMethod.PUT)
    public ResponseEntity<HandleEntitySuccessMsg> modDevice(@PathVariable("id") Integer id, @RequestBody JSONObject data) {
        String result = null;
        if(!data.containsKey("imei")) return ResponseEntity.ok(new HandleEntitySuccessMsg("参数不正确", "-1"));
        ProductConfig product = ConfigListener.getProductByDevId(id);
        if(product == null) return ResponseEntity.ok(new HandleEntitySuccessMsg("找不到产品配置信息或当前设备不存在", "-1"));
        CompanyConfig company = ConfigListener.getCompanyById(product.getCompanyfk());
        data.put("id", id);
        if(company.getSystemname().equalsIgnoreCase("onenet")) {
            result = onenetDeviceService.modDevice(data);
        } else if(company.getSystemname().equalsIgnoreCase("easyiot")) {
            result = easyiotDeviceService.modDevice(data);
        }
        return ResponseEntity.ok(Tool.isNumber(result)?new HandleEntitySuccessMsg("修改成功", result):new HandleEntitySuccessMsg("修改失败：" + result, "-1"));
    }

    /**
     * 异步发送命令
     * 发送:
     * {
     *     "imei": "设备imei",
     *     "name": "属性名",
     *     "value": 属性值
     * }
     * 返回:
     * {
     *     "id": "命令执行id",
     *     "message": "接口调用结果"
     * }
     */
    @RequestMapping(value = "/send", method = RequestMethod.PUT)
    public ResponseEntity<HandleEntitySuccessMsg> send(@RequestBody JSONObject data) {
        if(StringUtils.isBlank(data.getString("name")) || StringUtils.isBlank(data.getString("value"))
                || (StringUtils.isBlank(data.getString("id")) && StringUtils.isBlank(data.getString("imei")))) {
            return ResponseEntity.ok(new HandleEntitySuccessMsg("参数不正确", "-1"));
        }
        String result = null;
        CompanyConfig company = null;
        if(StringUtils.isBlank(data.getString("imei"))) {
            company = ConfigListener.getCompanyByDevId(data.getInteger("id"));
        } else {
            company = ConfigListener.getCompanyByImei(data.getString("imei"));
        }
        if(company == null) {
            return ResponseEntity.ok(new HandleEntitySuccessMsg("找不到产品配置信息或当前设备不存在", "-1"));
        }
        if(company.getSystemname().equalsIgnoreCase("onenet")) {
            result = onenetDeviceService.send(data);
        } else if(company.getSystemname().equalsIgnoreCase("easyiot")) {
            result = easyiotDeviceService.send(data);
        }
        return ResponseEntity.ok(Tool.isNumber(result)&&!result.equalsIgnoreCase("0")?new HandleEntitySuccessMsg("发送失败：" + result, "-1"):new HandleEntitySuccessMsg("发送成功", result));
    }

    /**
     * 异步读取属性
     */
    @RequestMapping(value = "/read", method = RequestMethod.PUT)
    public ResponseEntity<HandleEntitySuccessMsg> readProp(@RequestBody JSONObject data) {
        if(StringUtils.isBlank(data.getString("name")) || StringUtils.isBlank(data.getString("value"))
                || (StringUtils.isBlank(data.getString("id")) && StringUtils.isBlank(data.getString("imei")))) {
            return ResponseEntity.ok(new HandleEntitySuccessMsg("参数不正确", "-1"));
        }
        String result = null;
        CompanyConfig company = null;
        if(StringUtils.isBlank(data.getString("imei"))) {
            company = ConfigListener.getCompanyByDevId(data.getInteger("id"));
        } else {
            company = ConfigListener.getCompanyByImei(data.getString("imei"));
        }
        if(company == null) {
            return ResponseEntity.ok(new HandleEntitySuccessMsg("找不到产品配置信息或当前设备不存在", "-1"));
        }
        if(company.getSystemname().equalsIgnoreCase("onenet")) {
            result = onenetDeviceService.read(data);
        } else if(company.getSystemname().equalsIgnoreCase("easyiot")) {
            result = easyiotDeviceService.read(data);
        }
        return ResponseEntity.ok(Tool.isNumber(result)?new HandleEntitySuccessMsg("读取成功", result):new HandleEntitySuccessMsg("读取失败：" + result, "-1"));
    }

    /**
     * 同步读取属性
     */
    @RequestMapping(value = "/sendsync", method = RequestMethod.PUT)
    public ResponseEntity<HandleEntitySuccessMsg> syncSend(@RequestBody JSONObject data) {
        if(StringUtils.isBlank(data.getString("name")) || StringUtils.isBlank(data.getString("value"))
                || (StringUtils.isBlank(data.getString("id")) && StringUtils.isBlank(data.getString("imei")))) {
            return ResponseEntity.ok(new HandleEntitySuccessMsg("参数不正确", "-1"));
        }
        String result = null;
        CompanyConfig company = null;
        if(StringUtils.isBlank(data.getString("imei"))) {
            company = ConfigListener.getCompanyByDevId(data.getInteger("id"));
        } else {
            company = ConfigListener.getCompanyByImei(data.getString("imei"));
        }
        if(company == null) {
            return ResponseEntity.ok(new HandleEntitySuccessMsg("找不到产品配置信息或当前设备不存在", "-1"));
        }
        if(company.getSystemname().equalsIgnoreCase("onenet")) {
            result = onenetDeviceService.syncSend(data);
        } else if(company.getSystemname().equalsIgnoreCase("easyiot")) {
            result = easyiotDeviceService.syncSend(data);
        }
        return ResponseEntity.ok(Tool.isNumber(result)?new HandleEntitySuccessMsg("发送成功", result):new HandleEntitySuccessMsg("发送失败：" + result, "-1"));
    }

    /**
     * 异步读取属性
     */
    @RequestMapping(value = "/readsync", method = RequestMethod.PUT)
    public ResponseEntity<HandleEntitySuccessMsg> syncReadProp(@RequestBody JSONObject data) {
        if(StringUtils.isBlank(data.getString("name")) || StringUtils.isBlank(data.getString("value"))
                || (StringUtils.isBlank(data.getString("id")) && StringUtils.isBlank(data.getString("imei")))) {
            return ResponseEntity.ok(new HandleEntitySuccessMsg("参数不正确", "-1"));
        }
        String result = null;
        CompanyConfig company = null;
        if(StringUtils.isBlank(data.getString("imei"))) {
            company = ConfigListener.getCompanyByDevId(data.getInteger("id"));
        } else {
            company = ConfigListener.getCompanyByImei(data.getString("imei"));
        }
        if(company == null) {
            return ResponseEntity.ok(new HandleEntitySuccessMsg("找不到产品配置信息或当前设备不存在", "-1"));
        }
        if(company.getSystemname().equalsIgnoreCase("onenet")) {
            result = onenetDeviceService.syncRead(data);
        } else if(company.getSystemname().equalsIgnoreCase("easyiot")) {
            result = easyiotDeviceService.syncRead(data);
        }
        return ResponseEntity.ok(Tool.isNumber(result)?new HandleEntitySuccessMsg("读取成功", result):new HandleEntitySuccessMsg("读取失败：" + result, "-1"));
    }

    /**
     * 获取设备详细信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/info/{id}")
    public ResponseEntity<JSONObject> getDeviceInfo(@PathVariable("id") Integer id) {
        DeviceCache device = DeviceListener.getDeviceById(id);
        JSONObject result = new JSONObject();
        if(device == null) {
            result.put("message", "找不到设备信息");
            result.put("id", "-1");
            return ResponseEntity.ok(result);
        }
        CompanyConfig company = ConfigListener.getCompanyById(device.getCompanyfk());
        ProductConfig product = ConfigListener.getProductById(device.getProductfk());
        result.put("device", device);
        JSONObject companyJson = new JSONObject();
        JSONObject productJson = new JSONObject();
        companyJson.put("name", company.getName());
        companyJson.put("alias", company.getAlias());
        companyJson.put("id", company.getId());
        companyJson.put("systemname", company.getSystemname());
        result.put("company", companyJson);

        productJson.put("alias", product.getAlias());
        productJson.put("name", product.getName());
        productJson.put("notes", product.getNotes());
        productJson.put("id", product.getId());
        result.put("product", productJson);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/info/imei/{imei}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> getDeviceInfo(@PathVariable("imei") String imei) {
        DeviceCache device = DeviceListener.getDeviceByImei(imei);
        if(device == null) {
            JSONObject result = new JSONObject();
            result.put("message", "找不到设备信息");
            result.put("id", "-1");
            return ResponseEntity.ok(result);
        }
        return getDeviceInfo(device.getId());
    }

    @RequestMapping(value = "/pagebyproduct", method = RequestMethod.GET)
    public ResponseEntity<Page<DevDeviceEntity>> pagebyProduct(@RequestParam("id") Integer id, @RequestParam("page") PageRequestBean pageRequestBean,
                                                               @RequestParam("sort") SortRequestBean[] sortRequestBeans) {
        pageRequestBean.setPageNum(pageRequestBean.getPageNum() - 1);
        return ResponseEntity.ok(deviceNormalService.devicePageByProduct(id, pageRequestBean, Arrays.asList(sortRequestBeans)));
    }

    @RequestMapping(value = "/refreshcache", method = RequestMethod.GET)
    public ResponseEntity<HandleEntitySuccessMsg> refreshCache() {
        DeviceListener.refreshOnlineStatus();
        return ResponseEntity.ok(new HandleEntitySuccessMsg("刷新成功", "0"));
    }
}

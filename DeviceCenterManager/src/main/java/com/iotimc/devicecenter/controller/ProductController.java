package com.iotimc.devicecenter.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.domain.DevProductdtlEntity;
import com.iotimc.devicecenter.domain.DeviceCache;
import com.iotimc.devicecenter.domain.ProductConfig;
import com.iotimc.devicecenter.listener.ConfigListener;
import com.iotimc.devicecenter.listener.DeviceListener;
import com.iotimc.devicecenter.service.DeviceNormalService;
import com.iotimc.devicecenter.service.ProductService;
import com.iotimc.devicecenter.util.Tool;
import com.iotimc.elsi.auth.annotation.NoneAuthorize;
import com.iotimc.elsi.msg.common.HandleEntitySuccessMsg;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@NoneAuthorize
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private DeviceNormalService deviceNormalService;

    /**
     * 通过设备id获取产品
     */
    @RequestMapping(value = "/getproductbydevid", method = RequestMethod.GET)
    public JSONObject getProductByDevid(@RequestParam("id") Integer id) {
        DeviceCache deviceCache = DeviceListener.getDeviceById(id);
        return getProductById(deviceCache.getProductfk());
    }

    /**
     * 通过产品id获取产品
     */
    @RequestMapping(value = "/getproductbyid", method = RequestMethod.GET)
    public JSONObject getProductById(@RequestParam("id") Integer id) {
        ProductConfig product = ConfigListener.getProductByDevId(id);
        JSONObject productJson = new JSONObject();
        productJson.put("alias", product.getAlias());
        productJson.put("name", product.getName());
        productJson.put("notes", product.getNotes());
        productJson.put("id", product.getId());
        productJson.put("dtllist", product.getDtllist());
        return productJson;
    }

    /**
     * 通过厂商获取产品
     */
    @RequestMapping(value = "/refreshProductCache", method = RequestMethod.GET)
    public ResponseEntity<HandleEntitySuccessMsg> refreshProductCache() {
        ConfigListener.readProductConfig(null);
        return ResponseEntity.ok(new HandleEntitySuccessMsg("刷新成功", "0"));
    }

    /**
     * 添加产品
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseEntity<HandleEntitySuccessMsg> addProduct(@RequestBody JSONObject data) {
        String[] emptyNames = Tool.isBlanks(data, new String[]{"name", "alias", "companyfk"});
        if(emptyNames.length > 0 ) {
            return ResponseEntity.ok(new HandleEntitySuccessMsg("添加失败：以下参数为空[" + Tool.joinString(emptyNames) + "]"));
        } else if(data.get("dtllist") == null) {
            return ResponseEntity.ok(new HandleEntitySuccessMsg("添加失败：找不到属性", "-1"));
        }
        if(ConfigListener.getCompanyById(data.getInteger("companyfk")) == null)
            return ResponseEntity.ok(new HandleEntitySuccessMsg("添加失败：找不到所属运营商", "-1"));
        String result = productService.add(data);
        return ResponseEntity.ok(Tool.isNumber(result)?new HandleEntitySuccessMsg("添加成功", result):new HandleEntitySuccessMsg("添加失败：" + result, "-1"));
    }

    /**
     * 删除产品
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<HandleEntitySuccessMsg> delProduct(@PathVariable("id") Integer id) {
        List devicelist = deviceNormalService.deviceListByProduct(id);
        if(!devicelist.isEmpty()) return ResponseEntity.ok(new HandleEntitySuccessMsg("删除失败：该产品下拥有设备", "-1"));
        String result = productService.del(id);
        return ResponseEntity.ok(Tool.isNumber(result)?new HandleEntitySuccessMsg("删除成功", result):new HandleEntitySuccessMsg("删除失败：" + result, "-1"));
    }

    /**
     * 修改产品
     */
    public void modProduct() {
        //TODO: 修改产品待完善
    }

    /**
     * 添加产品属性
     * @param prop
     * @return
     */
    @RequestMapping(value = "/addprops", method = RequestMethod.PUT)
    public ResponseEntity<HandleEntitySuccessMsg> addProps(@RequestBody JSONObject prop) {
        // 检查可行性
        if(!prop.containsKey("props")) return ResponseEntity.ok(new HandleEntitySuccessMsg("添加失败：属性列表为空", "-1"));
        JSONArray datalist = prop.getJSONArray("props");
        for(int i=0; i<datalist.size(); i++) {
            JSONObject item = datalist.getJSONObject(i);
            String[] isempty = Tool.isBlanks(item, new String[]{"objid", "resid", "insid", "name", "writeable", "alias", "type"});
            if(isempty.length > 0)
                return ResponseEntity.ok(new HandleEntitySuccessMsg("添加失败：属性" + item.getString("name") + "缺少属性值[" + Tool.joinString(isempty) + "]"));
        }
        String result = productService.addProps(prop);
        return ResponseEntity.ok(Tool.isNumber(result)?new HandleEntitySuccessMsg("添加成功", result):new HandleEntitySuccessMsg("添加失败：" + result, "-1"));
    }
}

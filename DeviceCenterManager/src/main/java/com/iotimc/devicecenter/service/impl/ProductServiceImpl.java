package com.iotimc.devicecenter.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.dao.DevProductEntityRepository;
import com.iotimc.devicecenter.dao.DevProductdtlEntityRepository;
import com.iotimc.devicecenter.domain.DevProductEntity;
import com.iotimc.devicecenter.domain.DevProductdtlEntity;
import com.iotimc.devicecenter.domain.ProductConfig;
import com.iotimc.devicecenter.listener.ConfigListener;
import com.iotimc.devicecenter.service.ProductService;
import com.iotimc.devicecenter.util.Tool;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private DevProductEntityRepository devProductEntityRepository;

    @Autowired
    private DevProductdtlEntityRepository devProductdtlEntityRepository;

    @Override
    public List<DevProductEntity> listByCompany(Integer id) {
        return null;
    }

    @Override
    public Page<DevProductEntity> pageByCompany(Integer id) {
        return null;
    }

    @Override
    public JSONObject info(Integer id) {
        return null;
    }

    @Override
    public String add(JSONObject data) {
        DevProductEntity product = new DevProductEntity();
        product.setAlias(data.getString("alias"));
        if(!StringUtils.isBlank(data.getString("apikey"))) product.setApikey(data.getString("apikey"));
        if(!StringUtils.isBlank(data.getString("token"))) product.setToken(data.getString("token"));
        product.setCompanyfk(data.getInteger("companyfk"));
        if(!StringUtils.isBlank(data.getString("notes"))) product.setNotes(data.getString("notes"));
        product.setStatus("P");
        product.setName(data.getString("name"));
        JSONArray dtllist = data.getJSONArray("dtllist");
        // 检查属性是否有问题
        List<String> names = new ArrayList<>();
        for(int i=0; i<dtllist.size(); i++) {
            JSONObject item = dtllist.getJSONObject(i);
            String[] emptyNames = Tool.isBlanks(item, new String[]{"ishex", "type", "name", "objid", "resid", "insid", "alias", "writeable"});
            if(emptyNames.length > 0) return "第" + (i + 1) + "个属性信息不全,[" + Tool.joinString(emptyNames) + "]为空";
            String name = item.getString("name");
            String dsid = item.getString("objid") + "_" + item.getString("insid") + "_" + item.getString("resid");
            if(names.contains(name) || names.contains(dsid)) return "属性:" + name + "[" + dsid + "]配置重复";
            names.add(name);
            names.add(dsid);
        }
        product = devProductEntityRepository.save(product);
        for(int i=0; i<dtllist.size(); i++) {
            JSONObject item = dtllist.getJSONObject(i);
            DevProductdtlEntity dtl = new DevProductdtlEntity();
            dtl.setProductfk(product.getId());
            dtl.setIshex(item.getByte("ishex"));
            if(!StringUtils.isBlank(item.getString("editable"))) dtl.setEditable(item.getByte("editable"));
            dtl.setType(item.getString("type"));
            if(!StringUtils.isBlank(item.getString("correct"))) dtl.setCorrect(item.getString("correct"));
            dtl.setName(item.getString("name"));
            dtl.setObjid(item.getInteger("objid"));
            dtl.setResid(item.getInteger("resid"));
            dtl.setInsid(item.getByte("insid"));
            dtl.setAlias(item.getString("alias"));
            dtl.setWriteable(item.getByte("writeable"));
            dtl.setStatus("P");
            if(!StringUtils.isBlank(item.getString("notes")))dtl.setNotes(item.getString("notes"));
            devProductdtlEntityRepository.save(dtl);
        }
        ConfigListener.readProductConfig(product.getId());
        return String.valueOf(product.getId());
    }

    @Override
    public String mod(JSONObject data) {
        return null;
    }

    @Override
    public JSONObject getProductByDevid(Integer id) {
        return null;
    }

    @Override
    public String del(Integer id) {
        try {
            devProductEntityRepository.deleteById(id);
            devProductdtlEntityRepository.deleteByProductfk(id);
        } catch(Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return String.valueOf(id);
    }

    @Override
    public String addProps(JSONObject props) {
        ProductConfig product = ConfigListener.getProductById(props.getInteger("productfk"));
        Map<String, DevProductdtlEntity> dtllist = product.getDtllist();
        JSONArray datalist = props.getJSONArray("props");
        for(int i=0; i<datalist.size(); i++) {
            JSONObject data = datalist.getJSONObject(i);
            for(Map.Entry<String, DevProductdtlEntity> item : dtllist.entrySet()) {
                DevProductdtlEntity dtl = item.getValue();
                if(item.getKey().equals(data.getString("name"))) return "属性名" + dtl.getName() + "已存在";
                else if(dtl.getObjid() == data.getInteger("objid") && dtl.getResid() == data.getInteger("resid") && dtl.getInsid() == data.getByte("insid"))
                    return data.getString("name") + "的映射值[" + data.getInteger("objid") + "_" + data.getByte("insid") + "_" + data.getInteger("resid") + "]已存在";
                DevProductdtlEntity entity = new DevProductdtlEntity();
                entity.setStatus("P");
                entity.setWriteable(data.getByte("writeable"));
                entity.setAlias(data.getString("alias"));
                entity.setNotes(data.containsKey("notes")?data.getString("notes"):null);
                entity.setInsid(data.getByte("insid"));
                entity.setObjid(data.getInteger("objid"));
                entity.setResid(data.getInteger("resid"));
                entity.setName(data.getString("name"));
                entity.setIshex(data.containsKey("ishex")?data.getByte("ishex"):0);
                entity.setEditable(data.containsKey("editable")?data.getByte("editable"):0);
                entity.setCorrect(data.containsKey("correct")?data.getString("correct"):null);
                entity.setType(data.getString("type"));
                entity.setProductfk(product.getId());
                devProductdtlEntityRepository.save(entity);
            }
        }
        ConfigListener.readProductConfig(product.getId());
        return "0";
    }
}

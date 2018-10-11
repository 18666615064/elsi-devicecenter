package com.iotimc.devicecenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.domain.DevTemplateEntity;
import com.iotimc.devicecenter.service.TemplateService;
import com.iotimc.elsi.bean.PageRequestBean;
import org.springframework.data.domain.Page;

public class TemplateServiceImpl implements TemplateService {
    @Override
    public int add(JSONObject data) {
        return 0;
    }

    @Override
    public int mod(JSONObject data) {
        return 0;
    }

    @Override
    public int delete(Integer id) {
        return 0;
    }

    @Override
    public Page<DevTemplateEntity> getList(PageRequestBean page) {
        return null;
    }

    @Override
    public DevTemplateEntity getItem(Integer id) {
        return null;
    }
}

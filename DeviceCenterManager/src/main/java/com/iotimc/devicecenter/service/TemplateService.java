package com.iotimc.devicecenter.service;

import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.domain.DevTemplateEntity;
import com.iotimc.elsi.bean.PageRequestBean;
import org.springframework.data.domain.Page;

public interface TemplateService {
    int add(JSONObject data);

    int mod(JSONObject data);

    int delete(Integer id);

    Page<DevTemplateEntity> getList(PageRequestBean page);

    DevTemplateEntity getItem(Integer id);

}

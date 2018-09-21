package com.iotimc.devicecenter.service;

import com.iotimc.devicecenter.domain.DevDeviceEntity;
import com.iotimc.elsi.bean.PageRequestBean;
import com.iotimc.elsi.bean.SortRequestBean;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DeviceNormalService {
    Page<DevDeviceEntity> devicePageByProduct(Integer id, PageRequestBean pageRequestBean, List<SortRequestBean> sorts);

    List<DevDeviceEntity> deviceListByProduct(Integer id);
}

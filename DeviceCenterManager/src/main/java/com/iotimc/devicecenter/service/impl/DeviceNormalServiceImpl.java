package com.iotimc.devicecenter.service.impl;

import com.iotimc.devicecenter.dao.DevDeviceEntityRepository;
import com.iotimc.devicecenter.domain.DevDeviceEntity;
import com.iotimc.devicecenter.service.DeviceNormalService;
import com.iotimc.elsi.bean.PageRequestBean;
import com.iotimc.elsi.bean.SortRequestBean;
import com.iotimc.elsi.util.query.PageBeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceNormalServiceImpl implements DeviceNormalService {
    @Autowired
    private DevDeviceEntityRepository devDeviceEntityRepository;

    @Override
    public List<DevDeviceEntity> deviceListByProduct(Integer id) {
        return devDeviceEntityRepository.listByProduct(id);
    }

    @Override
    public Page<DevDeviceEntity> devicePageByProduct(Integer id, PageRequestBean pageRequestBean, List<SortRequestBean> sorts) {
        Pageable pageable = PageBeanConvertUtil.convertPageable(pageRequestBean, sorts);
        return devDeviceEntityRepository.pageByProduct(id, pageable);
    }
}

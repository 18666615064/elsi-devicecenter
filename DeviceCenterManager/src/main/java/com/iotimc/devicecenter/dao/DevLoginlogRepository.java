package com.iotimc.devicecenter.dao;

import com.iotimc.devicecenter.domain.DevLoginlogEntity;
import com.slyak.spring.jpa.GenericJpaRepository;
import com.slyak.spring.jpa.TemplateQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface DevLoginlogRepository extends GenericJpaRepository<DevLoginlogEntity, Integer> {
    @TemplateQuery
    List<Map> getList(@Param("starttime") String starttime, @Param("endtime") String endtime, @Param("imei") String imei);
}

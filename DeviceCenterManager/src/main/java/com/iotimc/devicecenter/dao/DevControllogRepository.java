package com.iotimc.devicecenter.dao;

import com.iotimc.devicecenter.domain.DevControllogEntity;
import com.slyak.spring.jpa.GenericJpaRepository;
import com.slyak.spring.jpa.TemplateQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DevControllogRepository extends GenericJpaRepository<DevControllogEntity, Integer> {
    @TemplateQuery
    public List<DevControllogEntity> getTop(@Param("imei")String imei, @Param("size")int size);
}

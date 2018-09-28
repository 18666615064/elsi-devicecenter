package com.iotimc.devicecenter.dao;

import com.iotimc.devicecenter.domain.DevProductEntity;
import com.slyak.spring.jpa.GenericJpaRepository;
import com.slyak.spring.jpa.TemplateQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface DevProductEntityRepository extends GenericJpaRepository<DevProductEntity, Integer> {
    @TemplateQuery
    List<DevProductEntity> getList();

    @TemplateQuery
    List<Map> getAll(@Param("productid") Integer productid);
}

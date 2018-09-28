package com.iotimc.devicecenter.dao;

import com.iotimc.devicecenter.domain.DevDeviceEntity;
import com.slyak.spring.jpa.GenericJpaRepository;
import com.slyak.spring.jpa.TemplateQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface DevDeviceEntityRepository extends GenericJpaRepository<DevDeviceEntity, Integer> {
    @TemplateQuery
    List<DevDeviceEntity> getAllList();

    @TemplateQuery
    Page<DevDeviceEntity> pageByProduct(@Param("id") Integer id, Pageable page);

    @TemplateQuery
    List<DevDeviceEntity> listByProduct(@Param("id") Integer id);
}

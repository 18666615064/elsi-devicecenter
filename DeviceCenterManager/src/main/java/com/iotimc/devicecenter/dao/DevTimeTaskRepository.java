package com.iotimc.devicecenter.dao;

import com.iotimc.devicecenter.domain.DevTimeTaskEntity;
import com.slyak.spring.jpa.GenericJpaRepository;
import com.slyak.spring.jpa.TemplateQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DevTimeTaskRepository extends GenericJpaRepository<DevTimeTaskEntity, Integer> {
    @TemplateQuery
    List<DevTimeTaskEntity> getRunList(@Param("id")Integer id);
}

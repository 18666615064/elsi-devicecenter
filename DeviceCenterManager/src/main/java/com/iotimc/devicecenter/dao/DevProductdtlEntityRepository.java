package com.iotimc.devicecenter.dao;

import com.iotimc.devicecenter.domain.DevProductdtlEntity;
import com.slyak.spring.jpa.GenericJpaRepository;
import com.slyak.spring.jpa.TemplateQuery;
import org.springframework.data.repository.query.Param;

public interface DevProductdtlEntityRepository extends GenericJpaRepository<DevProductdtlEntity, Integer>{
    void deleteByProductfk(@Param("productfk") Integer productfk);
}

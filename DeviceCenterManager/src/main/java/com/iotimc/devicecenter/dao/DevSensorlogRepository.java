package com.iotimc.devicecenter.dao;

import com.iotimc.devicecenter.domain.DevSensorlogEntity;
import com.slyak.spring.jpa.GenericJpaRepository;
import com.slyak.spring.jpa.TemplateQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface DevSensorlogRepository extends GenericJpaRepository<DevSensorlogEntity, Integer> {
    @TemplateQuery
    List<Map> getTop(@Param("imei") String imei, @Param("name") String name, @Param("size") int size);

    @TemplateQuery
    List<Map> getLast(@Param("imei") String imei, @Param("name") String name, @Param("size") int size);
}

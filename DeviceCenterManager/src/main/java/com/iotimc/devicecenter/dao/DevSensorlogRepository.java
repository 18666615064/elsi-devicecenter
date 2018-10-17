package com.iotimc.devicecenter.dao;

import com.iotimc.devicecenter.domain.DevSensorlogEntity;
import com.slyak.spring.jpa.GenericJpaRepository;
import com.slyak.spring.jpa.TemplateQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface DevSensorlogRepository extends GenericJpaRepository<DevSensorlogEntity, Integer> {
    @TemplateQuery
    List<Map> getTop(@Param("imei") String imei, @Param("name") String name, @Param("value") String value, @Param("size") int size);

    @TemplateQuery
    List<Map> getLast(@Param("imei") String imei, @Param("name") String name, @Param("size") int size);

    @TemplateQuery
    List<Map> getLastGroup(@Param("imei") String imei, @Param("name") String name, @Param("size") int size);

    @TemplateQuery
    List<Map> getList(@Param("starttime") String starttime, @Param("endtime") String endtime, @Param("imei") String imei, @Param("name") String name);

    @TemplateQuery
    List<Map> getLastGroupByTime(@Param("imei") String imei, @Param("name") String name, @Param("starttime") String starttime, @Param("endtime") String endtime);
}

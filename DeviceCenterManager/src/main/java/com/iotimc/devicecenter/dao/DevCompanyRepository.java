package com.iotimc.devicecenter.dao;

import com.iotimc.devicecenter.domain.DevCompanyEntity;
import com.slyak.spring.jpa.GenericJpaRepository;
import com.slyak.spring.jpa.TemplateQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface DevCompanyRepository extends GenericJpaRepository<DevCompanyEntity, Integer> {
    @TemplateQuery
    Page<Map> getPage(Pageable pageable);
}

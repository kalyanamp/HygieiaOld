package com.capitalone.dashboard.service;


import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CollectorPropertiesService {

    CollectorProperties create(CollectorProperties collectorProperties) throws HygieiaException;

    CollectorProperties getByName(String name) throws HygieiaException;

    Page<CollectorProperties> collectorPropertiesWithFilter(String filter, Pageable pageable);
}

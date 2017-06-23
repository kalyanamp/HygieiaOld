package com.capitalone.dashboard.service;


import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorProperties;

public interface CollectorPropertiesService {

    CollectorProperties create(CollectorProperties collectorProperties) throws HygieiaException;
}

package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorProperties;
import com.capitalone.dashboard.repository.CollectorPropertiesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectorPropertiesServiceImpl implements CollectorPropertiesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorPropertiesServiceImpl.class);

    private final CollectorPropertiesRepository collectorPropertiesRepository;

    @Autowired
    public CollectorPropertiesServiceImpl(CollectorPropertiesRepository collectorPropertiesRepository) {
        this.collectorPropertiesRepository = collectorPropertiesRepository;
    }
    public CollectorProperties create(CollectorProperties collectorProperties) throws HygieiaException{
        LOGGER.info("Successfully called service");
        collectorPropertiesRepository.save(collectorProperties);
//        Enumeration enuKeys = collectorProperties.keys();
//        while (enuKeys.hasMoreElements()) {
//            String key = (String) enuKeys.nextElement();
//            if(key.contains("dbusername")){
//
//            }
//            String value = collectorProperties.getProperty(key);
//            System.out.println(key + ": " + value);
//        }
        return null;
    }
}

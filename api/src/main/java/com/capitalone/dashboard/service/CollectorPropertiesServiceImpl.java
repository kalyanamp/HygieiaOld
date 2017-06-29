package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorProperties;
import com.capitalone.dashboard.repository.CollectorPropertiesRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

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

        Iterator<Map.Entry<Object,Object>> iter = collectorProperties.getProperties().entrySet().iterator();
        Properties collectorPropertiesForSubmit = new Properties();
        while (iter.hasNext()) {
            Map.Entry<Object,Object> entry = iter.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            if(key.indexOf(".") != -1){
                key = key.substring(key.indexOf(".") + 1);
                if(!key.equals("name")){
                    collectorPropertiesForSubmit.put(key,value);
                }else{
                    collectorProperties.setName(value);
                    CollectorProperties prop = getByName(value);
                    
                    if(prop != null){
                        collectorProperties.setId(prop.getId());
                    }

                }
            }
        }
        if(collectorProperties.getName() == null || collectorProperties.getName().isEmpty()){
            throw new HygieiaException("Name field not found.", HygieiaException.BAD_DATA);
        }
        collectorProperties.setProperties(collectorPropertiesForSubmit);
        return collectorPropertiesRepository.save(collectorProperties);

    }
    public CollectorProperties getByName(String name) throws HygieiaException{
        if(name == null || name.isEmpty()){
            throw new HygieiaException("Name field not found.", HygieiaException.BAD_DATA);
        }
        return collectorPropertiesRepository.findByName(name);
    }
    public CollectorProperties get(ObjectId id) throws HygieiaException{
        if(id == null){
            throw new HygieiaException("No Object ID found.", HygieiaException.BAD_DATA);
        }
        return collectorPropertiesRepository.findOne(id);
    }

}

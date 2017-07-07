package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorProperties;
import com.capitalone.dashboard.repository.CollectorPropertiesRepository;
import com.capitalone.dashboard.request.CollectorPropertiesRequest;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    @Override
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
    @Override
    public Page<CollectorProperties> collectorPropertiesWithFilter(String filter, Pageable pageable) {
        Page<CollectorProperties> propertiesString = collectorPropertiesRepository.findAllByNameContainingIgnoreCase(filter, pageable);
        return propertiesString;
    }
    @Override
    public CollectorProperties update(CollectorProperties collectorProperties) throws HygieiaException{
        if(collectorProperties.getName() == null || collectorProperties.getName().isEmpty()){
            throw new HygieiaException("Name field not found.", HygieiaException.BAD_DATA);
        }else{
            CollectorProperties properties = getByName(collectorProperties.getName());
            properties.getProperties().putAll(collectorProperties.getProperties());
            return collectorPropertiesRepository.save(properties);
        }
    }
    @Override
    public CollectorProperties remove(CollectorPropertiesRequest collectorPropertiesRequest) throws HygieiaException{
        if(collectorPropertiesRequest.getName() == null || collectorPropertiesRequest.getName().isEmpty()){
            throw new HygieiaException("Name field not found.", HygieiaException.BAD_DATA);
        }else{
            CollectorProperties properties = getByName(collectorPropertiesRequest.getName());
            String key = collectorPropertiesRequest.getPropertiesKey();
            if(key != null && !key.isEmpty()){
                properties.getProperties().remove(key);
                return collectorPropertiesRepository.save(properties);
            }else{
                throw new HygieiaException("Key field not found.", HygieiaException.BAD_DATA);
            }

        }
    }
}

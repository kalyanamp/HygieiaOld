package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CollectorProperties;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * {@link CollectorProperties} repository.
 */
public interface CollectorPropertiesRepository extends CrudRepository<CollectorProperties, ObjectId> {

    CollectorProperties findByName(String name);
}

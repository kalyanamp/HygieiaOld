package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CollectorProperties;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;



/**
 * {@link CollectorProperties} repository.
 */
public interface CollectorPropertiesRepository extends CrudRepository<CollectorProperties, ObjectId> {

    CollectorProperties findByName(String name);

    Page<CollectorProperties> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

}

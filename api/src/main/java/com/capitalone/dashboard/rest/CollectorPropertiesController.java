package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorProperties;
import com.capitalone.dashboard.request.CollectorPropertiesRequest;
import com.capitalone.dashboard.service.CollectorPropertiesService;
import com.capitalone.dashboard.service.CollectorPropertiesServiceImpl;
import com.capitalone.dashboard.util.PaginationHeaderUtility;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import javax.validation.Valid;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class CollectorPropertiesController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorPropertiesController.class);
    private final CollectorPropertiesService collectorPropertiesService;
    private PaginationHeaderUtility paginationHeaderUtility;

    @Autowired
    public CollectorPropertiesController(CollectorPropertiesService collectorPropertiesService,
                                         PaginationHeaderUtility paginationHeaderUtility) {
        this.collectorPropertiesService = collectorPropertiesService;
        this.paginationHeaderUtility = paginationHeaderUtility;
    }
    @RequestMapping(value = "/collectorProperties/propertiesFileUpload/", method = POST, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> fileUpload(MultipartHttpServletRequest request) {

        try {
            Iterator<String> itr=request.getFileNames();
            MultipartFile file = request.getFile(itr.next());



            Properties collectorProperties = new Properties();
            CollectorProperties properties = new CollectorProperties();
            collectorProperties.load(file.getInputStream());
            properties.setProperties(collectorProperties);
            collectorPropertiesService.create(properties);

            return ResponseEntity.ok("created");

        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(he.getMessage());
        }

    }
    @RequestMapping(value = "/collectorProperties/propertyList/", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CollectorProperties>> getPropertyList(@RequestParam(value = "search", required = false, defaultValue = "") String descriptionFilter, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {

        Page<CollectorProperties> pageOfConfigurationItems = collectorPropertiesService.collectorPropertiesWithFilter(descriptionFilter,pageable);

        return ResponseEntity
                .ok()
                .headers(paginationHeaderUtility.buildPaginationHeaders(pageOfConfigurationItems))
                .body(pageOfConfigurationItems.getContent());

    }
    @RequestMapping(value = "/collectorProperties/getSelectedProperty/{type}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectorProperties> getSelectedProperty(@PathVariable String type) {
        try {
            CollectorProperties properties = collectorPropertiesService.getByName(type);
            return ResponseEntity
                    .ok().body(properties);
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
    @RequestMapping(value = "/collectorProperties/updateProperties/", method = POST, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectorProperties> updateProperties( @Valid @RequestBody CollectorPropertiesRequest collectorPropertiesRequest) throws HygieiaException{

        try {
            CollectorProperties properties = collectorPropertiesRequest.toCollectorProperties();
            LOGGER.info(properties.getName());
            LOGGER.info("size: " + properties.getProperties().size());

            return ResponseEntity
                    .ok().body(collectorPropertiesService.create(properties));
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

    }

}

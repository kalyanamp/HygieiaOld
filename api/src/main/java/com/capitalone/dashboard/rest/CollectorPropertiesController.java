package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorProperties;
import com.capitalone.dashboard.service.CollectorPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class CollectorPropertiesController {

    private final CollectorPropertiesService collectorPropertiesService;

    @Autowired
    public CollectorPropertiesController(CollectorPropertiesService collectorPropertiesService) {
        this.collectorPropertiesService = collectorPropertiesService;
    }
    @RequestMapping(value = "propertiesFileUpload/", method = POST, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> fileUpload(MultipartHttpServletRequest request) {

        try {
            Iterator<String> itr=request.getFileNames();
            MultipartFile file = request.getFile(itr.next());



            Properties collectorProperties = new Properties();
            CollectorProperties properties = new CollectorProperties();
            collectorProperties.load(file.getInputStream());
            properties.setProperties(collectorProperties);
            collectorPropertiesService.create(properties);

        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(he.getMessage());
        }
        return null;
    }
}

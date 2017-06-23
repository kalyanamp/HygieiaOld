package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Properties;


@Document(collection="collector_properties")
public class CollectorProperties extends Properties {
}

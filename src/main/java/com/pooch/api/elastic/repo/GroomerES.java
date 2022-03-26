package com.pooch.api.elastic.repo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Dynamic;

@Document(indexName = "groomer", dynamic = Dynamic.TRUE)
public class GroomerES {

    @Id
    private Long id;
}

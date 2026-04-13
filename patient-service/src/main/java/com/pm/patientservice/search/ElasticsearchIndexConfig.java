package com.pm.patientservice.search;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@Configuration
public class ElasticsearchIndexConfig {

  @Bean
  public ApplicationRunner ensurePatientIndex(ElasticsearchOperations operations) {
    return args -> {
      var indexOps = operations.indexOps(PatientDocument.class);
      if (!indexOps.exists()) {
        indexOps.create();
        indexOps.putMapping(indexOps.createMapping(PatientDocument.class));
      }
    };
  }
}


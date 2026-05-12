package com.pm.patientservice.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@Configuration
public class ElasticsearchIndexConfig {

  private static final Logger logger = LoggerFactory.getLogger(ElasticsearchIndexConfig.class);
  private static final int MAX_RETRIES = 10;
  private static final int RETRY_DELAY_MS = 2000;

  @Bean
  public ApplicationRunner ensurePatientIndex(ElasticsearchOperations operations) {
    return args -> {
      new Thread(() -> {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
          try {
            var indexOps = operations.indexOps(PatientDocument.class);
            if (!indexOps.exists()) {
              indexOps.create();
              indexOps.putMapping(indexOps.createMapping(PatientDocument.class));
              logger.info("Elasticsearch index for PatientDocument created successfully");
            } else {
              logger.info("Elasticsearch index for PatientDocument already exists");
            }
            return; // Success, exit thread
          } catch (Exception e) {
            logger.warn("Attempt {}/{} to initialize Elasticsearch failed: {}",
                attempt, MAX_RETRIES, e.getMessage());
            if (attempt < MAX_RETRIES) {
              try {
                Thread.sleep(RETRY_DELAY_MS);
              } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                logger.error("Thread interrupted during Elasticsearch initialization retry");
                return;
              }
            } else {
              logger.error("Failed to initialize Elasticsearch after {} attempts. Continuing with service startup.", MAX_RETRIES);
            }
          }
        }
      }).start();
    };
  }
}


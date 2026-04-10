package com.pm.appointmentservice.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatientValidationGrpcClient {

  @Bean
  public Channel patientServiceChannel() {
    return ManagedChannelBuilder.forAddress("patient-service", 9000)
        .usePlaintext()
        .build();
  }
}



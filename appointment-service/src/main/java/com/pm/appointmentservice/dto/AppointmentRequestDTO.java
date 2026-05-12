package com.pm.appointmentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AppointmentRequestDTO {

  @NotNull(message = "Patient ID is required")
  private UUID patientId;

  @NotBlank(message = "Details are required")
  private String details;
}

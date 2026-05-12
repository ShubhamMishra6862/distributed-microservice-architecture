package com.pm.appointmentservice.dto;

import com.pm.appointmentservice.model.AppointmentStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AppointmentResponseDTO {

  private UUID id;
  private UUID patientId;
  private String details;
  private AppointmentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

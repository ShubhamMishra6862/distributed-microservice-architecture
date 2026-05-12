package com.pm.billingservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BillResponseDTO {

  private UUID id;
  private UUID patientId;
  private UUID appointmentId;
  private BigDecimal amount;
  private String description;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

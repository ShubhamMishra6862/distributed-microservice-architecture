
package com.pm.billingservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BillRequestDTO {

  @NotNull(message = "Patient ID is required")
  private UUID patientId;

  @NotNull(message = "Appointment ID is required")
  private UUID appointmentId;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
  private BigDecimal amount;

  @NotBlank(message = "Description is required")
  private String description;

  @NotBlank(message = "Status is required")
  private String status;
}

package com.pm.appointmentservice.dto;

import com.pm.appointmentservice.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusDTO {
  @NotNull(message = "Status is required")
  private AppointmentStatus status;

}

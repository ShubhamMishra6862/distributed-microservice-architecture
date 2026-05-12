package com.pm.patientservice.dto;

import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class PatientRequestDTO {

  @NotBlank(message = "Name is required",groups = {CreatePatientValidationGroup.class})
  @Size(max = 100, message = "Name cannot exceed 100 characters")
  private String name;

  @NotBlank(message = "Email is required",groups = {CreatePatientValidationGroup.class})
  @Email(message = "Email should be valid")
  private String email;

  @NotBlank(message = "Address is required",  groups = {CreatePatientValidationGroup.class})
  private String address;

  @Positive(message = "Weight must be a positive number")
  @NotNull(message = "Weight is required", groups = CreatePatientValidationGroup.class)
  private Double weight;

  @Positive(message = "Height must be a positive number")
  @NotNull(message = "Height is required", groups = CreatePatientValidationGroup.class)
  private Double height;

  @NotNull(message = "Date of birth is required",groups = {CreatePatientValidationGroup.class})
  @Past(message = "Date of birth must be in the past")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private LocalDate dateOfBirth;

  @NotNull(groups = CreatePatientValidationGroup.class, message =
          "Registered date is required")
  @PastOrPresent(message = "Registered date cannot be in the future")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private LocalDate registeredDate;
}
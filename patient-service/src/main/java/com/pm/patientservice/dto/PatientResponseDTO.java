package com.pm.patientservice.dto;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Data
public class PatientResponseDTO {
  private String id;
  private String name;
  private String email;
  private String address;
  private double height;
  private double weight;
  private LocalDate dateOfBirth;
}

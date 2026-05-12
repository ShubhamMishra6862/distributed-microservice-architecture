package com.pm.patientservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.time.LocalDate;

@Data
public class PatientSearchCriteria {

  private String q;

  @Size(max = 100, message = "Name cannot exceed 100 characters")
  private String name;

  @Email(message = "Email should be valid")
  private String email;

  @Size(max = 255, message = "Address cannot exceed 255 characters")
  private String address;

  @DateTimeFormat(iso = ISO.DATE)
  @PastOrPresent(message = "registeredFrom cannot be in the future")
  private LocalDate registeredFrom;

  @DateTimeFormat(iso = ISO.DATE)
  @PastOrPresent(message = "registeredTo cannot be in the future")
  private LocalDate registeredTo;

  @DateTimeFormat(iso = ISO.DATE)
  @Past(message = "dobFrom must be in the past")
  private LocalDate dobFrom;

  @DateTimeFormat(iso = ISO.DATE)
  @Past(message = "dobTo must be in the past")
  private LocalDate dobTo;

  //Pagination validations
  @Min(value = 0, message = "Page number cannot be negative")
  private Integer page = 0;

  @Min(value = 1, message = "Size must be at least 1")
  @Max(value = 100, message = "Size cannot exceed 100")
  private Integer size = 20;

  // Sorting validations
  @Pattern(regexp = "registeredDate|name|email",
          message = "SortBy must be one of: registeredDate, name, email")
  private String sortBy = "registeredDate";

  @Pattern(regexp = "asc|desc", message = "SortDir must be either 'asc' or 'desc'")
  private String sortDir = "desc";
}
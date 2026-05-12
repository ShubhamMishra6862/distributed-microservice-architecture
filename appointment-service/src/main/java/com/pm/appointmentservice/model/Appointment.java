package com.pm.appointmentservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "appointments")
public class Appointment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private UUID patientId;

  private String details;

  @Enumerated(EnumType.STRING)
  private AppointmentStatus status;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  // Constructors, getters, setters

  public Appointment() {}

  public Appointment(UUID patientId, String details, AppointmentStatus status) {
    this.patientId = patientId;
    this.details = details;
    this.status = status;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public void setStatus(AppointmentStatus status) {
    this.status = status;
    this.updatedAt = LocalDateTime.now();
  }
}

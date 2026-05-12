package com.pm.billingservice.model;

import jakarta.persistence.Entity;
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

  private UUID appointmentId;

  private UUID patientId;

  private String eventType;

  private LocalDateTime receivedAt;

  public Appointment() {}

  public Appointment(UUID appointmentId, UUID patientId, String eventType) {
    this.appointmentId = appointmentId;
    this.patientId = patientId;
    this.eventType = eventType;
    this.receivedAt = LocalDateTime.now();
  }
}

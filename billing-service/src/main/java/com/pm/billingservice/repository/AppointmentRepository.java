package com.pm.billingservice.repository;

import com.pm.billingservice.model.Appointment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
  boolean existsByAppointmentIdAndPatientId(UUID appointmentId, UUID patientId);
}

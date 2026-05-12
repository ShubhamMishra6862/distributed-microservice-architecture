package com.pm.billingservice.repository;

import com.pm.billingservice.model.Bill;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
    List<Bill> findByAppointmentId(UUID appointmentId);
    List<Bill> findByPatientId(UUID patientId);
}

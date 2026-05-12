package com.pm.billingservice.service;

import billing.BillingResponse;
import com.pm.billingservice.dto.BillRequestDTO;
import com.pm.billingservice.dto.BillResponseDTO;
import com.pm.billingservice.exception.BillNotFoundException;
import com.pm.billingservice.exception.InvalidBillingReferenceException;
import com.pm.billingservice.mapper.BillingMapper;
import com.pm.billingservice.model.Appointment;
import com.pm.billingservice.model.Bill;
import com.pm.billingservice.repository.AppointmentRepository;
import com.pm.billingservice.repository.BillRepository;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

  private final BillRepository billRepository;
  private final AppointmentRepository appointmentRepository;

  public BillingService(BillRepository billRepository,
                       AppointmentRepository appointmentRepository) {
    this.billRepository = billRepository;
      this.appointmentRepository = appointmentRepository;
  }

  public List<BillResponseDTO> getBills() {
    return billRepository.findAll().stream().map(BillingMapper::toDTO).toList();
  }

  public BillResponseDTO getBill(UUID id) {
    return BillingMapper.toDTO(findBillById(id));
  }

  public BillResponseDTO createBill(BillRequestDTO requestDTO) {
    validateAppointmentOwnership(requestDTO.getAppointmentId(),
        requestDTO.getPatientId());

    Bill bill = BillingMapper.toModel(requestDTO);
    return BillingMapper.toDTO(billRepository.save(bill));
  }

  public BillResponseDTO updateBill(UUID id, BillRequestDTO requestDTO) {
    validateAppointmentOwnership(requestDTO.getAppointmentId(),
        requestDTO.getPatientId());

    Bill existingBill = findBillById(id);
    BillingMapper.updateModel(existingBill, requestDTO);
    return BillingMapper.toDTO(billRepository.save(existingBill));
  }

  public void deleteBill(UUID id) {
    Bill bill = findBillById(id);
    billRepository.delete(bill);
  }

  private Bill findBillById(UUID id) {
    return billRepository.findById(id).orElseThrow(
        () -> new BillNotFoundException("Bill not found with ID: " + id));
  }

  public List<BillResponseDTO> getBillByAppointmentId(UUID appointmentId) {
    List<Bill> bills = billRepository.findByAppointmentId(appointmentId);
    return bills.stream().map(BillingMapper::toDTO).toList();
  }

  public List<BillResponseDTO> getBillsByPatientId(UUID patientId) {
    List<Bill> bills = billRepository.findByPatientId(patientId).stream()
        .filter(bill -> bill.getPatientId().equals(patientId)).toList();
    return bills.stream().map(BillingMapper::toDTO).toList();
  }

  private void validateAppointmentOwnership(UUID appointmentId, UUID patientId) {
    boolean isValidAppointmentPatientPair =
        appointmentRepository.existsByAppointmentIdAndPatientId(
            appointmentId, patientId);

    if (!isValidAppointmentPatientPair) {
      throw new InvalidBillingReferenceException(
          "No appointment mapping found for appointment ID " + appointmentId
              + " and patient ID " + patientId);
    }
  }

}

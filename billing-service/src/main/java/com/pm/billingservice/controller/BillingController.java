package com.pm.billingservice.controller;

import billing.BillingResponse;
import com.pm.billingservice.dto.BillRequestDTO;
import com.pm.billingservice.dto.BillResponseDTO;
import com.pm.billingservice.service.BillingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bills")
@Tag(name = "Billing", description = "API for managing Billing")
public class BillingController {

  private final BillingService billingService;

  public BillingController(BillingService billingService) {
    this.billingService = billingService;
  }

  @GetMapping
  public ResponseEntity<List<BillResponseDTO>> getBills() {
    return ResponseEntity.ok(billingService.getBills());
  }

  @GetMapping("/{id}")
  public ResponseEntity<BillResponseDTO> getBill(@PathVariable UUID id) {
    return ResponseEntity.ok(billingService.getBill(id));
  }

  @PostMapping
  public ResponseEntity<BillResponseDTO> createBill(
      @Valid @RequestBody BillRequestDTO billRequestDTO) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(billingService.createBill(billRequestDTO));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BillResponseDTO> updateBill(@PathVariable UUID id,
      @Valid @RequestBody BillRequestDTO billRequestDTO) {
    return ResponseEntity.ok(billingService.updateBill(id, billRequestDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBill(@PathVariable UUID id) {
    billingService.deleteBill(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/appointments/{appointmentId}")
  public ResponseEntity<List<BillResponseDTO>> getAppointments(
      @PathVariable UUID appointmentId) {
      List<BillResponseDTO> billingResponses = billingService.getBillByAppointmentId(appointmentId);
      return ResponseEntity.ok(billingResponses);
  }

  @GetMapping("/patients/{patientId}")
  public ResponseEntity<List<BillResponseDTO>> getPatients(@PathVariable UUID patientId) {
    List<BillResponseDTO> billingResponses = billingService.getBillsByPatientId(patientId);
    return ResponseEntity.ok(billingResponses);
  }
}

package com.pm.patientservice.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class PatientSearchCriteria {
  private String q;
  private String name;
  private String email;
  private String address;

  @DateTimeFormat(iso = ISO.DATE)
  private LocalDate registeredFrom;

  @DateTimeFormat(iso = ISO.DATE)
  private LocalDate registeredTo;

  @DateTimeFormat(iso = ISO.DATE)
  private LocalDate dobFrom;

  @DateTimeFormat(iso = ISO.DATE)
  private LocalDate dobTo;

  private Integer page = 0;
  private Integer size = 20;
  private String sortBy = "registeredDate";
  private String sortDir = "desc";

  public String getQ() {
    return q;
  }

  public void setQ(String q) {
    this.q = q;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public LocalDate getRegisteredFrom() {
    return registeredFrom;
  }

  public void setRegisteredFrom(LocalDate registeredFrom) {
    this.registeredFrom = registeredFrom;
  }

  public LocalDate getRegisteredTo() {
    return registeredTo;
  }

  public void setRegisteredTo(LocalDate registeredTo) {
    this.registeredTo = registeredTo;
  }

  public LocalDate getDobFrom() {
    return dobFrom;
  }

  public void setDobFrom(LocalDate dobFrom) {
    this.dobFrom = dobFrom;
  }

  public LocalDate getDobTo() {
    return dobTo;
  }

  public void setDobTo(LocalDate dobTo) {
    this.dobTo = dobTo;
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public String getSortDir() {
    return sortDir;
  }

  public void setSortDir(String sortDir) {
    this.sortDir = sortDir;
  }
}


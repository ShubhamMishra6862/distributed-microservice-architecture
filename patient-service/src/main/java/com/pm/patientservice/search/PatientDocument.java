package com.pm.patientservice.search;

import java.time.LocalDate;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "patients")
public class PatientDocument {
  @Id
  private String id;

  @Field(type = FieldType.Text)
  private String name;

  @Field(type = FieldType.Keyword)
  private String email;

  @Field(type = FieldType.Text)
  private String address;

  @Field(type = FieldType.Date, format = DateFormat.date)
  private LocalDate dateOfBirth;

  @Field(type = FieldType.Date, format = DateFormat.date)
  private LocalDate registeredDate;
}


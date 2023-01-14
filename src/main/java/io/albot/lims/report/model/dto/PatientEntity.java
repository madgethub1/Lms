package io.albot.lims.report.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@Entity
@TypeDef(name = "PatientEntity", typeClass = PatientEntity.class)
@Table(name = "patient_basic_info")
public class PatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id", columnDefinition = "SERIAL", nullable = false, updatable = false)
    private long patientId;
    @Column(name = "first_name")
    private String patientFirstName;
    @Column(name = "last_name")
    private String patientLastName;
    @Column(name = "address")
    private String patientAddress;
    @Column(name = "phone")
    private String patientPhone;
    @Column(name = "email")
    private String patientEmail;
    @Column(name = "dob")
    private Date patientDOB;
    @Column(name = "sex")
    private String patientSex;
    @Column(name = "clinician_name")
    private String clinicianName;
}

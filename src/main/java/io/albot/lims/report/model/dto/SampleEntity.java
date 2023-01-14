package io.albot.lims.report.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@Entity
@TypeDef(name = "SampleEntity", typeClass = SampleEntity.class)
@Table(name = "sample_info")
public class SampleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sample_id", columnDefinition = "SERIAL", nullable = false, updatable = false)
    private long sampleId;
    @Column(name = "submitted_sample_name")
    private String submittedSampleName;
    @Column(name = "record_type")
    private String recordType;
    @Column(name = "sample_genrate_id")
    private String sampleGenerateId;
    @Column(name = "test_ordered")
    private String testOrdered;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    @Column(name = "test_receive_date")
    private Date testReceiveDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    @Column(name = "test_report_date")
    private Date testReportDate;
    @Column(name = "sample_type")
    private String sampleType;
    @Column(name = "sample_collection_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date sampleCollectionDate;
    @Column(name = "PH")
    private String pH;
    @Column(name = "extraction_type")
    private String extractionType;
    @Column(name = "plate_id")
    private String plates;
    @Column(name = "sample_chose")
    private String sampleChose;
    @Column(name = "q_pcr_complete")
    private Boolean qPcrComplete;
    @Column(name = "elisa_complete")
    private Boolean elisaComplete;
    @Column(name = "report_approved")
    private Boolean reportApproved;
    @Column(name = "created_on")
    private Date createdOn;
    @Column(name = "created_by")
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    @Column(name = "recent_view_date")
    private Date recentViewDate;
    @Column(name = "user_id")
    private String userId;
    //@Column(name = "sample_status")
    //private String sampleStatus;

    @OneToOne
    @JoinColumn(name = "patientId", nullable = false, referencedColumnName = "patient_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PatientEntity patientId;
}

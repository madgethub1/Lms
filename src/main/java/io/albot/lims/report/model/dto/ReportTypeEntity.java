package io.albot.lims.report.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@Entity
@TypeDef(name = "ReportTypeEntity", typeClass = ReportTypeEntity.class)
@Table(name = "report_type")
public class ReportTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", columnDefinition = "bigserial", nullable = false, updatable = false)
    private long reportid;
    @Column(name = "report_type")
    private String reportType;
    @Column(name = "sample_type")
    private String sampleType;
}

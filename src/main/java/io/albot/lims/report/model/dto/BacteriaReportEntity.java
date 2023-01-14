package io.albot.lims.report.model.dto;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@ToString
@JsonDeserialize
@JsonPropertyOrder
//@TypeDef(name = "BacteriaReportEntity", typeClass = BacteriaReportEntity.class)
//@Table(name = "bacteria_report_info")
public class BacteriaReportEntity {
   // @Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
   // @Column(name = "bacteria_id", columnDefinition = "SERIAL", nullable = false, updatable = false)
    private long bacteria_id;
  //  @Column(name = "sample_id")
    private String sampleId;
  //  @Column(name = "target_name")
    private String targetName;
   // @Column(name = "delta_ct_mean")
    private String deltaCtMean;
}

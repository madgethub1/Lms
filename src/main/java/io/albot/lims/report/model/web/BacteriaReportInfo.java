package io.albot.lims.report.model.web;


import com.fasterxml.jackson.annotation.JsonProperty;
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
public class BacteriaReportInfo {
    @JsonProperty("SampleId")
    private String sampleId;
    @JsonProperty("TargetName")
    private String targetName;
    @JsonProperty("DeltaCtMean")
    private String deltaCtMean;
    private boolean validation;
    private String noBacteria;
}

package io.albot.lims.report.model.web;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@JsonPropertyOrder
public class SampleByPlateUpdate {

    @ApiModelProperty(value = "Field is used for holding plateId data as a String type.")
    private String plateId;
    @ApiModelProperty(value = "Field is used for holding sample_generated_id data as a String type.")
    private List<SampleUpdate> sampleUpdate;
}

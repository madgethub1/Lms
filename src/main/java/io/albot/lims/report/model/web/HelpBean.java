package io.albot.lims.report.model.web;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@JsonPropertyOrder
public class HelpBean {
    @ApiModelProperty(value = "Field is used for holding id data as a Long type.")
    private Long id;
    @ApiModelProperty(value = "Field is used for holding title data as a String type.")
    private String title;
    @ApiModelProperty(value = "Field is used for holding description data as a String type.")
    private String description;
    @ApiModelProperty(value = "Field is used for holding severityLevel data as a String type.")
    private String severityLevel;
}

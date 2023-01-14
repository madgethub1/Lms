package io.albot.lims.report.model.web;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@JsonPropertyOrder
public class ProtocolCreation {
    @ApiModelProperty(value = "Field is used for holding protocolId data as a Long type.", required = true)
    private long protocolId;
    @ApiModelProperty(value = "Field is used for holding protocolName data as a String type.")
    private String protocolName;
    @ApiModelProperty(value = "Field is used for holding protocolPDF data as a String type.")
    private String protocolPDF;
    @ApiModelProperty(value = "Field is used for holding isActive data as a String type.")
    private String isActive;
    @ApiModelProperty(value = "Field is used for holding createdOn data as a Date type.")
    private Date createdOn;
    @ApiModelProperty(value = "Field is used for holding createdBy data as a String type.")
    private String createdBy;
}

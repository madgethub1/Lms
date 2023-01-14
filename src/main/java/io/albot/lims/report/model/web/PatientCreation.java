package io.albot.lims.report.model.web;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class PatientCreation {
    @ApiModelProperty(value = "Field is used for holding patientId data as a Long type.", required = true)
    private long patientId;
    @ApiModelProperty(value = "Field is used for holding patientFirstName data as a String type.")
    private String patientFirstName;
    @ApiModelProperty(value = "Field is used for holding patientLastName data as a String type.")
    private String patientLastName;
    @ApiModelProperty(value = "Field is used for holding id patientAddress as a String type.")
    private String patientAddress;
    @ApiModelProperty(value = "Field is used for holding id patientPhone as a String type.")
    private String patientPhone;
    @ApiModelProperty(value = "Field is used for holding id patientEmail as a String type.")
    private String patientEmail;
    @ApiModelProperty(value = "Field is used for holding patientDOB data as a date type.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'", timezone = "Asia/Kolkata")
    private Date patientDOB;
    @ApiModelProperty(value = "Field is used for holding id patientSex as a String type.")
    private String patientSex;
    @ApiModelProperty(value = "Field is used for holding id clinicianName as a String type.")
    private String clinicianName;
}

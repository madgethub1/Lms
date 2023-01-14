package io.albot.lims.report.model.web;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ReportListContent {

    @ApiModelProperty(value = "Field is used for holding reportid data as a String type.")
    private long reportid;
    @ApiModelProperty(value = "Field is used for holding patientName data as a String type.")
    private String patientName;
    @ApiModelProperty(value = "Field is used for holding testReportDate data as a Date type.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date testReportDate;
    @ApiModelProperty(value = "Field is used for holding reportType data as a String type.")
    private String reportType;
    @ApiModelProperty(value = "Field is used for holding reportStatus data as a String type.")
    private String reportStatus;
}

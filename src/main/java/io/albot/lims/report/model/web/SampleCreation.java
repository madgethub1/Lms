package io.albot.lims.report.model.web;

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

import java.util.Date;


@Data

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@JsonPropertyOrder
public class SampleCreation {
    @ApiModelProperty(value = "Field is used for holding sampleId data as a Long type.")
    private long sampleId;
    @ApiModelProperty(value = "Field is used for holding submittedSampleName data as a String type.")
    private String submittedSampleName;
    @ApiModelProperty(value = "Field is used for holding recordType data as a String type.")
    private String recordType;
    @ApiModelProperty(value = "Field is used for holding sampleGenerateId data as a String type.")
    private String sampleGenerateId;
    @ApiModelProperty(value = "Field is used for holding testOrdered data as a String type.")
    private String testOrdered;
    @ApiModelProperty(value = "Field is used for holding testReceiveDate data as a Date type.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date testReceiveDate;
    @ApiModelProperty(value = "Field is used for holding testReportDate data as a Date type.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date testReportDate;
    @ApiModelProperty(value = "Field is used for holding sampleType data as a String type.")
    private String sampleType;
    @ApiModelProperty(value = "Field is used for holding sampleCollectionDate data as a Date type.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date sampleCollectionDate;
    @ApiModelProperty(value = "Field is used for holding vaginalPH data as a String type.")
    private String pH;
    @ApiModelProperty(value = "Field is used for holding extractionType data as a String type.")
    private String extractionType;
    @ApiModelProperty(value = "Field is used for holding plates data as a String type.")
    private String plates;
    @ApiModelProperty(value = "Field is used for holding sampleChose data as a String type.")
    private String sampleChose;
    @ApiModelProperty(value = "Field is used for holding patientFirstName data as a String type.")
    private String patientFirstName;
    @ApiModelProperty(value = "Field is used for holding patientLastName data as a String type.")
    private String patientLastName;
    @ApiModelProperty(value = "Field is used for holding patientAddress data as a String type.")
    private String patientAddress;
    @ApiModelProperty(value = "Field is used for holding patientPhone data as a String type.")
    private String patientPhone;
    @ApiModelProperty(value = "Field is used for holding patientEmail data as a String type.")
    private String patientEmail;
    @ApiModelProperty(value = "Field is used for holding patientDOB data as a Date type.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date patientDOB;
    @ApiModelProperty(value = "Field is used for holding patientSex data as a String type.")
    private String patientSex;
    @ApiModelProperty(value = "Field is used for holding clinicianName data as a String type.")
    private String clinicianName;
    @ApiModelProperty(value = "Field is used for holding stageFirst data as a Boolean type.")
    //@JsonProperty("qPcrComplete")
    private Boolean qPcrComplete;
    @ApiModelProperty(value = "Field is used for holding stageSecond data as a Boolean type.")
    //@JsonProperty("elisaComplete")
    private Boolean elisaComplete;
    @ApiModelProperty(value = "Field is used for holding stageThird data as a Boolean type.")
    //@JsonProperty("reportApproved")
    private Boolean reportApproved;
    @ApiModelProperty(value = "Field is used for holding createdBy data as a String type.")
    private String createdBy;
    @ApiModelProperty(value = "Field is used for holding recentViewDate data as a Date type.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date recentViewDate;
    @ApiModelProperty(value = "Field is used for holding userid of user who recently viewed sample")
    private String userId;
   // @ApiModelProperty(value = "Field is used for holding sampleStatus of user who recently viewed sample")
   // private String sampleStatus;
}

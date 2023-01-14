package io.albot.lims.report.model.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.albot.lims.report.model.dto.ProtocolEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@JsonPropertyOrder
public class PlatesCreation {
    @ApiModelProperty(value = "Field is used for holding id data as a Long type.")
    private long platesId;
    @ApiModelProperty(value = "Field is used for holding platesName data as a String type.")
    private String platesName;
    @ApiModelProperty(value = "Field is used for holding recordType data as a String type.")
    private String recordType;
    @ApiModelProperty(value = "Field is used for holding platesGeneratedId data as a String type.")
    private String platesGeneratedId;
    @ApiModelProperty(value = "Field is used for holding platesStatus data as a String type.")
    private String platesStatus;
    //@ApiModelProperty(value = "Field is used for holding platesCurrentProtocol data as a String type.")
    //private Integer platesCurrentProtocol;
    @ApiModelProperty(value = "Field is used for holding createdDate data as a date type.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date createdDate;
    @ApiModelProperty(value = "Field is used for holding recentViewDate data as a Date type.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date recentViewDate;
    @ApiModelProperty(value = "Field is used for holding userid of user who recently viewed sample")
    private Integer userId;
    @ApiModelProperty(value = "Field is used for holding List of Protocols")
    private List<ProtocolIdBean> protocols;
}

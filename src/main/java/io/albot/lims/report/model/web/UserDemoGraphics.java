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
public class UserDemoGraphics {
    @ApiModelProperty(value = "Field is used for holding id data as a Long type.", required = true)
    private Long id;
    @ApiModelProperty(value = "Field is used for holding phoneNumber data as a String type.")
    private String phoneNumber;
    @ApiModelProperty(value = "Field is used for holding userName data as a String type.")
    private String userName;
    @ApiModelProperty(value = "Field is used for holding patientDOB data as a Date type.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date patientDOB;
    @ApiModelProperty(value = "Field is used for holding gender data as a String type.")
    private String gender;
}

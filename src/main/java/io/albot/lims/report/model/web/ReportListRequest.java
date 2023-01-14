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
public class ReportListRequest {

	    @ApiModelProperty(value = "Field is used for holding reportType data as a String type.", required = true)
	    private String reportType;
	    @ApiModelProperty(value = "Field is used for holding emailId data as a String type.")
	    private String emailid;
	    @ApiModelProperty(value = "Field is used for holding startDate data as a Date type.")
	    @JsonFormat(pattern="yyyy-MM-dd")
	    private Date startDate;
	    @ApiModelProperty(value = "Field is used for holding endDate data as a String type.")
	    @JsonFormat(pattern="yyyy-MM-dd")
	    private Date endDate;
	    @ApiModelProperty(value = "Field is used for holding noOfMonths data as a String type.")
	    private long noOfMonths;
}

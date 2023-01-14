package io.albot.lims.report.model.web;


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
public class PaginationRequestBean {

    @ApiModelProperty(value = "Field is used for holding page data as a Long type.", required = true)
    private int page;
    @ApiModelProperty(value = "Field is used for holding sortField data as a String type.")
    private String sortField;
    @ApiModelProperty(value = "Field is used for holding sortDirection data as a String type.")
    private String sortDirection;
    @ApiModelProperty(value = "Field is used for holding recordPerPage data as a String type.", required = true)
    private int recordPerPage;

}

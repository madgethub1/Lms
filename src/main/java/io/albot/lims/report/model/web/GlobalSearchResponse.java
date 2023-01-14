package io.albot.lims.report.model.web;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.albot.lims.report.model.dto.PlatesEntity;
import io.albot.lims.report.model.dto.ProtocolEntity;
import io.albot.lims.report.model.dto.SampleEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@JsonPropertyOrder
public class GlobalSearchResponse {

    @ApiModelProperty(value = "Field is used for holding sampleData.")
    private Page<SampleEntity> sampleData;
    @ApiModelProperty(value = "Field is used for holding platesData.")
    private Page<PlatesEntity> platesData;
    @ApiModelProperty(value = "Field is used for holding protocolData.")
    private Page<ProtocolEntity> protocolData;

}

package io.albot.lims.report.exceptions;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@Data
public class ReportNotFoundException extends RuntimeException {
    private String errorMsg;
    private int errorCode;

    public ReportNotFoundException(String errorMsg, int errorCode) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }
}

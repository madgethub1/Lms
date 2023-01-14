package io.albot.lims.report.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.albot.lims.report.model.web.Response;

import javax.persistence.NonUniqueResultException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR = "Error: {}";
    private static final String FAILED = "Failed";

    @ExceptionHandler(PlateNotFoundException.class)
    public ResponseEntity<Response> exceptionHandler(PlateNotFoundException ex) {
        logger.error(ERROR, ex.getMessage());
        return ResponseEntity.ok(new Response().setStatus(FAILED)
                .setStatusCode(HttpStatus.NOT_FOUND.value())
                .setMessage(ex.getMessage()));
    }
    
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> exceptionHandler(NotFoundException ex) {
        logger.error(ERROR, ex.getMessage());
        return ResponseEntity.ok(new Response()
        		//.setStatus("Success")
                .setStatusCode(HttpStatus.NOT_FOUND.value())
                .setMessage(ex.getMessage()));
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<Response> exceptionHandler(ReportNotFoundException ex) {
        logger.error(ERROR, ex.getMessage());
        return ResponseEntity.ok(new Response()
                .setStatusCode(ex.getErrorCode())
                .setMessage(ex.getErrorMsg()));
    }

}

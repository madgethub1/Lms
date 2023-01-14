package io.albot.lims.report.exceptions;

public class PlateNotFoundException extends RuntimeException {
    public PlateNotFoundException(String message) {
        super("Error:" + message);
    }
}

package io.albot.lims.report.exceptions;

public class HelpException extends  RuntimeException{
    public HelpException(String message) {
        super("Error:" + message);
    }
}

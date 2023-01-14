package io.albot.lims.report.exceptions;

public class NotFoundException extends RuntimeException {
	 public NotFoundException(String message) {
	     super("Error:" + message);
	 }
}

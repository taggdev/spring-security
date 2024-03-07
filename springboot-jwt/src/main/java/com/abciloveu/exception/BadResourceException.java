package com.abciloveu.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * to indicate if the resource (Contact) is incomplete or in the wrong format
 */
@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadResourceException extends RuntimeException {

	private List<String> errorMessages = new ArrayList<>();

	public BadResourceException() {
	}

	public BadResourceException(String msg) {
		super(msg);
	}

	/**
	 * @return the errorMessages
	 */
	public List<String> getErrorMessages() {
		return errorMessages;
	}

	/**
	 * @param errorMessages the errorMessages to set
	 */
	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

	public void addErrorMessage(String message) {
		this.errorMessages.add(message);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
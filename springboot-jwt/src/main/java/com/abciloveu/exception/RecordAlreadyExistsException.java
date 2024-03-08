package com.abciloveu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an attempt to insert or update data
 * results in violation of an primary key or unique constraint.
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class RecordAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RecordAlreadyExistsException() {
		super();
	}

	public RecordAlreadyExistsException(String message) {
		super(message);
	}

	public RecordAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public RecordAlreadyExistsException(Throwable cause) {
		super(cause);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}

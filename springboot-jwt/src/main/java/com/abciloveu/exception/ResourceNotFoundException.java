package com.abciloveu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @deprecated Use {@link RecordNotFoundException} instead
 * to indicate that the resource is not available in the server (or database)
 */
@Deprecated
@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RecordNotFoundException {

	public ResourceNotFoundException() {
	}

	public ResourceNotFoundException(String msg) {
		super(msg);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
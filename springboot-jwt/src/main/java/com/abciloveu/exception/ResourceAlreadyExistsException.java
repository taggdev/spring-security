package com.abciloveu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @deprecated Use {@link RecordAlreadyExistsException} instead
 * to indicate that the resource is already available (conflict)
 */
@Deprecated
@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends RecordAlreadyExistsException {
	 
    public ResourceAlreadyExistsException() {
    }
 
    public ResourceAlreadyExistsException(String msg) {
        super(msg);
    }
    
    @Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
package com.abciloveu.exception;

public class PkConversionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PkConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public PkConversionException(String message) {
		super(message);
	}


	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
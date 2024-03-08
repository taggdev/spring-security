package com.abciloveu.web.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @see DefaultErrorAttributes 
 */
@JsonPropertyOrder(alphabetic = false)
@JsonInclude(Include.NON_NULL)
public class ApiError {

	private final Date timestamp;

	private final Integer status;

	/** ReasonPhrase */
	private String error;

	private String message;

	private String debugMessage;

	private List<Error> errors;
	
	public ApiError(Integer status, String error) {
		super();
		this.timestamp = new Date();
		this.status = status;
		this.error = error;
	}

	public ApiError(HttpStatus status) {
		this(status.value(), status.getReasonPhrase());
		
	}

	public ApiError(HttpStatus status, String error) {
		this(status.value(), error);
	}
	
	public ApiError(HttpStatus status, String error, String message) {
		this(status, error);
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public Integer getStatus() {
		return status;
	}

	private void addError(Error subError) {
		if (errors == null) {
			errors = new ArrayList<>();
		}
		errors.add(subError);
	}

	private void addValidationError(String object, String field, Object rejectedValue, String message) {
		addError(new ApiValidationError(object, field, rejectedValue, message));
	}

	private void addValidationError(String object, String message) {
		addError(new ApiValidationError(object, message));
	}

	/**
	 * Can unwrap fieldError to ConstraintViolation
	 * <code> ConstraintViolation<?> cv = fieldError.unwrap(ConstraintViolation.class) </code>
	 */
	private void addValidationError(FieldError fieldError) {
		this.addValidationError(
				fieldError.getObjectName(), 
				fieldError.getField(), 
				fieldError.getRejectedValue(),
				fieldError.getDefaultMessage());
	}

	private void addValidationError(ObjectError objectError) {
		this.addValidationError(
				objectError.getObjectName(), 
				objectError.getDefaultMessage());
	}
	
	public void addValidationErrors(List<ObjectError> validationErrors) {
		validationErrors.forEach(objectError -> {
			if(objectError instanceof FieldError) {
				addValidationError((FieldError)objectError);
			}
			else {
				addValidationError(objectError);
			}
		});
	}

		/**
		 * Utility method for adding error of ConstraintViolation. Usually when a @Validated
		 * validation fails.
		 *
		 * @param cv the ConstraintViolation
		 */
		private void addValidationError(ConstraintViolation<?> cv) {
			String objectClassName = cv.getLeafBean().getClass().getSimpleName();
//			String parentPath = ((PathImpl) cv.getPropertyPath()).getPathWithoutLeafNode().toString();
//			this.addValidationError(
//					StringUtils.isEmpty(parentPath)? objectClassName:parentPath,
//					((PathImpl) cv.getPropertyPath()).getLeafNode().asString(), 
//					cv.getInvalidValue(), 
//					cv.getMessage());
			
			this.addValidationError(objectClassName, cv.getPropertyPath().toString(), cv.getInvalidValue(), cv.getMessage());
		}

		public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
			constraintViolations.forEach(this::addValidationError);
		}

}
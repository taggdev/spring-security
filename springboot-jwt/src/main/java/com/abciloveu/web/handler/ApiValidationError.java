package com.abciloveu.web.handler;

import java.util.Objects;

public class ApiValidationError implements Error {

	private String object;

	private String field;

	private Object rejectedValue;

	private String message;

	ApiValidationError(String object, String message) {
		this.object = object;
		this.message = message;
	}

	public ApiValidationError(String object, String field, Object rejectedValue, String message) {
		this.object = object;
		this.field = field;
		this.rejectedValue = rejectedValue;
		this.message = message;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Object getRejectedValue() {
		return rejectedValue;
	}

	public void setRejectedValue(Object rejectedValue) {
		this.rejectedValue = rejectedValue;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

	@Override
	public int hashCode() {
		return Objects.hash(field, message, object);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ApiValidationError)) {
			return false;
		}
		
		final ApiValidationError other = (ApiValidationError) obj;
		return Objects.equals(object, other.object)
				&& Objects.equals(field, other.field) 
				&& Objects.equals(message, other.message);
	}

	@Override
	public String toString() {
		return new StringBuilder("ApiValidationError [")
			.append("object=").append(object)
			.append(", field=").append(field)
			.append(", rejectedValue=").append(rejectedValue)
			.append(", message=").append(message)
			.append("]").toString();
	}
	
	
	
	
}

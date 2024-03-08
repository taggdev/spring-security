package com.abciloveu.web.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.abciloveu.exception.PkConversionException;
import com.abciloveu.exception.RecordAlreadyExistsException;
import com.abciloveu.exception.RecordNotFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

	public RestResponseEntityExceptionHandler() {
		super();
	}

	/**
	 * For Validating Path Variables and Request Parameters
	 * https://mkyong.com/spring-boot/spring-rest-error-handling-example/
	 * 
	 * Handles javax.validation.ConstraintViolationException. Thrown when @Validated
	 * fails.
	 *
	 * @param ex the ConstraintViolationException
	 * @return the ApiError object
	 */

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
		final ApiError apiError = new ApiError(BAD_REQUEST, "Validation error", "Validation error");
		apiError.addValidationErrors(ex.getConstraintViolations());

		return buildResponseEntity(apiError);
	}

	/**
	 * javax.validation.ValidationException
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<Object> handleValidationException(ValidationException ex) {
		return buildResponseEntity(
				new ApiError(BAD_REQUEST, "Validation error", ExceptionUtils.getRootCauseMessage(ex)));
	}

	/**
	 * Handles RecordNotFoundException. Created to encapsulate errors with more detail
	 * than {@link RecordNotFoundException}.
	 *
	 * @param ex the EntityNotFoundException
	 * @return the ApiError object
	 */

	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex) {
		final ApiError apiError = new ApiError(NOT_FOUND, NOT_FOUND.getReasonPhrase(), ex.getMessage());

		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(RecordAlreadyExistsException.class)
	public ResponseEntity<Object> handleRecordAlreadyExistsException(RecordAlreadyExistsException ex) {
		final ApiError apiError = new ApiError(CONFLICT, "Record Already Exists", ex.getMessage());

		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
		if (ex instanceof LockedException) {
			return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED, "Account Locked", ex.getMessage()));
		}
		else if (ex instanceof DisabledException) {
			return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED, "Account Disabled", ex.getMessage()));
		}
		else if (ex instanceof AccountExpiredException) {
			return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED, "Account Expired", ex.getMessage()));
		}
		else if (ex instanceof CredentialsExpiredException) {
			return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED, "Credentials Expired", ex.getMessage()));
		}

		return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage()));

	}

	/**
	 * Handle DataAccessException, inspects the cause for different DB causes.
	 *
	 * @param ex the DataAccessException
	 * @return the ApiError object
	 */

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<Object> handleDataAccessException(DataAccessException ex, WebRequest request) {

		final String message = ExceptionUtils.getRootCauseMessage(ex);
		final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Database error", message);
		if (ex instanceof DataIntegrityViolationException) {

			Throwable cause = ex.getCause();
			if (cause instanceof javax.validation.ConstraintViolationException) {
				final javax.validation.ConstraintViolationException violation = (javax.validation.ConstraintViolationException) ex
						.getCause();
				apiError.addValidationErrors(violation.getConstraintViolations());
				//		        for (final ConstraintViolation<?> violation : consEx.getConstraintViolations()) {
				//		            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
				//		        }
			}
		}

		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
		final String message = Optional.ofNullable(ex.getMessage()).orElse("Invalid parameter");
		final ApiError apiError = new ApiError(BAD_REQUEST, BAD_REQUEST.getReasonPhrase(), message);

		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
		final String message = ExceptionUtils.getRootCauseMessage(ex);
		final ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(), message);

		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(TransactionException.class)
	public ResponseEntity<Object> handleTransactionException(TransactionException ex, WebRequest request) {
		final String message = ExceptionUtils.getRootCauseMessage(ex);
		final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Database error", message);

		return buildResponseEntity(apiError);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		final ApiError apiError = new ApiError(status, status.getReasonPhrase(),
				String.format("Missing required parameter: %s", ex.getParameterName()));

		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUnexpectedException(Exception ex, WebRequest request) {
		final String message = ExceptionUtils.getRootCauseMessage(ex);
		final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), message);

		LOG.error("{}", ex.getMessage(), ex);
		return buildResponseEntity(apiError);
	}

	/**
	 * error handle for @Valid
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		final ApiError apiError = new ApiError(status, status.getReasonPhrase(), "Validation error");
		apiError.addValidationErrors(ex.getBindingResult().getAllErrors());

		return buildResponseEntity(apiError);
	}
	
	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {

		final ApiError apiError = new ApiError(status, status.getReasonPhrase(), "Validation error");
		apiError.addValidationErrors(ex.getBindingResult().getAllErrors());

		return buildResponseEntity(apiError);

	}


	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String unsupported = "Unsupported content type: " + ex.getContentType();
		String supported = "Supported content types: " + MediaType.toString(ex.getSupportedMediaTypes());
		ApiError errorMessage = new ApiError(status, status.getReasonPhrase(), unsupported);
		errorMessage.setDebugMessage(supported);

		return buildResponseEntity(errorMessage);
	}

	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Throwable cause = ex.getMostSpecificCause();
		ApiError errorMessage;
		if (cause != null && cause instanceof JsonProcessingException) {
			errorMessage = new ApiError(status, status.getReasonPhrase(), "Malformed JSON request");
			errorMessage.setDebugMessage(((JsonProcessingException) cause).getOriginalMessage());
		}
		else if(StringUtils.contains(ex.getMessage(), "Required request body is missing")) {
			errorMessage = new ApiError(status, status.getReasonPhrase(), "Required request body is missing");
			errorMessage.setDebugMessage(ex.getMessage());
		}
		else {
			errorMessage = new ApiError(status, status.getReasonPhrase(), ex.getMessage());
		}

		return buildResponseEntity(errorMessage);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		final Throwable cause = ex.getMostSpecificCause();
		if (cause instanceof PkConversionException) {
			return buildResponseEntity(new ApiError(status, status.getReasonPhrase(), cause.getMessage()));
		}

		return super.handleTypeMismatch(ex, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
		}

		LOG.error("Exception: type={}, {}", ex.getClass(), ex.getMessage(), ex);
		final ApiError apiError = new ApiError(status, status.getReasonPhrase(), ex.getMessage());

		return buildResponseEntity(apiError, headers);
	}

	//---------------
	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return buildResponseEntity(apiError, new HttpHeaders());
	}

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError, HttpHeaders headers) {
		return ResponseEntity.status(apiError.getStatus()).headers(headers).body(apiError);
	}

}

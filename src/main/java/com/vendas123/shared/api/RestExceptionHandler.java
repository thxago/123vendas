package com.vendas123.shared.api;

import com.vendas123.shared.exception.BusinessException;
import com.vendas123.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
		log.debug("NotFoundException: {}", ex.getMessage());
		return error(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
		log.debug("BusinessException: {}", ex.getMessage());
		return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
	}

	@ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
	public ResponseEntity<Map<String, Object>> handleValidation(Exception ex) {
		log.debug("Validation exception: {}", ex.getMessage());
		return error(HttpStatus.BAD_REQUEST, "Validation error");
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		String name = ex.getName();
		Class<?> type = ex.getRequiredType();
		String requiredType = (type == null) ? "" : type.getSimpleName();
		String msg = "Invalid value for parameter '" + name + "'" + (requiredType.isEmpty() ? "" : ", expected " + requiredType);
		log.debug("Type mismatch: {} -> {}", name, ex.getValue());
		return error(HttpStatus.BAD_REQUEST, msg);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex) {
		log.debug("Malformed JSON: {}", ex.getMessage());
		return error(HttpStatus.BAD_REQUEST, "Malformed JSON request");
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
		log.warn("Data integrity violation", ex);
		return error(HttpStatus.UNPROCESSABLE_ENTITY, "Data integrity violation");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
		log.error("Unhandled exception", ex);
		return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
	}

	private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", Instant.now().toString());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		return new ResponseEntity<>(body, new HttpHeaders(), status);
	}
}

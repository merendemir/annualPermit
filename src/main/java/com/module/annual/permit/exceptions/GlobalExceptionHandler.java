package com.module.annual.permit.exceptions;

import com.module.annual.permit.util.ResponseCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private final ResponseCreator responseCreator;

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error ->{
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();

			errors.put(fieldName, errorMessage);
		});

		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleException(Exception e) {
		log.error("Exception :::{}" , e.getMessage());
		e.printStackTrace();
		return responseCreator.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "something.went.wrong", null);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<Object> HandleDataNotFoundException(DataNotFoundException e) {
		return responseCreator.createResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null, e.getParams());
	}

	@ExceptionHandler(DataNotAcceptableException.class)
	public ResponseEntity<Object> handleDataNotAcceptableException(DataNotAcceptableException e) {

		if (Objects.nonNull(e.getParams())) {
			return responseCreator.createResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null, e.getParams());
		} else {
			return responseCreator.createResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		}
	}

	@ExceptionHandler(DataAllReadyExistsException.class)
	public ResponseEntity<Object> handleDataAllReadyExistsException(DataAllReadyExistsException e) {
		return responseCreator.createResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
	}



}
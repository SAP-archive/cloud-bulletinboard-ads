package com.sap.bulletinboard.ads.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * A simple exception mapper for exceptions that also provides the error messages as part of the response. Gathers
 * all @ExceptionHandler methods in a single class so that exceptions from all controllers are handled consistently in
 * one place.
 */
@RestControllerAdvice
public class CustomExceptionMapper extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception,
            WebRequest request) {
        List<DetailError> errors = new ArrayList<DetailError>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String msg = violation.getRootBeanClass().getSimpleName() + " " + violation.getPropertyPath() + ": "
                    + violation.getMessage() + " [current value = " + violation.getInvalidValue() + "]";

            errors.add(new DetailError(msg));
        }

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage(), request,
                errors.toArray(new DetailError[errors.size()]));

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Here we have to override implementation of ResponseEntityExceptionHandler.
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        return convertToResponseEntity(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleBadRequestException(BadRequestException exception, WebRequest request) {
        return convertToResponseEntity(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception, WebRequest request) {
        return convertToResponseEntity(exception, HttpStatus.NOT_FOUND, request);
    }
    
    @ExceptionHandler
    public ResponseEntity<Object> handleNotAuthorizedException(NotAuthorizedException exception, WebRequest request) {
        return convertToResponseEntity(exception, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleAll(Exception exception, WebRequest request) {
        return convertToResponseEntity(exception, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> convertToResponseEntity(Exception exception, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(status, exception.getLocalizedMessage(), request,
                new DetailError(exception.getClass().getSimpleName() + ": error occurred"));

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), status);
    }

    /**
     * Common structure for an Error Response.
     */
    @JsonTypeName("error")
    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    public static class ApiError {
        private HttpStatus status;
        private String message; // user-facing (localizable) message, describing the error
        private String target; // endpoint of origin request
        private List<DetailError> details;

        public ApiError(HttpStatus status, String message, WebRequest request, DetailError... errors) {
            this.status = status;
            this.message = message;
            if (message == null) {
                this.message = status.getReasonPhrase();
            }
            this.details = Arrays.asList(errors);
            this.target = request.getDescription(false).substring(4);
        }

        public int getStatus() {
            return status.value();
        }

        public String getTarget() {
            return target;
        }

        public String getMessage() {
            return message;
        }

        public List<DetailError> getDetails() {
            return details;
        }

    }

    protected static class DetailError {
        private final String message; // user-facing (localizable) message, describing the error

        public DetailError(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}

package com.listingsearch.api.web;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of("field", e.getField(), "message", messageOf(e)))
                .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "One or more search parameters are invalid");
        problem.setTitle("Invalid search parameters");
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }

    private String messageOf(FieldError e) {
        if (e.isBindingFailure()) {
            if (e.contains(TypeMismatchException.class)) {
                Class<?> type = e.unwrap(TypeMismatchException.class).getRequiredType();
                if (type != null && Number.class.isAssignableFrom(type)) {
                    return "must be a number";
                }
            }
            return "invalid value";
        }
        return e.getDefaultMessage() == null ? "invalid" : e.getDefaultMessage();
    }
}

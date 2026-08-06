package com.kapil.jobtracker.common;


import com.kapil.jobtracker.auth.refresh.exception.RefreshTokenExpiredException;
import com.kapil.jobtracker.auth.refresh.exception.RefreshTokenNotFoundException;
import com.kapil.jobtracker.auth.refresh.exception.RefreshTokenRevokedException;
import com.kapil.jobtracker.company.exception.CompanyNotFoundException;
import com.kapil.jobtracker.interview.exception.InterviewNotFoundException;
import com.kapil.jobtracker.jobapplication.exception.DuplicateJobApplicationException;
import com.kapil.jobtracker.jobapplication.exception.InvalidApplicationStateException;
import com.kapil.jobtracker.jobapplication.exception.JobApplicationNotFoundException;
import com.kapil.jobtracker.notification.exception.NotificationNotFoundException;
import com.kapil.jobtracker.user.exception.EmailAlreadyRegisteredException;
import com.kapil.jobtracker.user.exception.RoleNotFoundException;
import com.kapil.jobtracker.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EmailAlreadyRegisteredException.class,
                        DuplicateJobApplicationException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException exception,
                                                        HttpServletRequest request){


        ErrorResponse error = new ErrorResponse(
                                        LocalDateTime.now(),
                                        HttpStatus.CONFLICT.value(),
                                        "Conflict",
                                        exception.getMessage(),
                                        request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFound(
            RoleNotFoundException exception,
            HttpServletRequest request
    ){
        ErrorResponse error = new ErrorResponse(
                                    LocalDateTime.now(),
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    "Internal server error",
                                    exception.getMessage(),
                                    request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ){
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors()
                .forEach(error-> errors.put(error.getField(), error.getDefaultMessage()));

        ValidationErrorResponse response=new ValidationErrorResponse(
                                    LocalDateTime.now(),
                                    HttpStatus.BAD_REQUEST.value(),
                                    "Bad Request",
                                    "Validation Failed",
                                    request.getRequestURI(),
                                    errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserNotFoundException.class,
                        CompanyNotFoundException.class,
                        JobApplicationNotFoundException.class,
                        RefreshTokenNotFoundException.class,
                        InterviewNotFoundException.class,
                        NotificationNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(
            RuntimeException exception,
            HttpServletRequest request
    ){
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                exception.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidApplicationStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidApplicationState(
            InvalidApplicationStateException exception,
            HttpServletRequest request
    ){
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Bad Request",
                exception.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({RefreshTokenExpiredException.class,
                        RefreshTokenRevokedException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            RuntimeException exception,
            HttpServletRequest request
    ){
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                exception.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "Duplicate record is not allowed",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }
}

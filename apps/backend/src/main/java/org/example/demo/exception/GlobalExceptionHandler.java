package org.example.demo.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
    @Autowired
    private MessageSource messageSource;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CustomErrors {
        @JsonProperty(value = "statusCode")
        Integer httpStatus;
        @JsonProperty(value = "errors")
        private Map<String, String> errors;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CustomError {
        @JsonProperty(value = "statusCode")
        Integer httpStatus;
        @JsonProperty(value = "error")
        private String error;
    }

    @ExceptionHandler(CustomExceptions.ExcelExportException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleExcelExportException(CustomExceptions.ExcelExportException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomExceptions.CustomFileErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleCustomFileErrorException(CustomExceptions.CustomFileErrorException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        System.out.println(ex.getMessage());
        String errorCode = "FailRequest";
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Required request body is missing")){
                errorCode = "RequestBodyMissing";
            }
            else if (ex.getMessage().contains("JSON parse error")){
                errorCode = "JsonParseError";
            }
        }
        String message = messageSource.getMessage(errorCode, new Object[]{}, LocaleContextHolder.getLocale());
        CustomError error = new CustomError(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex) {
        String errorCode = "FailRequest";
        String message = messageSource.getMessage(ex.getMessage(), new Object[]{}, LocaleContextHolder.getLocale());
        CustomError error = new CustomError(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        CustomError error = new CustomError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = messageSource.getMessage("typeMismatch", new Object[]{ex.getName()}, LocaleContextHolder.getLocale());
        CustomError customError = new CustomError();
        customError.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        customError.setError(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customError);
    }

    @ExceptionHandler({BindException.class})
    public ResponseEntity<?> handleBindException(BindException ex) {
        Map<String, String> mapErrors = new HashMap<>();
        List<FieldError> listError = ex.getFieldErrors();
        listError.forEach(s -> {
            String code = s.getCode();
            System.out.println("CODE: " + s.getCode());
            System.out.println(s.getDefaultMessage());
            System.out.println(s.getField());
            if (code != null) {
                if (code.equals("typeMismatch")) {
                    String message = messageSource.getMessage(code, new Object[]{s.getField()}, LocaleContextHolder.getLocale());
                    mapErrors.put(s.getField(), message);
                } else {
                    String message = messageSource.getMessage(s.getDefaultMessage(), new Object[]{}, LocaleContextHolder.getLocale());
                    mapErrors.put(s.getField(), message);
                }
            }
        });
        CustomErrors error = new CustomErrors(HttpStatus.BAD_REQUEST.value(), mapErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<?> handleBadRequestsException(BadRequestException ex) {
        CustomError error = new CustomError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(error);
    }

    @ExceptionHandler({CustomExceptions.CustomBadRequest.class})
    public ResponseEntity<?> handleCustomBadRequestException(CustomExceptions.CustomBadRequest ex) {
        CustomError error = new CustomError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(error);
    }


    @ExceptionHandler({CustomExceptions.ExistCodeException.class})
    public ResponseEntity<?> handleExistCodeException(CustomExceptions.ExistCodeException ex) {
        String message = messageSource.getMessage(ex.getMessage(), new Object[]{}, LocaleContextHolder.getLocale());
        CustomError error = new CustomError(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


}
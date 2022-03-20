package io.github.zodh.college.manager.interceptors;

import io.github.zodh.college.manager.exceptions.FlowException;
import io.github.zodh.model.ErrorResponse;
import java.util.ArrayList;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleSocketException(Exception exception) {
    var errorResponse = new ErrorResponse()
        .errorDescription("Oops, the server has encountered some errors... Try again later.");
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(FlowException.class)
  public ResponseEntity<ErrorResponse> handleFlowException(FlowException flowException) {
    var errorResponse = new ErrorResponse()
        .errorDescription(flowException.getErrorDescription())
        .requestId(flowException.getRequestId());
    return new ResponseEntity<>(errorResponse, flowException.getHttpStatus());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException methodArgumentNotValidException) {
    var requestId = MDC.get("requestId");
    var fieldErrors = methodArgumentNotValidException.getBindingResult().getFieldErrors();
    var globalErrors = methodArgumentNotValidException.getBindingResult().getGlobalErrors();
    var errors = new ArrayList<String>(fieldErrors.size() + globalErrors.size());
    fieldErrors.forEach(fieldError -> {
      var error = String.format("field <%s>, error <%s>",
          fieldError.getField(),
          fieldError.getDefaultMessage());
      errors.add(error);
    });
    globalErrors.forEach(globalError -> {
      var error = String.format("field <%s>, error <%s>",
          globalError.getObjectName(),
          globalError.getDefaultMessage());
      errors.add(error);
    });
    var errorResponse = new ErrorResponse()
        .errors(errors)
        .errorDescription("Request not valid")
        .requestId(requestId);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
}

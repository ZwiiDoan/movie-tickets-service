package per.duyd.interview.mts.controller;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import per.duyd.interview.mts.dto.TicketTransactionErrorResponse;
import per.duyd.interview.mts.enums.ErrorCode;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler({MethodArgumentNotValidException.class})
  protected ResponseEntity<TicketTransactionErrorResponse> handleBadRequest(
      MethodArgumentNotValidException ex) {
    logException(ex);
    String errorMessage =
        ex.getBindingResult().getAllErrors().stream().map(this::getFieldErrorMessage)
            .collect(Collectors.joining("; "));
    return new ResponseEntity<>(buildErrorResponse(ErrorCode.INVALID_REQUEST, errorMessage),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({HttpMessageNotReadableException.class,
      MethodArgumentTypeMismatchException.class})
  protected ResponseEntity<TicketTransactionErrorResponse> handleBadRequest(Exception ex) {
    logException(ex);
    return new ResponseEntity<>(buildErrorResponse(ErrorCode.INVALID_REQUEST,
        ExceptionUtils.getRootCause(ex).getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({Exception.class})
  protected ResponseEntity<TicketTransactionErrorResponse> handleOtherExceptions(Exception ex)
      throws Exception {
    logException(ex);

    if (ex instanceof ResponseStatusException) {
      throw ex;
    } else {
      return new ResponseEntity<>(buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR,
          ExceptionUtils.getRootCause(ex).getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private TicketTransactionErrorResponse buildErrorResponse(ErrorCode errorCode,
                                                            String errorMessage) {
    return TicketTransactionErrorResponse.builder().errorCode(errorCode)
        .errorMessage(errorMessage).build();
  }

  private String getFieldErrorMessage(ObjectError error) {
    if (error instanceof FieldError fieldError) {
      return String.format("%s %s", fieldError.getField(), fieldError.getDefaultMessage());
    } else {
      return String.format("%s %s", error.getObjectName(), error.getDefaultMessage());
    }
  }

  private void logException(Exception ex) {
    log.error("exception=\"{}\", rootCause=\"{}\"", ex.getClass().getName(),
        ExceptionUtils.getRootCauseMessage(ex));
    log.debug("exception details", ex);
  }
}

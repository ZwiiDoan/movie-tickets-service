package per.duyd.interview.mts.exception;

import per.duyd.interview.mts.enums.ErrorCode;

public class TicketConfigException extends RuntimeException {
  private final ErrorCode errorCode;

  public TicketConfigException(ErrorCode errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}

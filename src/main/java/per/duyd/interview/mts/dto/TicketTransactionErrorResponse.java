package per.duyd.interview.mts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import per.duyd.interview.mts.enums.ErrorCode;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketTransactionErrorResponse {
  private ErrorCode errorCode;
  private String errorMessage;
}

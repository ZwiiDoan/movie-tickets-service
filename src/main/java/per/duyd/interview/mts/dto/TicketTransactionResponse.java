package per.duyd.interview.mts.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketTransactionResponse {
  private long transactionId;

  private List<TicketGroup> tickets;

  private BigDecimal totalCost;
}

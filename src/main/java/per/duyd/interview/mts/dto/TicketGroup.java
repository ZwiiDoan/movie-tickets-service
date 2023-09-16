package per.duyd.interview.mts.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import per.duyd.interview.mts.enums.AgeGroup;

@Builder
@Data
public class TicketGroup {
  private AgeGroup ageGroup;

  private long quantity;

  private BigDecimal totalCost;
}

package per.duyd.interview.mts.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import per.duyd.interview.mts.enums.AgeGroup;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketConfig {
  @Positive
  private Integer minAge;

  @Positive
  private Integer maxAge;

  private AgeGroup baseGroup;

  private Double adjustPercent;

  @Positive
  private BigDecimal price;
}

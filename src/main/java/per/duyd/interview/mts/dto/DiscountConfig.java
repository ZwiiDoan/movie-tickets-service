package per.duyd.interview.mts.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountConfig {
  @NotNull
  private Double adjustPercent;

  @Positive
  @NotNull
  private Long minTickets;
}

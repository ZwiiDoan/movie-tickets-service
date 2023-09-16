package per.duyd.interview.mts.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import per.duyd.interview.mts.config.ApplicationConfig;
import per.duyd.interview.mts.dto.DiscountConfig;
import per.duyd.interview.mts.enums.AgeGroup;

@Service
@Slf4j
public class TicketDiscountService {

  private final Map<AgeGroup, DiscountConfig> discounts;

  public TicketDiscountService(ApplicationConfig applicationConfig) {
    this.discounts = applicationConfig.getDiscounts();
  }

  private double getDiscountPercent(AgeGroup ageGroup, long ticketCount) {
    return Optional.ofNullable(discounts.get(ageGroup))
        .filter(discount -> discount.getMinTickets() <= ticketCount)
        .map(DiscountConfig::getAdjustPercent)
        .orElse(0d);
  }

  @Observed(name = "Method",
      contextualName = "Get_Discounted_Cost",
      lowCardinalityKeyValues = {"method", "getDiscountedCost"})
  @Timed(value = "getDiscountedCost.timer", description = "Time taken to get discounted cost")
  public BigDecimal getDiscountedCost(AgeGroup ageGroup, long ticketCount,
                                      BigDecimal ticketPrice) {
    return BigDecimal.valueOf(ticketCount).multiply(ticketPrice)
        .multiply(BigDecimal.valueOf(1 + this.getDiscountPercent(ageGroup, ticketCount) / 100));
  }
}

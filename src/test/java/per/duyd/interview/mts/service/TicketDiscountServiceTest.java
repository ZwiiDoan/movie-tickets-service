package per.duyd.interview.mts.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import per.duyd.interview.mts.config.ApplicationConfig;
import per.duyd.interview.mts.dto.DiscountConfig;
import per.duyd.interview.mts.enums.AgeGroup;

class TicketDiscountServiceTest {

  private final Map<AgeGroup, DiscountConfig> discounts = Map.of(
      AgeGroup.Senior, DiscountConfig.builder().adjustPercent(-20d).minTickets(2L).build(),
      AgeGroup.Children, DiscountConfig.builder().adjustPercent(-30d).minTickets(3L).build()
  );

  private final TicketDiscountService ticketDiscountService = new TicketDiscountService(
      new ApplicationConfig(null, discounts)
  );

  @ParameterizedTest
  @CsvSource({
      "Senior,1,15,15.0", "Senior,2,15,24.0", "Senior,3,15,36.0",
      "Children,2,10,20.0", "Children,1,10,10.0", "Children,3,10,21.0", "Children,4,10,28.0"
  })
  void shouldGetDiscountedCost(AgeGroup ageGroup, long ticketCount, BigDecimal ticketPrice,
                               BigDecimal expectedDiscountPrice) {
    assertThat(ticketDiscountService.getDiscountedCost(ageGroup, ticketCount, ticketPrice))
        .isEqualTo(expectedDiscountPrice);
  }
}
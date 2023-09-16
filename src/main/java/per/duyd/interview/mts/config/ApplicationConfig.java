package per.duyd.interview.mts.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import per.duyd.interview.mts.dto.DiscountConfig;
import per.duyd.interview.mts.dto.TicketConfig;
import per.duyd.interview.mts.enums.AgeGroup;
import per.duyd.interview.mts.enums.ErrorCode;
import per.duyd.interview.mts.exception.TicketConfigException;

@Configuration
@ConfigurationProperties(prefix = "application")
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApplicationConfig {
  private Map<AgeGroup, TicketConfig> tickets;

  private Map<AgeGroup, DiscountConfig> discounts;

  @Bean
  TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

  @Bean
  ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
    return new ObservedAspect(observationRegistry);
  }

  @PostConstruct
  public void finaliseConfigs() {
    tickets.forEach((ageGroup, ticketConfig) -> {
      if (ticketConfig.getPrice() == null) {
        ticketConfig.setPrice(deriveTicketPrice(ageGroup, ticketConfig));
      }

      if (ticketConfig.getMinAge() == null) {
        ticketConfig.setMinAge(0);
      }

      if (ticketConfig.getMaxAge() == null) {
        ticketConfig.setMaxAge(Integer.MAX_VALUE);
      }
    });

    log.info("Ticket Configs: {}", tickets);
    log.info("Discounts Configs: {}", discounts);
  }

  private BigDecimal deriveTicketPrice(AgeGroup ageGroup, TicketConfig ticketConfig) {
    if (ticketConfig.getBaseGroup() == null || ticketConfig.getAdjustPercent() == null) {
      throw new TicketConfigException(ErrorCode.TICKET_PRICE_NOT_FOUND, "Cannot calculate ticket "
          + "price of age group: " + ageGroup);
    } else {
      return Optional.ofNullable(tickets.get(ticketConfig.getBaseGroup()))
          .map(TicketConfig::getPrice)
          .map(basePrice -> basePrice.multiply(
              BigDecimal.valueOf(1 + ticketConfig.getAdjustPercent() / 100))
          ).orElseThrow(() -> new TicketConfigException(ErrorCode.TICKET_PRICE_NOT_FOUND,
              "Cannot calculate ticket price of age group: " + ageGroup));
    }
  }
}

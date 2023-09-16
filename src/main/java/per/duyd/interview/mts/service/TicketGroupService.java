package per.duyd.interview.mts.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import per.duyd.interview.mts.config.ApplicationConfig;
import per.duyd.interview.mts.dto.TicketConfig;
import per.duyd.interview.mts.dto.TicketGroup;
import per.duyd.interview.mts.enums.AgeGroup;
import per.duyd.interview.mts.enums.ErrorCode;
import per.duyd.interview.mts.exception.TicketConfigException;

@Service
@Slf4j
public class TicketGroupService {
  private final Map<AgeGroup, TicketConfig> tickets;

  private final TicketDiscountService ticketDiscountService;

  public TicketGroupService(ApplicationConfig applicationConfig,
                            TicketDiscountService ticketDiscountService) {
    this.tickets = applicationConfig.getTickets();
    this.ticketDiscountService = ticketDiscountService;
  }

  @Observed(name = "Method",
      contextualName = "Build_Ticket_Group",
      lowCardinalityKeyValues = {"method", "buildTicketGroups"})
  @Timed(value = "buildTicketGroups.timer", description = "Time taken to build ticketGroups")
  public List<TicketGroup> buildTicketGroups(Map<AgeGroup, Long> ticketsByAgeGroup) {
    return ticketsByAgeGroup.entrySet().stream().map(entry -> {
      long ticketCount = entry.getValue();
      AgeGroup ageGroup = entry.getKey();
      BigDecimal totalCost = calculateTicketGroupCost(ageGroup, ticketCount);

      return TicketGroup.builder().quantity(ticketCount).ageGroup(ageGroup)
          .totalCost(totalCost).build();
    }).sorted(
        Comparator.comparing(ticketGroup -> ticketGroup.getAgeGroup().name())
    ).collect(Collectors.toList());
  }

  @Observed(name = "Method",
      contextualName = "Get_Age_Group",
      lowCardinalityKeyValues = {"method", "getAgeGroup"})
  @Timed(value = "getAgeGroup.timer", description = "Time taken to get ageGroup")
  public AgeGroup getAgeGroup(int age) {
    return tickets.entrySet().stream()
        .filter(entry -> {
          TicketConfig ticketConfig = entry.getValue();
          return ticketConfig.getMinAge() <= age && age <= ticketConfig.getMaxAge();
        })
        .map(Map.Entry::getKey)
        .findAny()
        .orElseThrow(() -> new TicketConfigException(ErrorCode.AGE_GROUP_NOT_FOUND,
            "Cannot find age group for age: " + age));
  }

  private BigDecimal calculateTicketGroupCost(AgeGroup ageGroup, long ticketCount) {
    return getTicketPrice(ageGroup).map(
            ticketPrice -> ticketDiscountService.getDiscountedCost(
                ageGroup, ticketCount, ticketPrice)
        )
        .map(cost -> cost.setScale(2, RoundingMode.HALF_EVEN))
        .orElseThrow(() -> new TicketConfigException(ErrorCode.TICKET_PRICE_NOT_FOUND,
            "Cannot find ticket price for ageGroup: " + ageGroup));
  }

  private Optional<BigDecimal> getTicketPrice(AgeGroup ageGroup) {
    return Optional.ofNullable(tickets.get(ageGroup)).map(TicketConfig::getPrice);
  }
}

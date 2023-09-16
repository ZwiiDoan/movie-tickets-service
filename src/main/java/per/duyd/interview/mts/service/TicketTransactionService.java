package per.duyd.interview.mts.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import per.duyd.interview.mts.dto.TicketGroup;
import per.duyd.interview.mts.dto.TicketTransactionRequest;
import per.duyd.interview.mts.dto.TicketTransactionResponse;
import per.duyd.interview.mts.enums.AgeGroup;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketTransactionService {

  private final TicketGroupService ticketGroupService;

  @Observed(name = "Method",
      contextualName = "Calculate_Cost",
      lowCardinalityKeyValues = {"method", "calculateCost"})
  @Timed(value = "calculateCost.timer", description = "Time taken to calculate transaction cost")
  public TicketTransactionResponse calculateCost(
      TicketTransactionRequest ticketTransactionRequest) {
    Map<AgeGroup, Long> ticketsByAgeGroup = groupTicketsByAgeGroup(ticketTransactionRequest);
    List<TicketGroup> ticketGroups = ticketGroupService.buildTicketGroups(ticketsByAgeGroup);
    BigDecimal totalCost = calculateTransactionCost(ticketGroups);

    return TicketTransactionResponse.builder()
        .transactionId(ticketTransactionRequest.getTransactionId())
        .tickets(ticketGroups)
        .totalCost(totalCost)
        .build();
  }

  @Observed(name = "Internal_Method",
      contextualName = "Group_Tickets_By_AgeGroup",
      lowCardinalityKeyValues = {"method", "groupTicketsByAgeGroup"})
  @Timed(value = "groupTicketsByAgeGroup.timer", description = "Time taken to group tickets by "
      + "ageGroup")
  private Map<AgeGroup, Long> groupTicketsByAgeGroup(
      TicketTransactionRequest ticketTransactionRequest) {
    return ticketTransactionRequest.getCustomers().stream()
        .collect(
            Collectors.groupingBy(
                customer -> ticketGroupService.getAgeGroup(customer.getAge()),
                Collectors.counting()
            )
        );
  }

  private BigDecimal calculateTransactionCost(List<TicketGroup> ticketGroups) {
    return ticketGroups.stream()
        .map(TicketGroup::getTotalCost)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_EVEN);
  }
}

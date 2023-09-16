package per.duyd.interview.mts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import per.duyd.interview.mts.dto.Customer;
import per.duyd.interview.mts.dto.TicketGroup;
import per.duyd.interview.mts.dto.TicketTransactionRequest;
import per.duyd.interview.mts.dto.TicketTransactionResponse;
import per.duyd.interview.mts.enums.AgeGroup;

@ExtendWith(MockitoExtension.class)
class TicketTransactionServiceTest {

  @Mock
  private TicketGroupService ticketGroupService;

  @InjectMocks
  private TicketTransactionService ticketTransactionService;

  private final List<TicketGroup> ticketGroups = List.of(
      TicketGroup.builder()
          .totalCost(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_EVEN))
          .quantity(2)
          .ageGroup(AgeGroup.Children)
          .build(),
      TicketGroup.builder()
          .totalCost(BigDecimal.valueOf(15).setScale(2, RoundingMode.HALF_EVEN))
          .quantity(1)
          .ageGroup(AgeGroup.Senior)
          .build(),
      TicketGroup.builder()
          .totalCost(BigDecimal.valueOf(36).setScale(2, RoundingMode.HALF_EVEN))
          .quantity(3)
          .ageGroup(AgeGroup.Teen)
          .build()
  );

  @Test
  void shouldCalculateTransactionCostSuccessfully() {
    TicketTransactionRequest ticketTransactionRequest = TicketTransactionRequest.builder()
        .transactionId(1)
        .customers(List.of(Customer.builder().build()))
        .build();

    when(ticketGroupService.getAgeGroup(any(Integer.class))).thenReturn(AgeGroup.Senior);

    when(ticketGroupService.buildTicketGroups(any(Map.class))).thenReturn(ticketGroups);

    assertThat(ticketTransactionService.calculateCost(ticketTransactionRequest)).isEqualTo(
        TicketTransactionResponse.builder().transactionId(1).tickets(ticketGroups)
            .totalCost(BigDecimal.valueOf(71).setScale(2, RoundingMode.HALF_EVEN))
            .build()
    );
  }
}
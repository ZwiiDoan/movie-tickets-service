package per.duyd.interview.mts.controller;

import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import per.duyd.interview.mts.dto.TicketTransactionRequest;
import per.duyd.interview.mts.dto.TicketTransactionResponse;
import per.duyd.interview.mts.service.TicketTransactionService;

@RestController
@RequiredArgsConstructor
public class TicketTransactionController {

  private final TicketTransactionService ticketTransactionService;

  @PostMapping("/v1/ticket-transaction")
  @Observed(name = "Request",
      contextualName = "Calculate_Ticket_Transaction_Cost",
      lowCardinalityKeyValues = {"endpoint", "/v1/ticket-transaction"})
  @Timed(value = "calculateTicketTransactionCost.timer", description = "Time taken to get process"
      + " TicketTransactionRequest")
  public TicketTransactionResponse calculateTicketTransactionCost(
      @Valid @RequestBody TicketTransactionRequest ticketTransactionRequest) {
    return ticketTransactionService.calculateCost(ticketTransactionRequest);
  }
}

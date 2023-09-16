package per.duyd.interview.mts.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import per.duyd.interview.mts.validation.UniqueCustomerNames;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketTransactionRequest {
  @Positive
  private long transactionId;

  @NotEmpty
  @UniqueCustomerNames
  private List<@Valid Customer> customers;
}

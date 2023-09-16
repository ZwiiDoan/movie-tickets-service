package per.duyd.interview.mts.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import per.duyd.interview.mts.BaseIntegrationTest;
import per.duyd.interview.mts.dto.TicketTransactionErrorResponse;
import per.duyd.interview.mts.dto.TicketTransactionResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketTransactionControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired
  protected ObjectMapper objectMapper;

  public static final String TICKET_TRANSACTION_PATH = "/v1/ticket-transaction";

  @Test
  void shouldCalculateTicketTransactionCostSuccessfully() throws Exception {
    MvcResult mvcResult =
        postTicketTransaction("/json/request/ticket_transaction_request_valid.json", 200);

    assertThat(objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
        TicketTransactionResponse.class))
        .isEqualTo(objectMapper.readValue(readFileToJsonString("/json/response"
            + "/ticket_transaction_response_success.json"), TicketTransactionResponse.class));
  }

  @ParameterizedTest
  @MethodSource("shouldReturnExpectedErrorResponsesParams")
  void shouldReturnExpectedErrorResponsesForInvalidRequests(
      String requestFile, String responseFile, int expectedResponseStatus) throws Exception {
    MvcResult mvcResult =
        postTicketTransaction(requestFile, expectedResponseStatus);

    assertThat(objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
        TicketTransactionErrorResponse.class))
        .isEqualTo(objectMapper.readValue(readFileToJsonString(responseFile),
            TicketTransactionErrorResponse.class));
  }

  private MvcResult postTicketTransaction(String requestFile, int expectedResponseStatus)
      throws Exception {
    return mockMvc.perform(post(TICKET_TRANSACTION_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(readFileToJsonString(requestFile)))
        .andExpect(status().is(expectedResponseStatus))
        .andReturn();
  }

  public Stream<Arguments> shouldReturnExpectedErrorResponsesParams() {
    return Stream.of(
        Arguments.of("/json/request/ticket_transaction_request_invalid_age.json",
            "/json/response/ticket_transaction_response_invalid_age.json", 400),
        Arguments.of("/json/request/ticket_transaction_request_invalid_customer_name.json",
            "/json/response/ticket_transaction_response_invalid_customer_name.json", 400),
        Arguments.of("/json/request/ticket_transaction_request_invalid_customers.json",
            "/json/response/ticket_transaction_response_invalid_customers.json", 400),
        Arguments.of("/json/request/ticket_transaction_request_invalid_transactionId.json",
            "/json/response/ticket_transaction_response_invalid_transactionId.json", 400),
        Arguments.of("/json/request/ticket_transaction_request_not_unique_customer_names.json",
            "/json/response/ticket_transaction_response_not_unique_customer_names.json", 400),
        Arguments.of("/json/request/ticket_transaction_request_malformed.json",
            "/json/response/ticket_transaction_response_malformed.json", 400)
    );
  }
}
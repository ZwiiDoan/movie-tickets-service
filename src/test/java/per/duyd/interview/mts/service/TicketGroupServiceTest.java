package per.duyd.interview.mts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;
import per.duyd.interview.mts.config.ApplicationConfig;
import per.duyd.interview.mts.dto.TicketConfig;
import per.duyd.interview.mts.dto.TicketGroup;
import per.duyd.interview.mts.enums.AgeGroup;
import per.duyd.interview.mts.enums.ErrorCode;
import per.duyd.interview.mts.exception.TicketConfigException;

class TicketGroupServiceTest {

  private final ApplicationConfig applicationConfig = new ApplicationConfig(
      Map.of(
          AgeGroup.Adult,
          TicketConfig.builder().price(BigDecimal.valueOf(25)).minAge(18).maxAge(64).build(),
          AgeGroup.Senior,
          TicketConfig.builder().price(BigDecimal.valueOf(20)).minAge(65).maxAge(Integer.MAX_VALUE)
              .build(),
          AgeGroup.Teen,
          TicketConfig.builder().price(BigDecimal.valueOf(12)).minAge(11).maxAge(17).build(),
          AgeGroup.Children,
          TicketConfig.builder().price(BigDecimal.valueOf(10)).minAge(0).maxAge(10).build()
      ), null);

  private TicketGroupService ticketGroupService;

  @BeforeEach
  void beforeEachTest() {
    TicketDiscountService ticketDiscountService = mock(TicketDiscountService.class);
    when(ticketDiscountService.getDiscountedCost(any(AgeGroup.class), any(Long.class),
        any(BigDecimal.class))).thenAnswer(invocation -> BigDecimal.valueOf(
        (Long) invocation.getArguments()[1]).multiply((BigDecimal) invocation.getArguments()[2]));

    ticketGroupService = new TicketGroupService(applicationConfig, ticketDiscountService);
  }

  @ParameterizedTest
  @MethodSource("shouldBuildTicketGroupsSuccessfullyParams")
  void shouldBuildTicketGroupsSuccessfully(Map<AgeGroup, Long> ticketsByAgeGroup,
                                           List<TicketGroup> expectedTicketGroups) {
    assertThat(ticketGroupService.buildTicketGroups(ticketsByAgeGroup)).isEqualTo(
        expectedTicketGroups);
  }

  @ParameterizedTest
  @ValueSource(ints = {17, 65})
  void shouldThrowTicketConfigExceptionWhenCannotFindAnAgeGroup(int age) {
    //Given
    Map<AgeGroup, TicketConfig> incompleteConfig = Map.of(
        AgeGroup.Adult,
        TicketConfig.builder().price(BigDecimal.valueOf(25)).minAge(18).maxAge(64).build());

    ReflectionTestUtils.setField(ticketGroupService, "tickets", incompleteConfig);

    //When
    TicketConfigException exception = assertThrows(TicketConfigException.class,
        () -> ticketGroupService.getAgeGroup(age));

    //Then
    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AGE_GROUP_NOT_FOUND);
    assertThat(exception.getMessage()).isEqualTo("Cannot find age group for age: " + age);
  }

  @Test
  void shouldThrowTicketConfigExceptionWhenCannotFindATicketPrice() {
    //Given
    Map<AgeGroup, TicketConfig> incompleteConfig = Map.of(
        AgeGroup.Adult,
        TicketConfig.builder().price(BigDecimal.valueOf(25)).minAge(18).maxAge(64).build());

    ReflectionTestUtils.setField(ticketGroupService, "tickets", incompleteConfig);

    //When
    TicketConfigException exception = assertThrows(TicketConfigException.class,
        () -> ticketGroupService.buildTicketGroups(Map.of(AgeGroup.Children, 2L)));

    //Then
    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TICKET_PRICE_NOT_FOUND);
    assertThat(exception.getMessage()).isEqualTo("Cannot find ticket price for ageGroup: Children");
  }

  public static Stream<Arguments> shouldBuildTicketGroupsSuccessfullyParams() {
    return Stream.of(
        Arguments.of(
            Map.of(AgeGroup.Children, 2L, AgeGroup.Senior, 1L),
            List.of(
                TicketGroup.builder().ageGroup(AgeGroup.Children).quantity(2)
                    .totalCost(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_EVEN)).build(),
                TicketGroup.builder().ageGroup(AgeGroup.Senior).quantity(1)
                    .totalCost(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_EVEN)).build())
        ),
        Arguments.of(
            Map.of(AgeGroup.Children, 3L, AgeGroup.Teen, 1L, AgeGroup.Adult, 1L),
            List.of(
                TicketGroup.builder().ageGroup(AgeGroup.Adult).quantity(1)
                    .totalCost(BigDecimal.valueOf(25).setScale(2, RoundingMode.HALF_EVEN)).build(),
                TicketGroup.builder().ageGroup(AgeGroup.Children).quantity(3)
                    .totalCost(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_EVEN)).build(),
                TicketGroup.builder().ageGroup(AgeGroup.Teen).quantity(1)
                    .totalCost(BigDecimal.valueOf(12).setScale(2, RoundingMode.HALF_EVEN)).build())
        )
    );
  }

  @ParameterizedTest
  @CsvSource({
      "0, Children", "10, Children", "6, Children",
      "11, Teen", "17, Teen", "14, Teen",
      "18, Adult", "64, Adult", "32, Adult",
      "65, Senior", "100, Senior"
  })
  void shouldGetExpectedAgeGroup(int age, AgeGroup expectedAgeGroup) {
    assertThat(ticketGroupService.getAgeGroup(age)).isEqualTo(expectedAgeGroup);
  }
}
package per.duyd.interview.mts.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import per.duyd.interview.mts.dto.TicketConfig;
import per.duyd.interview.mts.enums.AgeGroup;
import per.duyd.interview.mts.exception.TicketConfigException;

class ApplicationConfigTest {

  private ApplicationConfig applicationConfig;

  @BeforeEach
  void beforeEachTest() {
    applicationConfig = new ApplicationConfig();
  }

  @ParameterizedTest
  @MethodSource(value = "shouldFinaliseTicketsConfigSuccessfullyParams")
  void shouldFinaliseTicketsConfigSuccessfully(Map<AgeGroup, TicketConfig> inputTicketConfigs,
                                               Map<AgeGroup, TicketConfig> expectedTickets) {
    //Given
    applicationConfig.setTickets(inputTicketConfigs);

    //When
    applicationConfig.finaliseConfigs();

    //Then
    assertThat(applicationConfig.getTickets()).isEqualTo(expectedTickets);
  }

  @ParameterizedTest
  @MethodSource("shouldThrowTicketConfigExceptionWhenTicketPriceNotFoundForAgeGroupParams")
  void shouldThrowTicketConfigExceptionWhenTicketPriceNotFoundForAgeGroup(
      Map<AgeGroup, TicketConfig> inputTicketConfigs, AgeGroup errorAgeGroup) {
    applicationConfig.setTickets(inputTicketConfigs);

    TicketConfigException exception = assertThrows(TicketConfigException.class,
        () -> applicationConfig.finaliseConfigs());

    assertThat(exception.getMessage()).isEqualTo(
        "Cannot calculate ticket price of age group: " + errorAgeGroup);
  }

  public static Stream<Arguments> shouldFinaliseTicketsConfigSuccessfullyParams() {
    return Stream.of(
        Arguments.of(
            Map.of(
                AgeGroup.Adult,
                TicketConfig.builder().price(BigDecimal.valueOf(20)).minAge(18).maxAge(59).build(),
                AgeGroup.Senior,
                TicketConfig.builder().baseGroup(AgeGroup.Adult).adjustPercent(-20d).minAge(60)
                    .build(),
                AgeGroup.Teen,
                TicketConfig.builder().baseGroup(AgeGroup.Adult).adjustPercent(-10d).minAge(11)
                    .maxAge(17).build(),
                AgeGroup.Children,
                TicketConfig.builder().baseGroup(AgeGroup.Adult).adjustPercent(-40d).maxAge(10)
                    .build()
            ),
            Map.of(
                AgeGroup.Adult,
                TicketConfig.builder().price(BigDecimal.valueOf(20)).minAge(18).maxAge(59).build(),
                AgeGroup.Senior,
                TicketConfig.builder().baseGroup(AgeGroup.Adult).adjustPercent(-20d).minAge(60)
                    .maxAge(Integer.MAX_VALUE).price(BigDecimal.valueOf(16.0)).build(),
                AgeGroup.Teen,
                TicketConfig.builder().baseGroup(AgeGroup.Adult).adjustPercent(-10d).minAge(11)
                    .maxAge(17).price(BigDecimal.valueOf(18.0)).build(),
                AgeGroup.Children,
                TicketConfig.builder().baseGroup(AgeGroup.Adult).adjustPercent(-40d).maxAge(10)
                    .minAge(0).price(BigDecimal.valueOf(12.0)).build()
            )
        ),
        Arguments.of(
            Map.of(
                AgeGroup.Adult,
                TicketConfig.builder().price(BigDecimal.valueOf(25)).minAge(18).maxAge(64).build(),
                AgeGroup.Senior,
                TicketConfig.builder().baseGroup(AgeGroup.Adult).adjustPercent(-30d).minAge(65)
                    .build(),
                AgeGroup.Teen,
                TicketConfig.builder().price(BigDecimal.valueOf(12)).minAge(11).maxAge(17).build(),
                AgeGroup.Children,
                TicketConfig.builder().price(BigDecimal.valueOf(10)).maxAge(10).build()
            ),
            Map.of(
                AgeGroup.Adult,
                TicketConfig.builder().price(BigDecimal.valueOf(25)).minAge(18).maxAge(64).build(),
                AgeGroup.Senior,
                TicketConfig.builder().baseGroup(AgeGroup.Adult).adjustPercent(-30d).minAge(65)
                    .maxAge(Integer.MAX_VALUE).price(BigDecimal.valueOf(17.5)).build(),
                AgeGroup.Teen,
                TicketConfig.builder().price(BigDecimal.valueOf(12)).minAge(11).maxAge(17).build(),
                AgeGroup.Children,
                TicketConfig.builder().price(BigDecimal.valueOf(10)).minAge(0).maxAge(10).build()
            )
        )
    );
  }

  public static Stream<Arguments> shouldThrowTicketConfigExceptionWhenTicketPriceNotFoundForAgeGroupParams() {
    return Stream.of(
        Arguments.of(
            Map.of(
                AgeGroup.Senior,
                TicketConfig.builder().baseGroup(AgeGroup.Adult).adjustPercent(-30d).minAge(65)
                    .maxAge(Integer.MAX_VALUE).build()
            ),
            AgeGroup.Senior
        ),
        Arguments.of(
            Map.of(
                AgeGroup.Adult,
                TicketConfig.builder().price(BigDecimal.valueOf(25)).minAge(18).maxAge(64).build(),
                AgeGroup.Senior,
                TicketConfig.builder().baseGroup(AgeGroup.Adult).minAge(65)
                    .maxAge(Integer.MAX_VALUE).build()
            ),
            AgeGroup.Senior
        ),
        Arguments.of(
            Map.of(
                AgeGroup.Adult,
                TicketConfig.builder().price(BigDecimal.valueOf(25)).minAge(18).maxAge(64).build(),
                AgeGroup.Senior,
                TicketConfig.builder().adjustPercent(-30d).minAge(65)
                    .maxAge(Integer.MAX_VALUE).build()
            ),
            AgeGroup.Senior
        )
    );
  }
}
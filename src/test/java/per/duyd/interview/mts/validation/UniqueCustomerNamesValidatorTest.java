package per.duyd.interview.mts.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import per.duyd.interview.mts.dto.Customer;

class UniqueCustomerNamesValidatorTest {

  private final UniqueCustomerNamesValidator uniqueCustomerNamesValidator =
      new UniqueCustomerNamesValidator();

  public static Stream<Arguments> shouldValidateCustomersHaveUniqueNamesParams() {
    return Stream.of(
        Arguments.of(
            List.of(
                Customer.builder().name("name1").build(),
                Customer.builder().name("name2").build(),
                Customer.builder().name("name3").build()
            ),
            true
        ),
        Arguments.of(
            List.of(
                Customer.builder().name("name1").build(),
                Customer.builder().name("name2").build(),
                Customer.builder().name("name1").build()
            ),
            false
        )
    );
  }

  @ParameterizedTest
  @MethodSource("shouldValidateCustomersHaveUniqueNamesParams")
  void shouldValidateCustomersHaveUniqueNames(List<Customer> customers, boolean expected) {
    assertThat(uniqueCustomerNamesValidator.isValid(customers,
        mock(ConstraintValidatorContext.class))).isEqualTo(expected);
  }
}
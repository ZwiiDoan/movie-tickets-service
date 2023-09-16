package per.duyd.interview.mts.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import per.duyd.interview.mts.dto.Customer;

public class UniqueCustomerNamesValidator
    implements ConstraintValidator<UniqueCustomerNames, List<Customer>> {
  @Override
  public boolean isValid(List<Customer> customers, ConstraintValidatorContext context) {
    return Optional.ofNullable(customers)
        .map(it -> it.stream().collect(
            Collectors.groupingBy(
                Customer::getName,
                Collectors.counting()
            )).entrySet().stream().noneMatch(entry -> entry.getValue() > 1)
        ).orElse(false);
  }
}

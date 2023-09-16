package per.duyd.interview.mts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Customer {
  @NotBlank
  private String name;

  @Positive
  private int age;
}

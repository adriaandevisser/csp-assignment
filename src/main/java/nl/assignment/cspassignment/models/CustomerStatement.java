package nl.assignment.cspassignment.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class CustomerStatement {

    @NotNull
    private Long transactionReference;
    @NotNull
    private String accountNumber;
    @NotNull
    private BigDecimal startBalance;
    @NotNull
    private BigDecimal endBalance;
    @NotNull
    private BigDecimal mutation;
    @NotNull
    private String description;

}

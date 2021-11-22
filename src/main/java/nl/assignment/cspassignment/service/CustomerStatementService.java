package nl.assignment.cspassignment.service;

import lombok.AllArgsConstructor;
import nl.assignment.cspassignment.models.CustomerStatement;
import nl.assignment.cspassignment.models.CustomerStatementResult;
import nl.assignment.cspassignment.repo.CustomerStatementRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerStatementService {

    private final CustomerStatementRepository statements;

    public CustomerStatementResult process(CustomerStatement cs) {
        CustomerStatementResult result = new CustomerStatementResult();
        register(cs, result);

        validateBalance(cs, result);
        return result;
    }
    
    private void register(CustomerStatement cs, CustomerStatementResult result) {
        if (!statements.register(cs.getTransactionReference())) {
            result.addError("DUPLICATE_REFERENCE", cs.getTransactionReference(), cs.getAccountNumber());
        }
    }

    private void validateBalance(CustomerStatement cs, CustomerStatementResult result) {
        BigDecimal expected = Optional.ofNullable(cs.getStartBalance())
                .map(start -> cs.getMutation() != null ?
                        start.add(cs.getMutation())
                        : cs.getStartBalance())
                .orElse(null);

        BigDecimal actual = cs.getEndBalance();
        if (expected == null || actual == null || actual.compareTo(expected) != 0) {
            result.addError("INCORRECT_END_BALANCE", cs.getTransactionReference(), cs.getAccountNumber());
        }
    }

}

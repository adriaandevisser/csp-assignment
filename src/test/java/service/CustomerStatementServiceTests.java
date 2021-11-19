package service;

import nl.assignment.cspassignment.models.CustomerStatement;
import nl.assignment.cspassignment.models.CustomerStatementResult;
import nl.assignment.cspassignment.repo.CustomerStatementRepository;
import nl.assignment.cspassignment.service.CustomerStatementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerStatementServiceTests {

    @Mock
    private CustomerStatementRepository repo;

    private CustomerStatement statement;

    @InjectMocks
    private CustomerStatementService service;

    private static final BigDecimal START_BALANCE = BigDecimal.valueOf(30);
    private static final BigDecimal END_BALANCE = BigDecimal.valueOf(42);
    private static final BigDecimal MUTATION = BigDecimal.valueOf(12);


    @BeforeEach
    public void setUp() {
        this.statement = CustomerStatement.builder()
                .transactionReference(1234L)
                .accountNumber("NL13ABNA4948749915")
                .startBalance(START_BALANCE)
                .endBalance(END_BALANCE)
                .mutation(MUTATION)
                .description("description")
                .build();
    }

    @Test
    void testProcess_validUniqueStatement_processedSuccessfully() {
        when(repo.register(statement.getTransactionReference())).thenReturn(true);

        CustomerStatementResult result = service.process(statement);

        assertEquals("SUCCESSFUL", result.getResultMessage());
        assertEquals(0, result.getErrorRecords().size());
    }

    @Test
    void testProcess_invalidUniqueStatement_endBalanceAndMutationMismatch_processedAsIncorrectEndBalance() {
        statement.setEndBalance(BigDecimal.valueOf(10));
        when(repo.register(statement.getTransactionReference())).thenReturn(true);

        CustomerStatementResult result = service.process(statement);

        assertEquals("INCORRECT_END_BALANCE", result.getResultMessage());
        assertEquals(1, result.getErrorRecords().size());
        assertEquals(statement.getAccountNumber(), result.getErrorRecords().get(0).getAccountNumber());
        assertEquals(statement.getTransactionReference(), result.getErrorRecords().get(0).getTransactionReference());
    }

    @ParameterizedTest
    @MethodSource("provideFieldSets")
    void testProcess_invalidUniqueStatement_nullValues_processedAsIncorrectEndBalance(BigDecimal start, BigDecimal end, BigDecimal mutation) {
        statement.setStartBalance(start);
        statement.setEndBalance(end);
        statement.setMutation(mutation);
        when(repo.register(statement.getTransactionReference())).thenReturn(true);

        CustomerStatementResult result = service.process(statement);

        assertEquals("INCORRECT_END_BALANCE", result.getResultMessage());
        assertEquals(1, result.getErrorRecords().size());
        assertEquals(statement.getAccountNumber(), result.getErrorRecords().get(0).getAccountNumber());
        assertEquals(statement.getTransactionReference(), result.getErrorRecords().get(0).getTransactionReference());
    }

    private static Stream<Arguments> provideFieldSets() {
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of(START_BALANCE, END_BALANCE, null),
                Arguments.of(START_BALANCE, null, MUTATION),
                Arguments.of(null, END_BALANCE, MUTATION),
                Arguments.of(null, null, MUTATION),
                Arguments.of(null, END_BALANCE, null),
                Arguments.of(START_BALANCE, null, null)
        );
    }

    @Test
    void testProcess_validDuplicateStatement_processedAsDuplicateReference() {
        CustomerStatementResult result = service.process(statement);

        assertEquals("DUPLICATE_REFERENCE", result.getResultMessage());
        assertEquals(1, result.getErrorRecords().size());
        assertEquals(statement.getAccountNumber(), result.getErrorRecords().get(0).getAccountNumber());
        assertEquals(statement.getTransactionReference(), result.getErrorRecords().get(0).getTransactionReference());
    }

    @Test
    void testProcess_invalidDuplicateStatement_processedAsDuplicateReferenceAndIncorrectEndBalance() {
        statement.setEndBalance(BigDecimal.valueOf(10));
        CustomerStatementResult result = service.process(statement);

        assertEquals("DUPLICATE_REFERENCE_INCORRECT_END_BALANCE", result.getResultMessage());
        assertEquals(2, result.getErrorRecords().size());
        assertEquals(statement.getAccountNumber(), result.getErrorRecords().get(0).getAccountNumber());
        assertEquals(statement.getTransactionReference(), result.getErrorRecords().get(0).getTransactionReference());
        assertEquals(statement.getAccountNumber(), result.getErrorRecords().get(1).getAccountNumber());
        assertEquals(statement.getTransactionReference(), result.getErrorRecords().get(1).getTransactionReference());
    }

}

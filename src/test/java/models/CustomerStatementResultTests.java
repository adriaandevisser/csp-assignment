package models;


import nl.assignment.cspassignment.models.CustomerStatementResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerStatementResultTests {

    private CustomerStatementResult result;
    private final static String ACCOUNT_NUMBER = "NL13ABNA4948749915";

    @BeforeEach
    public void setUp() {
        result = new CustomerStatementResult();
    }

    @Test
    void testAddError_hasAdditionalErrorMessage() {
        assertTrue(result.getErrorRecords().isEmpty());

        result.addError("", 1234, ACCOUNT_NUMBER);

        assertEquals("", result.getResultMessage());
        assertEquals(1, result.getErrorRecords().size());
        assertEquals(1234, result.getErrorRecords().get(0).getTransactionReference());
        assertEquals(ACCOUNT_NUMBER, result.getErrorRecords().get(0).getAccountNumber());
    }

    @Test
    void testAddError_firstNewMessage_overridesSuccessMessage() {
        result.addError("", 1234, ACCOUNT_NUMBER);
        assertEquals("", result.getResultMessage());
    }

    @Test
    void testAddError_secondMessage_appendedToFirstMessage() {
        result.addError("foo", 1234, ACCOUNT_NUMBER);
        result.addError("bar", 1234, ACCOUNT_NUMBER);
        assertEquals("foo_bar", result.getResultMessage());
    }

}

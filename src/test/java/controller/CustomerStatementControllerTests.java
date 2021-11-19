package controller;

import nl.assignment.cspassignment.controller.CustomerStatementController;
import nl.assignment.cspassignment.models.CustomerStatement;
import nl.assignment.cspassignment.models.CustomerStatementResult;
import nl.assignment.cspassignment.service.CustomerStatementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerStatementControllerTests {

    @Mock
    private CustomerStatementService service;

    @Mock
    private CustomerStatementResult statementResult;

    @Mock
    private CustomerStatement statement;

    @InjectMocks
    private CustomerStatementController controller;

    @Test
    void testProcessStatement() {
        when(service.process(eq(statement))).thenReturn(statementResult);
        CustomerStatementResult result = controller.processStatement(statement);
        assertEquals(result, statementResult);
    }

}

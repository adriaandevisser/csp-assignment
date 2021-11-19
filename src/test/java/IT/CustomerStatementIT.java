package IT;

import lombok.SneakyThrows;
import nl.assignment.cspassignment.CspAssignmentApplication;
import nl.assignment.cspassignment.models.CustomerStatementResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {CspAssignmentApplication.class}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomerStatementIT {
    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;
    private final String path = "/";

    private final static String ACCOUNT_NUMBER = "NL13ABNA4948749915";
    private final static long REFERENCE = 1234;

    @BeforeEach
    public void setUp() {
        this.restTemplate = new RestTemplate();
    }

    @Test
    @SneakyThrows
    void testInvalidJson_fieldsWrongDataType_BadRequest() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        String json = getJsonFile("statement-invalid-parse.json");

        HttpEntity<String> request = new HttpEntity<>(json, requestHeaders);
        assertThrows(HttpClientErrorException.BadRequest.class,
                () -> restTemplate.postForEntity("http://localhost:" + port + path, request, CustomerStatementResult.class));
    }

    @Test
    @SneakyThrows
    void testInvalidJson_fieldsMissing_BadRequest() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        String json = getJsonFile("statement-invalid-partial.json");

        HttpEntity<String> request = new HttpEntity<>(json, requestHeaders);
        assertThrows(HttpClientErrorException.BadRequest.class,
                () -> restTemplate.postForEntity("http://localhost:" + port + path, request, CustomerStatementResult.class));
    }

    @Test
    @SneakyThrows
    void testValidJson_ReturnsSuccessful(){
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        String json = getJsonFile("statement-valid.json");

        HttpEntity<String> request = new HttpEntity<>(json, requestHeaders);
        ResponseEntity<CustomerStatementResult> response =
                restTemplate.postForEntity("http://localhost:" + port + path, request, CustomerStatementResult.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        CustomerStatementResult result = response.getBody();

        assertEquals("SUCCESSFUL", result.getResultMessage());
        assertEquals(0, result.getErrorRecords().size());
    }

    @Test
    @SneakyThrows
    void test_invalidUniqueStatement_endBalanceAndMutationMismatch_processedAsIncorrectEndBalance(){
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        String json = getJsonFile("statement-invalid-balance.json");

        HttpEntity<String> request = new HttpEntity<>(json, requestHeaders);
        ResponseEntity<CustomerStatementResult> response =
                restTemplate.postForEntity("http://localhost:" + port + path, request, CustomerStatementResult.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        CustomerStatementResult result = response.getBody();

        assertEquals("INCORRECT_END_BALANCE", result.getResultMessage());
        assertEquals(1, result.getErrorRecords().size());
        assertEquals(ACCOUNT_NUMBER, result.getErrorRecords().get(0).getAccountNumber());
        assertEquals(REFERENCE, result.getErrorRecords().get(0).getTransactionReference());
    }

    @Test
    @SneakyThrows
    void testProcess_validDuplicateStatement_processedAsDuplicateReference(){
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        String json = getJsonFile("statement-valid.json");

        HttpEntity<String> request = new HttpEntity<>(json, requestHeaders);
        ResponseEntity<CustomerStatementResult> response =
                restTemplate.postForEntity("http://localhost:" + port + path, request, CustomerStatementResult.class);
        response =
                restTemplate.postForEntity("http://localhost:" + port + path, request, CustomerStatementResult.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        CustomerStatementResult result = response.getBody();

        assertEquals("DUPLICATE_REFERENCE", result.getResultMessage());
        assertEquals(1, result.getErrorRecords().size());
        assertEquals(ACCOUNT_NUMBER, result.getErrorRecords().get(0).getAccountNumber());
        assertEquals(REFERENCE, result.getErrorRecords().get(0).getTransactionReference());
    }

    @Test
    @SneakyThrows
    void testProcess_invalidDuplicateStatement_processedAsDuplicateReferenceAndIncorrectEndBalance(){
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        String json = getJsonFile("statement-invalid-balance.json");

        HttpEntity<String> request = new HttpEntity<>(json, requestHeaders);
        ResponseEntity<CustomerStatementResult> response =
                restTemplate.postForEntity("http://localhost:" + port + path, request, CustomerStatementResult.class);
        response =
                restTemplate.postForEntity("http://localhost:" + port + path, request, CustomerStatementResult.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        CustomerStatementResult result = response.getBody();

        assertEquals("DUPLICATE_REFERENCE_INCORRECT_END_BALANCE", result.getResultMessage());
        assertEquals(2, result.getErrorRecords().size());
        assertEquals(ACCOUNT_NUMBER, result.getErrorRecords().get(0).getAccountNumber());
        assertEquals(REFERENCE, result.getErrorRecords().get(0).getTransactionReference());
        assertEquals(ACCOUNT_NUMBER, result.getErrorRecords().get(1).getAccountNumber());
        assertEquals(REFERENCE, result.getErrorRecords().get(1).getTransactionReference());
    }

    private String getJsonFile(String fileName) throws Exception {
        return Files.lines(Path.of(Objects.requireNonNull(this.getClass().getClassLoader().getResource(fileName)).toURI())).collect(Collectors.joining());
    }
}

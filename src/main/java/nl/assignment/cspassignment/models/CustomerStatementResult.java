package nl.assignment.cspassignment.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerStatementResult {

    @JsonProperty("result")
    private String resultMessage = "SUCCESSFUL";
    private final List<ErrorRecord> errorRecords = new ArrayList<>();

    public void addError(String message, long transactionReference, String accountNumber) {
        this.setResultMessage(message);
        this.errorRecords.add(new ErrorRecord(transactionReference, accountNumber));
    }

    private void setResultMessage(String message) {
        if ("SUCCESSFUL".equals(resultMessage)) {
            this.resultMessage = message;
        } else {
            this.resultMessage = this.resultMessage + "_" + message;
        }
    }

}

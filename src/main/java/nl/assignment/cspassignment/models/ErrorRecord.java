package nl.assignment.cspassignment.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorRecord {

    public ErrorRecord() {}

    @JsonProperty("reference")
    private long transactionReference;
    private String accountNumber;

}

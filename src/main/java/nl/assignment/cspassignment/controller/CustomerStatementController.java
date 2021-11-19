package nl.assignment.cspassignment.controller;

import lombok.AllArgsConstructor;
import nl.assignment.cspassignment.models.CustomerStatement;
import nl.assignment.cspassignment.models.CustomerStatementResult;
import nl.assignment.cspassignment.service.CustomerStatementService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class CustomerStatementController {

    private final CustomerStatementService service;

    @PostMapping
    public CustomerStatementResult processStatement(@RequestBody @Valid CustomerStatement cs) {
        return service.process(cs);
    }

}

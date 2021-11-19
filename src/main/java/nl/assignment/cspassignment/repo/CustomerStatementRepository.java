package nl.assignment.cspassignment.repo;

import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CustomerStatementRepository {

    // Placeholder repo - does not persist after shutdown
    private final Set<Long> transactionset = ConcurrentHashMap.newKeySet();

    public boolean register(long transactionReference) {
        return transactionset.add(transactionReference);
    }
}

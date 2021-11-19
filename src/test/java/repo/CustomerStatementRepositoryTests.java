package repo;

import nl.assignment.cspassignment.repo.CustomerStatementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerStatementRepositoryTests {

    private CustomerStatementRepository repo;

    @BeforeEach
    public void setUp() {
        this.repo = new CustomerStatementRepository();
    }

    @Test
    void testRegister_new_returnsTrue(){
        assertTrue(repo.register(1234));
    }

    @Test
    void testRegister_registerDuplicate_returnsFalse(){
        assertTrue(repo.register(1234));
        assertFalse(repo.register(1234));
    }

}

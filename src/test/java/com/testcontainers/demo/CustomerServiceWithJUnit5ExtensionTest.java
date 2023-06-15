package com.testcontainers.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class CustomerServiceWithJUnit5ExtensionTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2-alpine");

    CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        customerService.deleteAllCustomers();
    }

    @Test
    void shouldCreateCustomer() {
        customerService.createCustomer(new Customer(1L, "George"));

        Optional<Customer> customer = customerService.getCustomer(1L);
        assertTrue(customer.isPresent());
        assertEquals(1L, customer.get().id());
        assertEquals("George", customer.get().name());
    }

    @Test
    void shouldGetCustomers() {
        customerService.createCustomer(new Customer(1L, "George"));
        customerService.createCustomer(new Customer(2L, "John"));

        List<Customer> customers = customerService.getAllCustomers();
        assertEquals(2, customers.size());
    }
}

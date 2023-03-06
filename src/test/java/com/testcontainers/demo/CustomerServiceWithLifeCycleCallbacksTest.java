package com.testcontainers.demo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerServiceWithLifeCycleCallbacksTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2-alpine");

    CustomerService customerService;

    @BeforeAll
    static void startContainers() {
        postgres.start();
    }

    @AfterAll
    static void stopContainers() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword());
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

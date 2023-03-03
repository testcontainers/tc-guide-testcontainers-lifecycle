package com.testcontainers.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerService {

    private final String url;
    private final String username;
    private final String password;

    public CustomerService(String url,
                                String username,
                                String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        createCustomersTableIfNotExists();
    }

    public void createCustomer(Customer customer) {
        try (Connection conn = this.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("insert into customers(id,name) values(?,?)");
            pstmt.setLong(1, customer.getId());
            pstmt.setString(2, customer.getName());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = this.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("select id,name from customers");
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                customers.add(new Customer(id,name));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }

    public Optional<Customer> getCustomer(Long customerId) {
        try (Connection conn = this.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("select id,name from customers where id = ?1");
            pstmt.setLong(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                return Optional.of(new Customer(id,name));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void deleteAllCustomers() {
        try (Connection conn = this.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("delete from customers");
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createCustomersTableIfNotExists() {
        try (Connection conn = this.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                """
                    create table if not exists customers (
                        id bigint not null,
                        name varchar not null,
                        primary key (id)
                    )
                    """);
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

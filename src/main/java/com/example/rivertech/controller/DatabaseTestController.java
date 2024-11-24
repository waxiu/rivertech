package com.example.rivertech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/db")
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/test")
    public String testConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return "Połączenie działa! " + connection.getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            return "Błąd połączenia: " + e.getMessage();
        }
    }
}


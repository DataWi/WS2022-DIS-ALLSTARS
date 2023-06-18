package com.dsys.pdfgenerator.service;

import com.dsys.pdfgenerator.model.Customer;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseService {
    private static Connection connect() throws SQLException {
        String connectionString="jdbc:postgresql://localhost:30001/customerdb";
        return DriverManager.getConnection(connectionString, "postgres", "postgres");
    }



    public static Customer getCustomer(String id) throws SQLException {
         Customer customer = new Customer(0, "Jane", "Does");

        try ( Connection conn = connect() ) {
            String query = "SELECT id, first_name, last_name FROM customer WHERE id = " + id + ";";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();

            if(!resultSet.next()) {
                throw new SQLException("Customer not found in DB");
            }

            while( resultSet.next()) {
                 customer = new Customer(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3)
                );
            }
            return customer;
        }
    }
}

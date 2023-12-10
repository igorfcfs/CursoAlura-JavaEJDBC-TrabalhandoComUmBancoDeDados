package br.com.alura.bytebank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/byte_bank_alura?user=root&password=root");

            System.out.println("Recuperei a conexão");

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

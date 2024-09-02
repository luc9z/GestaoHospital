package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    // Dados de conexão
    private final String url = "jdbc:postgresql://localhost:5432/GestaoHospital";
    private final String user = "postgres";
    private final String password = "123"; // Adicione a senha aqui

    public Connection conectar() {
        Connection conn = null;
        try {
            // Conectando ao banco de dados
            conn = DriverManager.getConnection(url, user, password); // Passe a senha como terceiro argumento
            System.out.println("Conectado ao PostgreSQL com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao PostgreSQL");
            e.printStackTrace();
        }

        return conn;
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.conectar();
    }
}

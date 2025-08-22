package com.deligo.DatabaseManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
        String user = "postgres.kcgdzfgoyjlzcxsvghyx";
        String pass = "72HCrGueeHwL1wcl";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("✅ Connected to PostgreSQL!");
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
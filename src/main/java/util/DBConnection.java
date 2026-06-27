package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Apne MySQL ka exact Port (usually 3306) aur database ka naam (gym_db) lagaein
    private static final String URL = "jdbc:mysql://localhost:3306/Gym_Fitness";
    private static final String USER = "root";
    private static final String PASSWORD = "3668"; // ⚠️ Yahan apna MySQL password lazmi likhein

    public static Connection getConnection() throws SQLException {
        try {
            // Driver class load karna mandatory hai
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
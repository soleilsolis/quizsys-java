package process;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Database {
    private static String url = "jdbc:mysql://localhost:3306/quiz";
    private static String username = "root";
    private static String password = "4E53p1X7XE";
    private static ResultSet resultSet = null;
    public static ResultSet getResultSet (String query) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultSet;
    }
}

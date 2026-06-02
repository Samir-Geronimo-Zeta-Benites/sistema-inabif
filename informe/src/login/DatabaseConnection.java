package login;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

  private static final String URL =
          "jdbc:sqlserver://localhost:1433;" +
                  "databaseName=informe;" +
                  "encrypt=true;" +
                  "trustServerCertificate=true;" +
                  "user=sa;" +
                  "password=123456;";

  public static Connection getConnection() throws Exception {
    return DriverManager.getConnection(URL);
  }
}
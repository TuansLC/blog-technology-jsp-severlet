package com.ptit.blogtechnology.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

  private static final String URL = "jdbc:mysql://localhost:3306/blog-technology?allowPublicKeyRetrieval=true&useSSL=false";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "root";

  static {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USERNAME, PASSWORD);
  }

}

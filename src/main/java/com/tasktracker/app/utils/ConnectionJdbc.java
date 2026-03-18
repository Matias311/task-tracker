package com.tasktracker.app.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/** Class that contain the logic to the JDBC connection. */
public class ConnectionJdbc {

  private static HikariConfig config = new HikariConfig();
  private static HikariDataSource ds;

  static {
    config.setJdbcUrl(
        "jdbc:postgresql://"
            + System.getenv("POSTGRES_HOST")
            + "localhost:5432/"
            + System.getenv("POSTGRES_DB"));
    config.setUsername(System.getenv("POSTGRES_USER"));
    config.setPassword(System.getenv("POSTGRES_PASSWORD"));
    config.setMaximumPoolSize(10);
    config.setMinimumIdle(2);
    config.setIdleTimeout(3000);
    config.setConnectionTimeout(3000);
    config.setLeakDetectionThreshold(1500);
    ds = new HikariDataSource(config);
  }

  private ConnectionJdbc() {}

  /**
   * Get a Connection for JDBC.
   *
   * @return Connection for postgresql
   * @throws SQLException if can not connect to Posgresql
   */
  public static Connection getConnection() throws SQLException {
    return ds.getConnection();
  }
}

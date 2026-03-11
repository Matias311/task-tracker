package com.tasktracker.app.utils;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

/** Class that contain the logic to the JDBC connection. */
public enum ConnectionJdbc {
  INSTANCE;
  private DataSource dataSource;

  ConnectionJdbc() {
    PGSimpleDataSource ds = new PGSimpleDataSource();
    ds.setServerNames(new String[] {System.getenv("POSTGRES_HOST")});
    ds.setDatabaseName(System.getenv("POSTGRES_DB"));
    ds.setUser(System.getenv("POSTGRES_USER"));
    ds.setPassword(System.getenv("POSTGRES_PASSWORD"));
    this.dataSource = ds;
  }

  /**
   * Get a Connection for JDBC.
   *
   * @return Connection for postgresql
   * @throws SQLException if can not connect to Posgresql
   */
  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}

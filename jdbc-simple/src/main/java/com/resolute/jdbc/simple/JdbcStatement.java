package com.resolute.jdbc.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

public class JdbcStatement {
  private static Logger log = LoggerFactory.getLogger(JdbcStatementFactory.class);

  private final DataSource dataSource;
  private String sql;
  private String errorMessage;
  private Optional<SqlConsumer<PreparedStatement>> preparedStatementHandler = Optional.empty();

  JdbcStatement(DataSource dataSource) {
    this.dataSource = requireNonNull(dataSource, "dataSource cannot be null");
  }

  /**
   * Use to set the SQL statement to be executed.
   * 
   * @param sql the SQL statement .
   * @return the JdbcStatement object
   */
  public JdbcStatement withSql(String sql) {
    this.sql = requireNonNull(sql, "sql cannot be null");
    return this;
  }

  /**
   * Use to set the error message that will be used if an SQLException is thrown when the statement
   * is executed.
   * 
   * @param errorMessage the error message .
   * @return the JdbcStatement object
   */
  public JdbcStatement withErrorMessage(String errorMessage) {
    this.errorMessage = requireNonNull(errorMessage, "errorMessage cannot be null");
    return this;
  }

  /**
   * Use to assign values to statement parameters before executing the statement.
   * 
   * @param preparedStatementHandler a consumer that accepts a PreparedStatement, and invokes
   *        methods on that statement in order to assign values to statement parameters denoted by
   *        question marks in the SQL string. .
   * @return the JdbcStatement object
   */
  public JdbcStatement prepareStatement(SqlConsumer<PreparedStatement> preparedStatementHandler) {
    this.preparedStatementHandler = Optional
        .of(requireNonNull(preparedStatementHandler, "preparedStatementHandler cannot be null"));
    return this;
  }

  /**
   * Use to execute a SQL query. Before calling this method, you must invoke the
   * {@link {@link #withSql(String) withSql} and {@link #withErrorMessage(String) withErrorMessage}
   * methods. You may optionally also invoke the {@link #prepareStatement(SqlConsumer)
   * prepareStatement} method as needed.
   * 
   * @param executeQueryHandler a function that accepts a Result, converts it into one of more value
   *        objects and returns them.
   * @return the value returned b the executeQueryHandler
   */
  public <T> T executeQuery(SqlFunction<Result, T> executeQueryHandler) {
    requireNonNull(executeQueryHandler, "executeQueryHandler cannot be null");
    requireNonNull(sql, "sql cannot be null");
    requireNonNull(errorMessage, "errorMessage cannot be null");
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        if (preparedStatementHandler.isPresent()) {
          preparedStatementHandler.get().accept(stmt);
        }
        try (ResultSet resultSet = stmt.executeQuery()) {
          Result processor = new Result(resultSet);
          return executeQueryHandler.apply(processor);
        }
      }
    } catch (Exception e) {
      if (e instanceof DataAccessException) {
        throw DataAccessException.class.cast(e);
      }
      throw new DataAccessException(errorMessage,
          e);
    }
  }

  /**
   * Use to execute a SQL query. Before calling this method, you must invoke the
   * {@link {@link #withSql(String) withSql} and {@link #withErrorMessage(String) withErrorMessage}
   * methods. You may optionally also invoke the {@link #prepareStatement(SqlConsumer)
   * prepareStatement} method as needed.
   * 
   * @param executeQueryHandler a consumer that accepts a Result.
   */
  public void executeQueryNoReturn(SqlConsumer<Result> executeQueryHandler) {
    requireNonNull(executeQueryHandler, "executeQueryHandler cannot be null");
    requireNonNull(sql, "sql cannot be null");
    requireNonNull(errorMessage, "errorMessage cannot be null");
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        if (preparedStatementHandler.isPresent()) {
          preparedStatementHandler.get().accept(stmt);
        }
        try (ResultSet resultSet = stmt.executeQuery()) {
          Result processor = new Result(resultSet);
          executeQueryHandler.accept(processor);
        }
      }
    } catch (Exception e) {
      if (e instanceof DataAccessException) {
        throw DataAccessException.class.cast(e);
      }
      throw new DataAccessException(errorMessage,
          e);
    }
  }

  /**
   * Use to execute a SQL query within the context of a larger transaction. Before calling this
   * method, you must invoke the {@link #withSql(String) withSql} method. You may optionally also
   * invoke the {@link #prepareStatement(SqlConsumer) prepareStatement} method as needed.
   * 
   * @param conn a database connection
   * @param executeQueryHandler a function that accepts a Result, converts it into one of more value
   *        objects and returns them.
   * @return the value returned b the executeQueryHandler
   */
  public <T> T executeQueryWithConnection(Connection conn,
      SqlFunction<Result, T> executeQueryHandler)
      throws SQLException {
    requireNonNull(executeQueryHandler, "executeQueryHandler cannot be null");
    requireNonNull(conn, "conn cannot be null");
    requireNonNull(sql, "sql cannot be null");

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      if (preparedStatementHandler.isPresent()) {
        preparedStatementHandler.get().accept(stmt);
      }
      try (ResultSet resultSet = stmt.executeQuery()) {
        Result processor = new Result(resultSet);
        return executeQueryHandler.apply(processor);
      }
    }
  }

  /**
   * Use to execute a SQL query within the context of a larger transaction. Before calling this
   * method, you must invoke the {@link #withSql(String) withSql} method. You may optionally also
   * invoke the {@link #prepareStatement(SqlConsumer) prepareStatement} method as needed.
   * 
   * @param conn a database connection
   * @param executeQueryHandler a consumer that accepts a Result.
   */
  public void executeQueryWithConnectionNoReturn(Connection conn,
      SqlConsumer<Result> executeQueryHandler)
      throws SQLException {
    requireNonNull(executeQueryHandler, "executeQueryHandler cannot be null");
    requireNonNull(conn, "conn cannot be null");
    requireNonNull(sql, "sql cannot be null");

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      if (preparedStatementHandler.isPresent()) {
        preparedStatementHandler.get().accept(stmt);
      }
      try (ResultSet resultSet = stmt.executeQuery()) {
        Result processor = new Result(resultSet);
        executeQueryHandler.accept(processor);
      }
    }
  }

  /**
   * Use to execute a SQL statement. Before calling this method, you must invoke the
   * {@link #withSql(String) withSql}, and {@link #withErrorMessage(String) withErrorMessage}
   * methods. You may optionally also invoke the {@link #prepareStatement(SqlConsumer)
   * prepareStatement} method as needed.
   * 
   * @return the current result as an update count or -1 if the current result is a ResultSet object
   *         or there are no more results
   */
  public int execute() {
    requireNonNull(sql, "sql cannot be null");
    requireNonNull(errorMessage, "errorMessage cannot be null");

    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        if (preparedStatementHandler.isPresent()) {
          preparedStatementHandler.get().accept(stmt);
        }
        stmt.execute();
        return stmt.getUpdateCount();
      }
    } catch (Exception e) {
      if (e instanceof DataAccessException) {
        throw DataAccessException.class.cast(e);
      }
      throw new DataAccessException(errorMessage,
          e);
    }
  }

  /**
   * Use to execute a Batch SQL statement. Before calling this method, you must invoke the
   * {@link #withSql(String) withSql}, {@link #withErrorMessage(String) withErrorMessage} methods.
   * You may should also invoke the {@link #prepareStatement(SqlConsumer) prepareStatement} method
   * to add batch parameters.
   * 
   * @return the current result as an update count or -1 if the current result is a ResultSet object
   *         or there are no more results
   */
  public int[] executeBatch() {
    requireNonNull(sql, "sql cannot be null");
    requireNonNull(errorMessage, "errorMessage cannot be null");

    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        if (preparedStatementHandler.isPresent()) {
          preparedStatementHandler.get().accept(stmt);
        }
        return stmt.executeBatch();
      }
    } catch (Exception e) {
      if (e instanceof DataAccessException) {
        throw DataAccessException.class.cast(e);
      }
      throw new DataAccessException(errorMessage,
          e);
    }
  }

  /**
   * Use to execute a SQL statement within the context of a larger transaction. Before calling this
   * method you must invoke the {@link #withSql(String) withSql} method. You may optionally also
   * invoke the {@link #prepareStatement(SqlConsumer) prepareStatement} method as needed.
   * 
   * @param conn a database connection
   * @return the current result as an update count or -1 if the current result is a ResultSet object
   *         or there are no more results
   */
  public int executeWithConnection(Connection conn) throws SQLException {
    requireNonNull(conn, "conn cannot be null");
    requireNonNull(sql, "sql cannot be null");

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      if (preparedStatementHandler.isPresent()) {
        preparedStatementHandler.get().accept(stmt);
      }
      stmt.execute();
      return stmt.getUpdateCount();
    }
  }

  /**
   * Use to execute a series of SQL statements together as a single transaction. Before calling this
   * method, you must invoke the {@link #withErrorMessage(String) withErrorMessage} method.
   * 
   * @param transactionHandler a consumer procedure that accepts a connection and executes a series
   *        of database operations using that connection
   */
  public void executeTransaction(SqlConsumer<Connection> transactionHandler) {
    requireNonNull(transactionHandler, "transactionHandler cannot be null");
    requireNonNull(errorMessage, "errorMessage cannot be null");

    Connection conn = null;
    try {
      conn = dataSource.getConnection();
      conn.setAutoCommit(false);
      conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
      transactionHandler.accept(conn);
      conn.commit();
    } catch (Throwable e) {
      rollback(conn, e,
          errorMessage);
    } finally {
      closeConnection(conn,
          errorMessage);
    }
  }

  /**
   * Use to execute a series of SQL statements together as a single transaction and return a value.
   * Before calling this method, you must invoke the {@link #withErrorMessage(String)
   * withErrorMessage} method.
   * 
   * @param transactionHandler a function that excepts a connection, executes a series of database
   *        operations using that connection, and then returns a value
   * @return the value returned by the transactionHandler
   */
  public <T> T executeTransactionAndReturnValue(SqlFunction<Connection, T> transactionHandler) {
    requireNonNull(transactionHandler, "transactionHandler cannot be null");
    requireNonNull(errorMessage, "errorMessage cannot be null");

    T result = null;
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
      conn.setAutoCommit(false);
      conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
      result = transactionHandler.apply(conn);
      conn.commit();
      return result;
    } catch (Throwable e) {
      rollback(conn, e,
          errorMessage);
    } finally {
      closeConnection(conn,
          errorMessage);
    }
    return result;
  }

  /**
   * Use to execute a series of SQL statements as separate transactions. Before calling this method,
   * you must invoke the {@link #withErrorMessage(String) withErrorMessage} method.
   * 
   * @param multipleStatementHandler a function that excepts a connection and executes a series of
   *        database operations using that connection
   */
  public void executeMultipleStatements(SqlConsumer<Connection> multipleStatementHandler) {
    requireNonNull(multipleStatementHandler, "transactionHandler cannot be null");
    requireNonNull(errorMessage, "errorMessage cannot be null");

    try (Connection conn = dataSource.getConnection()) {
      multipleStatementHandler.accept(conn);
    } catch (Exception e) {
      if (e instanceof DataAccessException) {
        throw DataAccessException.class.cast(e);
      }
      throw new DataAccessException(errorMessage,
          e);
    }
  }


  /**
   * Use to execute a series of SQL statements as separate transactions and return a value. Before
   * calling this method, you must invoke the {@link #withErrorMessage(String) withErrorMessage}
   * methods.
   * 
   * @param multipleStatementHandler a function that excepts a connection and executes a series of
   *        database operations using that connection
   * @return the value returned by the multipleStatementHandler
   */
  public <T> T executeMultipleStatementsAndReturnValue(
      SqlFunction<Connection, T> multipleStatementHandler) {
    requireNonNull(multipleStatementHandler, "transactionHandler cannot be null");
    requireNonNull(errorMessage, "errorMessage cannot be null");

    try (Connection conn = dataSource.getConnection()) {
      return multipleStatementHandler.apply(conn);
    } catch (Exception e) {
      if (e instanceof DataAccessException) {
        throw DataAccessException.class.cast(e);
      }
      throw new DataAccessException(errorMessage,
          e);
    }
  }

  private void closeConnection(Connection conn, String message) {
    try {
      if (conn != null) {
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        conn.setAutoCommit(true);
        conn.close();
      }
    } catch (SQLException e) {
      throw new DataAccessException(message,
          e);
    }
  }

  private void rollback(Connection conn, Throwable e, String message) {
    if (conn != null) {
      try {
        conn.rollback();
      } catch (Throwable t) {
        log.error("A problem occurred while attempting to rollback a transaction", t);
      }
    }
    if (e instanceof DataAccessException) {
      throw DataAccessException.class.cast(e);
    }
    throw new DataAccessException(message,
        e);
  }
}


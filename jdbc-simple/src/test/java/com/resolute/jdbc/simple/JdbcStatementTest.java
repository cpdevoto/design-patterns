package com.resolute.jdbc.simple;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.resolute.jdbc.simple.fixtures.Foo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static org.hamcrest.CoreMatchers.equalTo;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JdbcStatementTest {
  @Mock
  private DataSource dataSource;
  @Mock
  private Connection conn;
  @Mock
  private PreparedStatement stmt1;
  @Mock
  private PreparedStatement stmt2;
  @Mock
  private ResultSet resultSet;

  @Before
  public void setup() throws SQLException {
    MockitoAnnotations.initMocks(this);
    when(dataSource.getConnection()).thenReturn(conn);
    when(stmt1.executeQuery()).thenReturn(resultSet);
  }

  @Test
  public void test_execute_query() throws SQLException {
    when(conn.prepareStatement(anyString())).thenReturn(stmt1);
    when(resultSet.next()).thenReturn(true, true, false);
    when(resultSet.getInt("id")).thenReturn(1, 2);
    when(resultSet.getString("name")).thenReturn("My First Foo", "My Second Foo");

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    List<Foo> foos = factory.newStatement()
        .withSql("SELECT * FROM foo WHERE bar_id = ?")
        .withErrorMessage("A problem occurred while attempting to retrieve foos.")
        .prepareStatement(s -> {
          s.setInt(1, 5);
        })
        .executeQuery(result -> {
          return result.toList((rowNum, rs) -> {
            Foo f = Foo.builder()
                .withId(rs.getInt("id"))
                .withName(rs.getString("name"))
                .build();
            return f;
          });
        });

    verify(stmt1, times(1)).setInt(eq(1), eq(5));
    assertNotNull(foos);
    assertThat(foos.size(), equalTo(2));
    assertThat(foos.get(0).getId(), equalTo(1));
    assertThat(foos.get(1).getId(), equalTo(2));
    assertThat(foos.get(0).getName(), equalTo("My First Foo"));
    assertThat(foos.get(1).getName(), equalTo("My Second Foo"));
  }

  @Test
  public void test_execute_query_throws_exception() throws SQLException {
    String errorMessage = "A problem occurred while attempting to retrieve foos.";
    SQLException sqlEx = new SQLException();

    when(conn.prepareStatement(anyString())).thenReturn(stmt1);
    when(stmt1.executeQuery()).thenThrow(sqlEx);

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    try {
      factory.newStatement()
          .withSql("SELECT * FROM foo WHERE bar_id = ?")
          .withErrorMessage(errorMessage)
          .prepareStatement(s -> {
            s.setInt(1, 5);
          })
          .executeQuery(result -> {
            return result.toList((rowNum, rs) -> {
              Foo f = Foo.builder()
                  .withId(rs.getInt("id"))
                  .withName(rs.getString("name"))
                  .build();
              return f;
            });
          });
      fail("Expected a DataAccessException");
    } catch (DataAccessException ex) {
      assertThat(ex.getMessage(), equalTo(errorMessage));
    }
  }

  @Test
  public void test_execute() throws SQLException {

    when(conn.prepareStatement(anyString())).thenReturn(stmt1);
    when(stmt1.execute()).thenReturn(true);
    when(stmt1.getUpdateCount()).thenReturn(1);

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    int result = factory.newStatement()
        .withSql("INSERT INTO foo (id, name) VALUES (?, ?)")
        .withErrorMessage("A problem occurred while attempting to insert a foo.")
        .prepareStatement(s -> {
          s.setInt(1, 1);
          s.setString(2, "My First Foo");
        })
        .execute();

    verify(stmt1, times(1)).setInt(eq(1), eq(1));
    verify(stmt1, times(1)).setString(eq(2), eq("My First Foo"));
    verify(stmt1, times(1)).execute();
    assertThat(result, equalTo(1));
  }

  @Test
  public void test_executeBatch() throws SQLException {

    when(conn.prepareStatement(anyString())).thenReturn(stmt1);
    when(stmt1.execute()).thenReturn(true);
    when(stmt1.executeBatch()).thenReturn(new int[] {1, 1});
    when(stmt1.getUpdateCount()).thenReturn(1);

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    int[] result = factory.newStatement()
        .withSql("INSERT INTO foo (id, name) VALUES (?, ?)")
        .withErrorMessage("A problem occurred while attempting to insert a foo.")
        .prepareStatement(s -> {
          s.setInt(1, 1);
          s.setString(2, "My First Foo");
          s.addBatch();
          s.setInt(1, 2);
          s.setString(2, "My Second Foo");
          s.addBatch();
        })
        .executeBatch();

    verify(stmt1, times(1)).setInt(eq(1), eq(1));
    verify(stmt1, times(1)).setString(eq(2), eq("My First Foo"));
    verify(stmt1, times(1)).setInt(eq(1), eq(2));
    verify(stmt1, times(1)).setString(eq(2), eq("My Second Foo"));
    verify(stmt1, times(1)).executeBatch();
    assertNotNull(result);
    assertThat(result.length, equalTo(2));
  }

  @Test
  public void test_execute_throws_exception() throws SQLException {
    String errorMessage = "A problem occurred while attempting to insert a foo.";

    SQLException sqlEx = new SQLException();

    when(conn.prepareStatement(anyString())).thenReturn(stmt1);
    when(stmt1.execute()).thenThrow(sqlEx);

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    try {
      factory.newStatement()
          .withSql("INSERT INTO foo (id, name) VALUES (?, ?)")
          .withErrorMessage(errorMessage)
          .prepareStatement(s -> {
            s.setInt(1, 1);
            s.setString(2, "My First Foo");
          })
          .execute();
      fail("Expected a DataAccessException");
    } catch (DataAccessException ex) {
      assertThat(ex.getMessage(), equalTo(errorMessage));
    }
  }

  @Test
  public void test_execute_transaction() throws SQLException {

    String sql2 = "INSERT INTO bar (name) VALUES (?) RETURNING id";
    String sql1 = "INSERT INTO foo (bar_id, name) VALUES (?, ?)";

    when(conn.prepareStatement(sql1)).thenReturn(stmt1);
    when(conn.prepareStatement(sql2)).thenReturn(stmt2);
    when(stmt1.executeQuery()).thenReturn(resultSet);
    when(stmt2.execute()).thenReturn(true);
    when(stmt2.getUpdateCount()).thenReturn(1);
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getInt("id")).thenReturn(1);

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    factory.newStatement()
        .withErrorMessage("A problem occurred while attempting to insert a bar.")
        .executeTransaction(conn -> {

          verify(conn, times(1)).setAutoCommit(eq(false));
          verify(conn, times(1))
              .setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

          int id = factory.newStatement()
              .withSql(sql1)
              .prepareStatement(stmt -> {
                stmt.setString(1, "My First Bar");
              })
              .executeQueryWithConnection(conn, result -> { // Must pass in the connection!
                return result.toObject((rowNum, rs) -> {
                  return rs.getInt("id");
                });
              });

          verify(stmt1, times(1)).setString(eq(1), eq("My First Bar"));
          assertThat(id, equalTo(1));

          int updateCount = factory.newStatement()
              .withSql(sql2)
              .prepareStatement(stmt -> {
                stmt.setInt(1, id);
                stmt.setString(2, "My First Foo");
              })
              .executeWithConnection(conn); // Must pass in the connection!

          assertThat(updateCount, equalTo(1));
          verify(stmt2, times(1)).setInt(eq(1), eq(id));
          verify(stmt2, times(1)).setString(eq(2), eq("My First Foo"));
          verify(stmt2, times(1)).execute();
        });

    verify(conn, times(1)).commit();
    verify(conn, times(1)).setAutoCommit(eq(true));
    verify(conn, times(2)).setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

  }

  @Test
  public void test_execute_transaction_throws_exception() throws SQLException {

    String sql2 = "INSERT INTO bar (name) VALUES (?) RETURNING id";
    String sql1 = "INSERT INTO foo (bar_id, name) VALUES (?, ?)";
    String errorMessage = "A problem occurred while attempting to insert a bar.";
    SQLException sqlEx = new SQLException();

    when(conn.prepareStatement(sql1)).thenReturn(stmt1);
    when(conn.prepareStatement(sql2)).thenReturn(stmt2);
    when(stmt1.executeQuery()).thenReturn(resultSet);
    when(stmt2.execute()).thenThrow(sqlEx);
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getInt("id")).thenReturn(1);

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    try {
      factory.newStatement()
          .withErrorMessage(errorMessage)
          .executeTransaction(conn -> {

            verify(conn, times(1)).setAutoCommit(eq(false));
            verify(conn, times(1))
                .setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

            int id = factory.newStatement()
                .withSql(sql1)
                .prepareStatement(stmt -> {
                  stmt.setString(1, "My First Bar");
                })
                .executeQueryWithConnection(conn, result -> { // Must pass in the connection!
                  return result.toObject((rowNum, rs) -> {
                    return rs.getInt("id");
                  });
                });

            verify(stmt1, times(1)).setString(eq(1), eq("My First Bar"));
            assertThat(id, equalTo(1));

            factory.newStatement()
                .withSql(sql2)
                .prepareStatement(stmt -> {
                  stmt.setInt(1, id);
                  stmt.setString(2, "My First Foo");
                })
                .executeWithConnection(conn); // Must pass in the connection!
          });
      fail("Expected a DataAccessException");
    } catch (DataAccessException ex) {
      assertThat(ex.getMessage(), equalTo(errorMessage));
      verify(conn, times(1)).rollback();
      verify(conn, times(1)).setAutoCommit(eq(true));
      verify(conn, times(2)).setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

    }
  }

  @Test
  public void test_execute_transaction_and_return_value() throws SQLException {

    String sql2 = "INSERT INTO bar (name) VALUES (?) RETURNING id";
    String sql1 = "INSERT INTO foo (bar_id, name) VALUES (?, ?)";

    when(conn.prepareStatement(sql1)).thenReturn(stmt1);
    when(conn.prepareStatement(sql2)).thenReturn(stmt2);
    when(stmt1.executeQuery()).thenReturn(resultSet);
    when(stmt2.execute()).thenReturn(true);
    when(stmt2.getUpdateCount()).thenReturn(1);
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getInt("id")).thenReturn(1);

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    int identifier = factory.newStatement()
        .withErrorMessage("A problem occurred while attempting to insert a bar.")
        .executeTransactionAndReturnValue(conn -> {

          verify(conn, times(1)).setAutoCommit(eq(false));
          verify(conn, times(1))
              .setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

          int id = factory.newStatement()
              .withSql(sql1)
              .prepareStatement(stmt -> {
                stmt.setString(1, "My First Bar");
              })
              .executeQueryWithConnection(conn, result -> { // Must pass in the connection!
                return result.toObject((rowNum, rs) -> {
                  return rs.getInt("id");
                });
              });

          verify(stmt1, times(1)).setString(eq(1), eq("My First Bar"));
          assertThat(id, equalTo(1));

          int updateCount = factory.newStatement()
              .withSql(sql2)
              .prepareStatement(stmt -> {
                stmt.setInt(1, id);
                stmt.setString(2, "My First Foo");
              })
              .executeWithConnection(conn); // Must pass in the connection!

          assertThat(updateCount, equalTo(1));
          verify(stmt2, times(1)).setInt(eq(1), eq(id));
          verify(stmt2, times(1)).setString(eq(2), eq("My First Foo"));
          verify(stmt2, times(1)).execute();
          return id;
        });

    assertThat(identifier, equalTo(1));
    verify(conn, times(1)).commit();
    verify(conn, times(1)).setAutoCommit(eq(true));
    verify(conn, times(2)).setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

  }

  @Test
  public void test_execute_transaction_and_return_value_throws_exception() throws SQLException {

    String sql2 = "INSERT INTO bar (name) VALUES (?) RETURNING id";
    String sql1 = "INSERT INTO foo (bar_id, name) VALUES (?, ?)";
    String errorMessage = "A problem occurred while attempting to insert a bar.";
    SQLException sqlEx = new SQLException();

    when(conn.prepareStatement(sql1)).thenReturn(stmt1);
    when(conn.prepareStatement(sql2)).thenReturn(stmt2);
    when(stmt1.executeQuery()).thenReturn(resultSet);
    when(stmt2.execute()).thenThrow(sqlEx);
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getInt("id")).thenReturn(1);

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    try {
      factory.newStatement()
          .withErrorMessage(errorMessage)
          .<Integer>executeTransactionAndReturnValue(conn -> {

            verify(conn, times(1)).setAutoCommit(eq(false));
            verify(conn, times(1))
                .setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

            int id = factory.newStatement()
                .withSql(sql1)
                .prepareStatement(stmt -> {
                  stmt.setString(1, "My First Bar");
                })
                .executeQueryWithConnection(conn, result -> { // Must pass in the connection!
                  return result.toObject((rowNum, rs) -> {
                    return rs.getInt("id");
                  });
                });

            verify(stmt1, times(1)).setString(eq(1), eq("My First Bar"));
            assertThat(id, equalTo(1));

            factory.newStatement()
                .withSql(sql2)
                .prepareStatement(stmt -> {
                  stmt.setInt(1, id);
                  stmt.setString(2, "My First Foo");
                })
                .executeWithConnection(conn); // Must pass in the connection!

            return id;
          });
      fail("Expected a DataAccessException");
    } catch (DataAccessException ex) {
      assertThat(ex.getMessage(), equalTo(errorMessage));
      verify(conn, times(1)).rollback();
      verify(conn, times(1)).setAutoCommit(eq(true));
      verify(conn, times(2)).setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

    }
  }

  @Test
  public void test_execute_multiple_statements() throws SQLException {

    String sql2 = "INSERT INTO bar (name) VALUES (?) RETURNING id";
    String sql1 = "INSERT INTO foo (bar_id, name) VALUES (?, ?)";

    when(conn.prepareStatement(sql1)).thenReturn(stmt1);
    when(conn.prepareStatement(sql2)).thenReturn(stmt2);
    when(stmt1.executeQuery()).thenReturn(resultSet);
    when(stmt2.execute()).thenReturn(true);
    when(stmt2.getUpdateCount()).thenReturn(1);
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getInt("id")).thenReturn(1);

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    factory.newStatement()
        .withErrorMessage("A problem occurred while attempting to insert a bar.")
        .executeMultipleStatements(conn -> {

          verify(conn, times(0)).setAutoCommit(eq(false));
          verify(conn, times(0))
              .setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

          int id = factory.newStatement()
              .withSql(sql1)
              .prepareStatement(stmt -> {
                stmt.setString(1, "My First Bar");
              })
              .executeQueryWithConnection(conn, result -> { // Must pass in the connection!
                return result.toObject((rowNum, rs) -> {
                  return rs.getInt("id");
                });
              });

          verify(stmt1, times(1)).setString(eq(1), eq("My First Bar"));
          assertThat(id, equalTo(1));

          int updateCount = factory.newStatement()
              .withSql(sql2)
              .prepareStatement(stmt -> {
                stmt.setInt(1, id);
                stmt.setString(2, "My First Foo");
              })
              .executeWithConnection(conn); // Must pass in the connection!

          assertThat(updateCount, equalTo(1));
          verify(stmt2, times(1)).setInt(eq(1), eq(id));
          verify(stmt2, times(1)).setString(eq(2), eq("My First Foo"));
          verify(stmt2, times(1)).execute();
        });

    verify(conn, times(0)).commit();
    verify(conn, times(0)).setAutoCommit(eq(true));
    verify(conn, times(0)).setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

  }

  @Test
  public void test_execute_multiple_statements_throws_exception() throws SQLException {

    String sql2 = "INSERT INTO bar (name) VALUES (?) RETURNING id";
    String sql1 = "INSERT INTO foo (bar_id, name) VALUES (?, ?)";
    String errorMessage = "A problem occurred while attempting to insert a bar.";
    SQLException sqlEx = new SQLException();

    when(conn.prepareStatement(sql1)).thenReturn(stmt1);
    when(conn.prepareStatement(sql2)).thenReturn(stmt2);
    when(stmt1.executeQuery()).thenReturn(resultSet);
    when(stmt2.execute()).thenThrow(sqlEx);
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getInt("id")).thenReturn(1);

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    try {
      factory.newStatement()
          .withErrorMessage(errorMessage)
          .executeMultipleStatements(conn -> {

            verify(conn, times(0)).setAutoCommit(eq(false));
            verify(conn, times(0))
                .setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

            int id = factory.newStatement()
                .withSql(sql1)
                .prepareStatement(stmt -> {
                  stmt.setString(1, "My First Bar");
                })
                .executeQueryWithConnection(conn, result -> { // Must pass in the connection!
                  return result.toObject((rowNum, rs) -> {
                    return rs.getInt("id");
                  });
                });

            verify(stmt1, times(1)).setString(eq(1), eq("My First Bar"));
            assertThat(id, equalTo(1));

            factory.newStatement()
                .withSql(sql2)
                .prepareStatement(stmt -> {
                  stmt.setInt(1, id);
                  stmt.setString(2, "My First Foo");
                })
                .executeWithConnection(conn); // Must pass in the connection!
          });
      fail("Expected a DataAccessException");
    } catch (DataAccessException ex) {
      assertThat(ex.getMessage(), equalTo(errorMessage));
      verify(conn, times(0)).rollback();
      verify(conn, times(0)).setAutoCommit(eq(true));
      verify(conn, times(0)).setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

    }
  }

  @Test
  public void test_execute_multiple_statements_and_return_value() throws SQLException {

    String sql2 = "INSERT INTO bar (name) VALUES (?) RETURNING id";
    String sql1 = "INSERT INTO foo (bar_id, name) VALUES (?, ?)";

    when(conn.prepareStatement(sql1)).thenReturn(stmt1);
    when(conn.prepareStatement(sql2)).thenReturn(stmt2);
    when(stmt1.executeQuery()).thenReturn(resultSet);
    when(stmt2.execute()).thenReturn(true);
    when(stmt2.getUpdateCount()).thenReturn(1);
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getInt("id")).thenReturn(1);

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    int identifier = factory.newStatement()
        .withErrorMessage("A problem occurred while attempting to insert a bar.")
        .executeMultipleStatementsAndReturnValue(conn -> {

          verify(conn, times(0)).setAutoCommit(eq(false));
          verify(conn, times(0))
              .setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

          int id = factory.newStatement()
              .withSql(sql1)
              .prepareStatement(stmt -> {
                stmt.setString(1, "My First Bar");
              })
              .executeQueryWithConnection(conn, result -> { // Must pass in the connection!
                return result.toObject((rowNum, rs) -> {
                  return rs.getInt("id");
                });
              });

          verify(stmt1, times(1)).setString(eq(1), eq("My First Bar"));
          assertThat(id, equalTo(1));

          int updateCount = factory.newStatement()
              .withSql(sql2)
              .prepareStatement(stmt -> {
                stmt.setInt(1, id);
                stmt.setString(2, "My First Foo");
              })
              .executeWithConnection(conn); // Must pass in the connection!

          assertThat(updateCount, equalTo(1));
          verify(stmt2, times(1)).setInt(eq(1), eq(id));
          verify(stmt2, times(1)).setString(eq(2), eq("My First Foo"));
          verify(stmt2, times(1)).execute();

          return id;
        });

    assertThat(identifier, equalTo(1));
    verify(conn, times(0)).commit();
    verify(conn, times(0)).setAutoCommit(eq(true));
    verify(conn, times(0)).setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

  }

  @Test
  public void test_execute_multiple_statements_and_return_value_throws_exception()
      throws SQLException {

    String sql2 = "INSERT INTO bar (name) VALUES (?) RETURNING id";
    String sql1 = "INSERT INTO foo (bar_id, name) VALUES (?, ?)";
    String errorMessage = "A problem occurred while attempting to insert a bar.";
    SQLException sqlEx = new SQLException();

    when(conn.prepareStatement(sql1)).thenReturn(stmt1);
    when(conn.prepareStatement(sql2)).thenReturn(stmt2);
    when(stmt1.executeQuery()).thenReturn(resultSet);
    when(stmt2.execute()).thenThrow(sqlEx);
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getInt("id")).thenReturn(1);

    JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

    try {
      factory.newStatement()
          .withErrorMessage(errorMessage)
          .<Integer>executeMultipleStatementsAndReturnValue(conn -> {

            verify(conn, times(0)).setAutoCommit(eq(false));
            verify(conn, times(0))
                .setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

            int id = factory.newStatement()
                .withSql(sql1)
                .prepareStatement(stmt -> {
                  stmt.setString(1, "My First Bar");
                })
                .executeQueryWithConnection(conn, result -> { // Must pass in the connection!
                  return result.toObject((rowNum, rs) -> {
                    return rs.getInt("id");
                  });
                });

            verify(stmt1, times(1)).setString(eq(1), eq("My First Bar"));
            assertThat(id, equalTo(1));

            factory.newStatement()
                .withSql(sql2)
                .prepareStatement(stmt -> {
                  stmt.setInt(1, id);
                  stmt.setString(2, "My First Foo");
                })
                .executeWithConnection(conn); // Must pass in the connection!

            return id;
          });
      fail("Expected a DataAccessException");
    } catch (DataAccessException ex) {
      assertThat(ex.getMessage(), equalTo(errorMessage));
      verify(conn, times(0)).rollback();
      verify(conn, times(0)).setAutoCommit(eq(true));
      verify(conn, times(0)).setTransactionIsolation(eq(Connection.TRANSACTION_READ_COMMITTED));

    }
  }


}

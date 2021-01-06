package com.resolute.jdbc.simple;

import static com.resolute.jdbc.simple.QueryHandler.processList;
import static com.resolute.jdbc.simple.QueryHandler.processObject;
import static com.resolute.jdbc.simple.QueryHandler.toList;
import static com.resolute.jdbc.simple.QueryHandler.toObject;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import com.resolute.jdbc.simple.fixtures.Bar;
import com.resolute.jdbc.simple.fixtures.DataSourceBuilder;
import com.resolute.jdbc.simple.fixtures.Foo;

@Testcontainers
public class JdbcStatementTest {
  @Container
  private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:9.6.12");

  @BeforeAll
  public static void beforeAll() throws SQLException, IOException {
    executeDatabaseScript(dataSource -> {
      DaoUtils.executeSqlScript(dataSource, JdbcStatementTest.class,
          "jdbcstatement-test-data.sql");
    });
  }

  @AfterAll
  public static void afterAll() throws SQLException, IOException {
    executeDatabaseScript(dataSource -> {
      DaoUtils.executeSqlScript(dataSource, JdbcStatementTest.class,
          "jdbcstatement-test-teardown.sql");
    });
  }

  @Nested
  @DisplayName("executeQuery")
  class ExecuteQuery {
    @Test
    public void test_execute_query() throws SQLException {
      executeDatabaseFunction(dataSource -> {
        try {
          insertBars(dataSource, ImmutableList.of(Bar.Column.ID, Bar.Column.NAME),
              createBar(5, "My First Bar"));
          insertFoos(dataSource,
              ImmutableList.of(Foo.Column.ID, Foo.Column.BAR_ID, Foo.Column.NAME),
              createFoo(1, 5, "My First Foo"),
              createFoo(2, 5, "My Second Foo"));

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          List<Foo> foos = factory.newStatement()
              .withSql("SELECT * FROM foo WHERE bar_id = ?")
              .withErrorMessage("A problem occurred while attempting to retrieve foos.")
              .prepareStatement(s -> {
                s.setInt(1, 5);
              })
              .executeQuery(result -> result.toList(rs -> {
                Foo f = Foo.builder()
                    .withId(rs.getInt("id"))
                    .withName(rs.getString("name"))
                    .build();
                return f;
              }));

          assertFoos(foos,
              createFoo(1, "My First Foo"),
              createFoo(2, "My Second Foo"));

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }

    @Test
    public void test_execute_query_throws_exception() throws SQLException {
      executeDatabaseFunction(dataSource -> {
        String errorMessage = "A problem occurred while attempting to retrieve foos.";

        JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

        try {
          factory.newStatement()
              .withSql("SELECT * FROM foosxf WHERE bar_id = ?")
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
          assertThat(ex.getMessage()).isEqualTo(errorMessage);
        }

      });
    }
  }

  @Nested
  @DisplayName("executeQueryUsingQueryHandler")
  class ExecuteQueryUsingQueryHandler {
    @Test
    public void test_execute_query_with_query_handler_to_list() throws SQLException {
      executeDatabaseFunction(dataSource -> {
        try {
          insertBars(dataSource, ImmutableList.of(Bar.Column.ID, Bar.Column.NAME),
              createBar(5, "My First Bar"));
          insertFoos(dataSource,
              ImmutableList.of(Foo.Column.ID, Foo.Column.BAR_ID, Foo.Column.NAME),
              createFoo(1, 5, "My First Foo"),
              createFoo(2, 5, "My Second Foo"));

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          List<Foo> foos = factory.newStatement()
              .withSql("SELECT * FROM foo WHERE bar_id = ?")
              .withErrorMessage("A problem occurred while attempting to retrieve foos.")
              .prepareStatement(s -> {
                s.setInt(1, 5);
              })
              .executeQuery(toList(rs -> {
                Foo f = Foo.builder()
                    .withId(rs.getInt("id"))
                    .withName(rs.getString("name"))
                    .build();
                return f;
              }));

          assertFoos(foos,
              createFoo(1, "My First Foo"),
              createFoo(2, "My Second Foo"));

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }

    @Test
    public void test_execute_query_w_query_handler_process_list()
        throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          insertBars(dataSource, ImmutableList.of(Bar.Column.ID, Bar.Column.NAME),
              createBar(5, "My First Bar"));
          insertFoos(dataSource,
              ImmutableList.of(Foo.Column.ID, Foo.Column.BAR_ID, Foo.Column.NAME),
              createFoo(1, 5, "My First Foo"),
              createFoo(2, 5, "My Second Foo"));

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          List<Foo> foos = new ArrayList<>();
          factory.newStatement()
              .withSql("SELECT * FROM foo WHERE bar_id = ?")
              .withErrorMessage("A problem occurred while attempting to retrieve foos.")
              .prepareStatement(s -> {
                s.setInt(1, 5);
              })
              .executeQueryNoReturn(processList(rs -> {
                Foo f = Foo.builder()
                    .withId(rs.getInt("id"))
                    .withName(rs.getString("name"))
                    .build();
                foos.add(f);
              }));

          assertFoos(foos,
              createFoo(1, "My First Foo"),
              createFoo(2, "My Second Foo"));

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });

    }

    @Test
    public void test_execute_query_with_query_handler_to_object() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          insertBars(dataSource, ImmutableList.of(Bar.Column.ID, Bar.Column.NAME),
              createBar(5, "My First Bar"));
          insertFoos(dataSource,
              ImmutableList.of(Foo.Column.ID, Foo.Column.BAR_ID, Foo.Column.NAME),
              createFoo(1, 5, "My First Foo"));

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          Foo foo = factory.newStatement()
              .withSql("SELECT * FROM foo WHERE bar_id = ?")
              .withErrorMessage("A problem occurred while attempting to retrieve foos.")
              .prepareStatement(s -> {
                s.setInt(1, 5);
              })
              .executeQuery(toObject(rs -> {
                Foo f = Foo.builder()
                    .withId(rs.getInt("id"))
                    .withName(rs.getString("name"))
                    .build();
                return f;
              }));

          assertFoo(foo,
              createFoo(1, "My First Foo"));

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }

    @Test
    public void test_execute_query_w_query_handler_process_object()
        throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          insertBars(dataSource, ImmutableList.of(Bar.Column.ID, Bar.Column.NAME),
              createBar(5, "My First Bar"));
          insertFoos(dataSource,
              ImmutableList.of(Foo.Column.ID, Foo.Column.BAR_ID, Foo.Column.NAME),
              createFoo(1, 5, "My First Foo"));

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          List<Foo> foos = new ArrayList<>();
          factory.newStatement()
              .withSql("SELECT * FROM foo WHERE bar_id = ?")
              .withErrorMessage("A problem occurred while attempting to retrieve foos.")
              .prepareStatement(s -> {
                s.setInt(1, 5);
              })
              .executeQueryNoReturn(processObject(rs -> {
                Foo f = Foo.builder()
                    .withId(rs.getInt("id"))
                    .withName(rs.getString("name"))
                    .build();
                foos.add(f);
              }));

          assertFoos(foos,
              createFoo(1, "My First Foo"));

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }

  }


  @Nested
  @DisplayName("executeQueryWithConnection")
  class ExecuteQueryWithConnection {


    @Test
    public void test_execute_query_w_connection() throws SQLException {
      executeDatabaseFunction(dataSource -> {
        try {
          insertBars(dataSource, ImmutableList.of(Bar.Column.ID, Bar.Column.NAME),
              createBar(5, "My First Bar"));
          insertFoos(dataSource,
              ImmutableList.of(Foo.Column.ID, Foo.Column.BAR_ID, Foo.Column.NAME),
              createFoo(1, 5, "My First Foo"),
              createFoo(2, 5, "My Second Foo"));

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          try (Connection conn = dataSource.getConnection()) {
            List<Foo> foos = factory.newStatement()
                .withSql("SELECT * FROM foo WHERE bar_id = ?")
                .withErrorMessage("A problem occurred while attempting to retrieve foos.")
                .prepareStatement(s -> {
                  s.setInt(1, 5);
                })
                .executeQueryWithConnection(conn, result -> result.toList(rs -> {
                  Foo f = Foo.builder()
                      .withId(rs.getInt("id"))
                      .withName(rs.getString("name"))
                      .build();
                  return f;
                }));

            assertFoos(foos,
                createFoo(1, "My First Foo"),
                createFoo(2, "My Second Foo"));
          }
        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }


    @Test
    public void test_execute_w_conn_query_throws_exception() throws SQLException {
      executeDatabaseFunction(dataSource -> {
        String errorMessage = "A problem occurred while attempting to retrieve foos.";

        JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

        try (Connection conn = dataSource.getConnection()) {
          factory.newStatement()
              .withSql("SELECT * FROM foosxf WHERE bar_id = ?")
              .withErrorMessage(errorMessage)
              .prepareStatement(s -> {
                s.setInt(1, 5);
              })
              .executeQueryWithConnection(conn, result -> {
                return result.toList((rowNum, rs) -> {
                  Foo f = Foo.builder()
                      .withId(rs.getInt("id"))
                      .withName(rs.getString("name"))
                      .build();
                  return f;
                });
              });
          fail("Expected an SQLException");
        } catch (SQLException ex) {
        }

      });
    }
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    public void test_execute() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          int result = factory.newStatement()
              .withSql("INSERT INTO foo (id, name) VALUES (?, ?)")
              .withErrorMessage("A problem occurred while attempting to insert a foo.")
              .prepareStatement(s -> {
                s.setInt(1, 1);
                s.setString(2, "My First Foo");
              })
              .execute();
          assertThat(result).isEqualTo(1);

          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos,
              createFoo(1, "My First Foo"));
        } finally {
          deleteFoosAndBars(dataSource);
        }

      });

    }

    @Test
    public void test_execute_throws_exception() throws SQLException {
      executeDatabaseFunction(dataSource -> {
        String errorMessage = "A problem occurred while attempting to insert a foo.";

        JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

        try {
          factory.newStatement()
              .withSql("INSERT INTO foosx (id, name) VALUES (?, ?)")
              .withErrorMessage(errorMessage)
              .prepareStatement(s -> {
                s.setInt(1, 1);
                s.setString(2, "My First Foo");
              })
              .execute();
          fail("Expected a DataAccessException");
        } catch (DataAccessException ex) {
          assertThat(ex.getMessage()).isEqualTo(errorMessage);
        }

      });

    }
  }

  @Nested
  @DisplayName("executeWithConnection")
  class ExecuteWithConnection {

    @Test
    public void test_execute_w_connection() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          try (Connection conn = dataSource.getConnection()) {
            int result = factory.newStatement()
                .withSql("INSERT INTO foo (id, name) VALUES (?, ?)")
                .withErrorMessage("A problem occurred while attempting to insert a foo.")
                .prepareStatement(s -> {
                  s.setInt(1, 1);
                  s.setString(2, "My First Foo");
                })
                .executeWithConnection(conn);
            assertThat(result).isEqualTo(1);

            List<Foo> foos = retrieveFoos(dataSource);

            assertFoos(foos,
                createFoo(1, "My First Foo"));
          }
        } finally {
          deleteFoosAndBars(dataSource);
        }

      });

    }

    @Test
    public void test_execute_w_conn_throws_exception() throws SQLException {
      executeDatabaseFunction(dataSource -> {
        String errorMessage = "A problem occurred while attempting to insert a foo.";

        JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

        try (Connection conn = dataSource.getConnection()) {
          factory.newStatement()
              .withSql("INSERT INTO foosx (id, name) VALUES (?, ?)")
              .withErrorMessage(errorMessage)
              .prepareStatement(s -> {
                s.setInt(1, 1);
                s.setString(2, "My First Foo");
              })
              .executeWithConnection(conn);
          fail("Expected an SQLException");
        } catch (SQLException ex) {
        }

      });

    }
  }

  @Nested
  @DisplayName("executeBatch")
  class ExecuteBatch {

    @Test
    public void test_execute_batch() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
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
          assertThat(result).isNotNull().hasSize(2);


          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos,
              createFoo(1, "My First Foo"),
              createFoo(2, "My Second Foo"));
        } finally {
          deleteFoosAndBars(dataSource);
        }

      });

    }

    @Test
    public void test_execute_batch_throws_exception() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        String errorMessage = "A problem occurred while attempting to insert foos in batch.";

        JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

        try {
          factory.newStatement()
              .withSql("INSERT INTO foosx (id, name) VALUES (?, ?)")
              .withErrorMessage(errorMessage)
              .prepareStatement(s -> {
                s.setInt(1, 1);
                s.setString(2, "My First Foo");
                s.addBatch();
                s.setInt(1, 2);
                s.setString(2, "My Second Foo");
                s.addBatch();
              })
              .executeBatch();
          fail("Expected a DataAccessException");
        } catch (DataAccessException ex) {
          assertThat(ex.getMessage()).isEqualTo(errorMessage);
        }
      });

    }
  }

  @Nested
  @DisplayName("executeBatchWithConnection")
  class ExecuteBatchWithConnection {

    @Test
    public void test_execute_batch_w_connection() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          try (Connection conn = dataSource.getConnection()) {
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
                .executeBatchWithConnection(conn);
            assertThat(result).isNotNull().hasSize(2);


            List<Foo> foos = retrieveFoos(dataSource);

            assertFoos(foos,
                createFoo(1, "My First Foo"),
                createFoo(2, "My Second Foo"));
          }
        } finally {
          deleteFoosAndBars(dataSource);
        }

      });

    }

    @Test
    public void test_execute_batch_w_connection_throws_exception() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        String errorMessage = "A problem occurred while attempting to insert foos in batch.";

        JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

        try (Connection conn = dataSource.getConnection()) {
          factory.newStatement()
              .withSql("INSERT INTO foosx (id, name) VALUES (?, ?)")
              .withErrorMessage(errorMessage)
              .prepareStatement(s -> {
                s.setInt(1, 1);
                s.setString(2, "My First Foo");
                s.addBatch();
                s.setInt(1, 2);
                s.setString(2, "My Second Foo");
                s.addBatch();
              })
              .executeBatchWithConnection(conn);
          fail("Expected an SQLException");
        } catch (SQLException ex) {
        }
      });

    }
  }

  @Nested
  @DisplayName("executeBatchAndReturnPkValues")
  class ExecuteBatchAndReturnPkValues {

    @Test
    public void test_exec_batch_and_return_pk() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          resetSequences(dataSource);

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          int[] result = factory.newStatement()
              .withSql("INSERT INTO foo (name) VALUES (?)")
              .withErrorMessage("A problem occurred while attempting to insert foos in batch.")
              .prepareStatement(s -> {
                s.setString(1, "My First Foo");
                s.addBatch();
                s.setString(1, "My Second Foo");
                s.addBatch();
              })
              .executeBatchAndReturnPkValues();
          assertThat(result).isNotNull().containsExactly(1, 2);


          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos,
              createFoo(1, "My First Foo"),
              createFoo(2, "My Second Foo"));
        } finally {
          deleteFoosAndBars(dataSource);
        }

      });

    }

    @Test
    public void test_exec_batch_and_return_pk_throws_exception() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          resetSequences(dataSource);

          String errorMessage = "A problem occurred while attempting to insert foos in batch.";

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          try {
            factory.newStatement()
                .withSql("INSERT INTO foosx (name) VALUES (?)")
                .withErrorMessage(errorMessage)
                .prepareStatement(s -> {
                  s.setString(1, "My First Foo");
                  s.addBatch();
                  s.setString(1, "My Second Foo");
                  s.addBatch();
                })
                .executeBatchAndReturnPkValues();
          } catch (DataAccessException e) {
            assertThat(e.getMessage()).isEqualTo(errorMessage);
          }

          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos);
        } finally {
          deleteFoosAndBars(dataSource);
        }

      });

    }
  }

  @Nested
  @DisplayName("executeBatchWithConnectionAndReturnPkValues")
  class ExecuteBatchWithConnectionAndReturnPkValues {
    @Test
    public void test_exec_batch_w_connection_and_return_pk() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          resetSequences(dataSource);

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          try (Connection conn = dataSource.getConnection()) {
            int[] result = factory.newStatement()
                .withSql("INSERT INTO foo (name) VALUES (?)")
                .withErrorMessage("A problem occurred while attempting to insert foos in batch.")
                .prepareStatement(s -> {
                  s.setString(1, "My First Foo");
                  s.addBatch();
                  s.setString(1, "My Second Foo");
                  s.addBatch();
                })
                .executeBatchWithConnectionAndReturnPkValues(conn);
            assertThat(result).isNotNull().containsExactly(1, 2);


            List<Foo> foos = retrieveFoos(dataSource);

            assertFoos(foos,
                createFoo(1, "My First Foo"),
                createFoo(2, "My Second Foo"));

          }

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });

    }

    @Test
    public void test_exec_batch_w_conn_and_return_pk_throws_exception() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          resetSequences(dataSource);

          String errorMessage = "A problem occurred while attempting to insert foos in batch.";

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          try (Connection conn = dataSource.getConnection()) {
            factory.newStatement()
                .withSql("INSERT INTO foosx (name) VALUES (?)")
                .withErrorMessage(errorMessage)
                .prepareStatement(s -> {
                  s.setString(1, "My First Foo");
                  s.addBatch();
                  s.setString(1, "My Second Foo");
                  s.addBatch();
                })
                .executeBatchWithConnectionAndReturnPkValues(conn);
            fail("Expected an SQLException");
          } catch (SQLException e) {
          }

          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos);
        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }
  }

  @Nested
  @DisplayName("executeTransaction")
  class ExecuteTransaction {


    @Test
    public void test_execute_transaction() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          String sql1 = "INSERT INTO bar (id, name) VALUES (?, ?)";
          String sql2 = "INSERT INTO foo (id, bar_id, name) VALUES (?, ?, ?)";

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          factory.newStatement()
              .withErrorMessage(
                  "A problem occurred while attempting to insert a bar and a foo in a single transaction.")
              .executeTransaction(conn -> {

                int updateCount = factory.newStatement()
                    .withSql(sql1)
                    .prepareStatement(stmt -> {
                      stmt.setInt(1, 5);
                      stmt.setString(2, "My First Bar");
                    })
                    .executeWithConnection(conn);

                assertThat(updateCount).isEqualTo(1);

                updateCount = factory.newStatement()
                    .withSql(sql2)
                    .prepareStatement(stmt -> {
                      stmt.setInt(1, 1);
                      stmt.setInt(2, 5);
                      stmt.setString(3, "My First Foo");
                    })
                    .executeWithConnection(conn); // Must pass in the connection!

                assertThat(updateCount).isEqualTo(1);

              });


          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos,
              createFoo(1, 5, "My First Foo"));

          List<Bar> bars = retrieveBars(dataSource);

          assertBars(bars,
              createBar(5, "My First Bar"));

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });


    }

    @Test
    public void test_execute_transaction_throws_exception() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          String errorMessage =
              "A problem occurred while attempting to insert a bar and a foo in a single transaction.";
          String sql1 = "INSERT INTO bar (id, name) VALUES (?, ?)";
          String sql2 = "INSERT INTO foosx (id, bar_id, name) VALUES (?, ?, ?)";

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          try {
            factory.newStatement()
                .withErrorMessage(
                    "A problem occurred while attempting to insert a bar and a foo in a single transaction.")
                .executeTransaction(conn -> {

                  int updateCount = factory.newStatement()
                      .withSql(sql1)
                      .prepareStatement(stmt -> {
                        stmt.setInt(1, 5);
                        stmt.setString(2, "My First Bar");
                      })
                      .executeWithConnection(conn);

                  assertThat(updateCount).isEqualTo(1);

                  updateCount = factory.newStatement()
                      .withSql(sql2)
                      .prepareStatement(stmt -> {
                        stmt.setInt(1, 1);
                        stmt.setInt(2, 5);
                        stmt.setString(3, "My First Foo");
                      })
                      .executeWithConnection(conn); // Must pass in the connection!

                  assertThat(updateCount).isEqualTo(1);

                });
            fail("Expected a DataAccessException");
          } catch (DataAccessException e) {
            assertThat(e.getMessage()).isEqualTo(errorMessage);
          }


          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos);

          List<Bar> bars = retrieveBars(dataSource);

          assertBars(bars);

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }
  }

  @Nested
  @DisplayName("executeTransactionAndReturnValue")
  class ExecuteTransactionAndReturnValue {


    @Test
    public void test_execute_transaction_and_return_value() throws SQLException {
      executeDatabaseFunction(dataSource -> {
        try {
          resetSequences(dataSource);

          String sql1 = "INSERT INTO bar (name) VALUES (?) RETURNING id";
          String sql2 = "INSERT INTO foo (bar_id, name) VALUES (?, ?)";

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          int identifier = factory.newStatement()
              .withErrorMessage("A problem occurred while attempting to insert a bar.")
              .executeTransactionAndReturnValue(conn -> {

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

                assertThat(id).isEqualTo(1);

                int updateCount = factory.newStatement()
                    .withSql(sql2)
                    .prepareStatement(stmt -> {
                      stmt.setInt(1, id);
                      stmt.setString(2, "My First Foo");
                    })
                    .executeWithConnection(conn); // Must pass in the connection!

                assertThat(updateCount).isEqualTo(1);
                return id;
              });

          assertThat(identifier).isEqualTo(1);

          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos,
              createFoo(1, 1, "My First Foo"));

          List<Bar> bars = retrieveBars(dataSource);

          assertBars(bars,
              createBar(1, "My First Bar"));

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }

    @Test
    public void test_x_transaction_and_return_value_throws_exception() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          resetSequences(dataSource);

          String errorMessage =
              "A problem occurred while attempting to insert a foo and a bar in a transaction.";
          String sql1 = "INSERT INTO bar (name) VALUES (?) RETURNING id";
          String sql2 = "INSERT INTO foosx (bar_id, name) VALUES (?, ?)";

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          try {
            factory.newStatement()
                .withErrorMessage(errorMessage)
                .executeTransactionAndReturnValue(conn -> {

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

                  assertThat(id).isEqualTo(1);

                  int updateCount = factory.newStatement()
                      .withSql(sql2)
                      .prepareStatement(stmt -> {
                        stmt.setInt(1, id);
                        stmt.setString(2, "My First Foo");
                      })
                      .executeWithConnection(conn); // Must pass in the connection!

                  assertThat(updateCount).isEqualTo(1);
                  return id;
                });
            fail("Expected a DataAccessException");
          } catch (DataAccessException e) {
            assertThat(e.getMessage()).isEqualTo(errorMessage);
          }

          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos);

          List<Bar> bars = retrieveBars(dataSource);

          assertBars(bars);

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }
  }

  @Nested
  @DisplayName("executeMultipleStatements")
  class ExecuteMultipleStatements {

    @Test
    public void test_exec_multiple_statements() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          String sql1 = "INSERT INTO bar (id, name) VALUES (?, ?)";
          String sql2 = "INSERT INTO foo (id, bar_id, name) VALUES (?, ?, ?)";

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          factory.newStatement()
              .withErrorMessage(
                  "A problem occurred while attempting to insert a bar and a foo using the same connection.")
              .executeMultipleStatements(conn -> {

                int updateCount = factory.newStatement()
                    .withSql(sql1)
                    .prepareStatement(stmt -> {
                      stmt.setInt(1, 5);
                      stmt.setString(2, "My First Bar");
                    })
                    .executeWithConnection(conn);

                assertThat(updateCount).isEqualTo(1);

                updateCount = factory.newStatement()
                    .withSql(sql2)
                    .prepareStatement(stmt -> {
                      stmt.setInt(1, 1);
                      stmt.setInt(2, 5);
                      stmt.setString(3, "My First Foo");
                    })
                    .executeWithConnection(conn); // Must pass in the connection!

                assertThat(updateCount).isEqualTo(1);

              });


          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos,
              createFoo(1, 5, "My First Foo"));

          List<Bar> bars = retrieveBars(dataSource);

          assertBars(bars,
              createBar(5, "My First Bar"));

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }

    @Test
    public void test_x_multiple_statements_throws_exception() throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          String errorMessage =
              "A problem occurred while attempting to insert a bar and a foo using the same connection.";
          String sql1 = "INSERT INTO bar (id, name) VALUES (?, ?)";
          String sql2 = "INSERT INTO foosx (id, bar_id, name) VALUES (?, ?, ?)";

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          try {
            factory.newStatement()
                .withErrorMessage(errorMessage)
                .executeMultipleStatements(conn -> {

                  int updateCount = factory.newStatement()
                      .withSql(sql1)
                      .prepareStatement(stmt -> {
                        stmt.setInt(1, 5);
                        stmt.setString(2, "My First Bar");
                      })
                      .executeWithConnection(conn);

                  assertThat(updateCount).isEqualTo(1);

                  updateCount = factory.newStatement()
                      .withSql(sql2)
                      .prepareStatement(stmt -> {
                        stmt.setInt(1, 1);
                        stmt.setInt(2, 5);
                        stmt.setString(3, "My First Foo");
                      })
                      .executeWithConnection(conn); // Must pass in the connection!

                  assertThat(updateCount).isEqualTo(1);

                });
            fail("Expected a DataAccessException");
          } catch (DataAccessException e) {
            assertThat(e.getMessage()).isEqualTo(errorMessage);
          }

          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos);

          List<Bar> bars = retrieveBars(dataSource);

          assertBars(bars,
              createBar(5, "My First Bar"));

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }
  }

  @Nested
  @DisplayName("executeMultipleStatementsAndReturnValue")
  class ExecuteMultipleStatementsAndReturnValue {


    @Test
    public void test_x_multiple_statements_and_return_value() throws SQLException {
      executeDatabaseFunction(dataSource -> {
        try {
          resetSequences(dataSource);

          String sql1 = "INSERT INTO bar (name) VALUES (?) RETURNING id";
          String sql2 = "INSERT INTO foo (bar_id, name) VALUES (?, ?)";

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          int identifier = factory.newStatement()
              .withErrorMessage(
                  "A problem occurred while attempting to insert foo and a bar using the same connection.")
              .executeMultipleStatementsAndReturnValue(conn -> {

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

                assertThat(id).isEqualTo(1);

                int updateCount = factory.newStatement()
                    .withSql(sql2)
                    .prepareStatement(stmt -> {
                      stmt.setInt(1, id);
                      stmt.setString(2, "My First Foo");
                    })
                    .executeWithConnection(conn); // Must pass in the connection!

                assertThat(updateCount).isEqualTo(1);
                return id;
              });

          assertThat(identifier).isEqualTo(1);

          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos,
              createFoo(1, 1, "My First Foo"));

          List<Bar> bars = retrieveBars(dataSource);

          assertBars(bars,
              createBar(1, "My First Bar"));

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }

    @Test
    public void test_x_multiple_statements_and_return_value_throws_exception()
        throws SQLException {

      executeDatabaseFunction(dataSource -> {
        try {
          resetSequences(dataSource);

          String errorMessage =
              "A problem occurred while attempting to insert foo and a bar using the same connection.";
          String sql1 = "INSERT INTO bar (name) VALUES (?) RETURNING id";
          String sql2 = "INSERT INTO foosx (bar_id, name) VALUES (?, ?)";

          JdbcStatementFactory factory = JdbcStatementFactory.getInstance(dataSource);

          try {
            factory.newStatement()
                .withErrorMessage(errorMessage)
                .executeMultipleStatementsAndReturnValue(conn -> {

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

                  assertThat(id).isEqualTo(1);

                  int updateCount = factory.newStatement()
                      .withSql(sql2)
                      .prepareStatement(stmt -> {
                        stmt.setInt(1, id);
                        stmt.setString(2, "My First Foo");
                      })
                      .executeWithConnection(conn); // Must pass in the connection!

                  assertThat(updateCount).isEqualTo(1);
                  return id;
                });
            fail("Expected a DataAccessException");
          } catch (DataAccessException e) {
            assertThat(e.getMessage()).isEqualTo(errorMessage);
          }

          List<Foo> foos = retrieveFoos(dataSource);

          assertFoos(foos);

          List<Bar> bars = retrieveBars(dataSource);

          assertBars(bars,
              createBar(1, "My First Bar"));

        } finally {
          deleteFoosAndBars(dataSource);
        }

      });
    }
  }

  // ------------------------
  // Helper Methods
  // ------------------------

  private static void executeDatabaseFunction(DatabaseFunction function)
      throws SQLException {
    DataSource dataSource = createDataSource();
    function.execute(dataSource);
  }

  private static void executeDatabaseScript(DatabaseScriptFunction function)
      throws SQLException, IOException {
    DataSource dataSource = createDataSource();
    function.execute(dataSource);
  }

  private static DataSource createDataSource() {
    DataSource dataSource = DataSourceBuilder.newInstance()
        .withHost(postgres.getHost())
        .withPort(postgres.getFirstMappedPort())
        .withDatabase(postgres.getDatabaseName())
        .withUsername(postgres.getUsername())
        .withPassword(postgres.getPassword())
        .build();
    return dataSource;
  }

  private static List<Foo> retrieveFoos(DataSource dataSource) throws SQLException {
    List<Foo> foos = new ArrayList<>();
    executeSqlQuery(dataSource, "SELECT id, bar_id, name FROM foo", rs -> {
      Foo.Builder builder = Foo.builder()
          .withId(rs.getInt("id"))
          .withName(rs.getString("name"));
      if (rs.getObject("bar_id") != null) {
        builder.withBarId(rs.getInt("bar_id"));
      }
      Foo foo = builder.build();
      foos.add(foo);
    });
    return foos;
  }

  private static List<Bar> retrieveBars(DataSource dataSource) throws SQLException {
    List<Bar> bars = new ArrayList<>();
    executeSqlQuery(dataSource, "SELECT id, name FROM bar", rs -> {
      Bar bar = Bar.builder()
          .withId(rs.getInt("id"))
          .withName(rs.getString("name"))
          .build();
      bars.add(bar);
    });
    return bars;
  }

  private static void insertFoos(DataSource dataSource, List<Foo.Column> columns, Foo... foos)
      throws SQLException {
    StringBuilder buf = new StringBuilder("INSERT INTO foo (");
    buf.append(columns.stream().map(Foo.Column::getName).collect(joining(", ")));
    buf.append(") VALUES\n");
    buf.append(
        Arrays.stream(foos)
            .map(foo -> {
              StringBuilder buf2 = new StringBuilder();
              buf2.append("  (");
              buf2.append(
                  columns.stream()
                      .map(column -> {
                        switch (column) {
                          case ID:
                            return foo.getId().map(String::valueOf).orElse("null");
                          case BAR_ID:
                            return foo.getBarId().map(String::valueOf).orElse("null");
                          case NAME:
                            return String.format("'%s'", foo.getName());
                          default:
                            throw new AssertionError("Unrecognized column: " + column);
                        }
                      })
                      .collect(joining(", ")));
              buf2.append(")");
              return buf2.toString();
            })
            .collect(joining(",\n")));
    buf.append(";");
    String sql = buf.toString();
    executeSql(dataSource, sql);
  }

  private static void insertBars(DataSource dataSource, List<Bar.Column> columns, Bar... bars)
      throws SQLException {
    StringBuilder buf = new StringBuilder("INSERT INTO bar (");
    buf.append(columns.stream().map(Bar.Column::getName).collect(joining(", ")));
    buf.append(") VALUES\n");
    buf.append(
        Arrays.stream(bars)
            .map(bar -> {
              StringBuilder buf2 = new StringBuilder();
              buf2.append("  (");
              buf2.append(
                  columns.stream()
                      .map(column -> {
                        switch (column) {
                          case ID:
                            return bar.getId().map(String::valueOf).orElse("null");
                          case NAME:
                            return String.format("'%s'", bar.getName());
                          default:
                            throw new AssertionError("Unrecognized column: " + column);
                        }
                      })
                      .collect(joining(", ")));
              buf2.append(")");
              return buf2.toString();
            })
            .collect(joining(",\n")));
    buf.append(";");
    String sql = buf.toString();
    executeSql(dataSource, sql);
  }

  private static void deleteFoosAndBars(DataSource dataSource) throws SQLException {
    executeSql(dataSource, "DELETE FROM foo;\n"
        + "DELETE FROM bar;\n"
        + "ALTER SEQUENCE foo_id_seq RESTART WITH 1;\n"
        + "ALTER SEQUENCE bar_id_seq RESTART WITH 1;");
  }

  private static void resetSequences(DataSource dataSource) throws SQLException {
    executeSql(dataSource, "ALTER SEQUENCE foo_id_seq RESTART WITH 1;\n"
        + "ALTER SEQUENCE bar_id_seq RESTART WITH 1;");
  }


  private static void executeSql(DataSource dataSource, String sql)
      throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.execute();
      }
    }
  }

  private static void executeSqlQuery(DataSource dataSource, String sql, RowProcessor rowProcessor)
      throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        try (ResultSet rs = stmt.executeQuery()) {
          while (rs.next()) {
            rowProcessor.process(rs);
          }
        }
      }
    }
  }

  private static Bar createBar(int id, String name) {
    return Bar.builder()
        .withId(id)
        .withName(name)
        .build();
  }

  private static Foo createFoo(int id, String name) {
    return Foo.builder()
        .withId(id)
        .withName(name)
        .build();
  }

  private static Foo createFoo(int id, int barId, String name) {
    return Foo.builder()
        .withId(id)
        .withBarId(barId)
        .withName(name)
        .build();
  }

  private static void assertFoo(Foo actualFoo, Foo expectedFoo) {
    assertThat(actualFoo).isEqualTo(expectedFoo);
  }

  private static void assertFoos(List<Foo> actualFoos, Foo... expectedFoos) {
    assertThat(actualFoos).isNotNull();
    assertThat(actualFoos).containsExactly(expectedFoos);
  }

  private static void assertBars(List<Bar> actualBars, Bar... expectedBars) {
    assertThat(actualBars).isNotNull();
    assertThat(actualBars).containsExactly(expectedBars);
  }

  // ------------------------
  // Helper Classes
  // ------------------------

  @FunctionalInterface
  private interface RowProcessor {
    public void process(ResultSet rs) throws SQLException;
  }


  @FunctionalInterface
  private interface DatabaseFunction {
    public void execute(DataSource dataSource) throws SQLException;
  }

  @FunctionalInterface
  private interface DatabaseScriptFunction {
    public void execute(DataSource dataSource) throws SQLException, IOException;
  }

}

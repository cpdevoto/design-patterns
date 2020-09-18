# jdbc-simple

**Owner(s):** Carlos Devoto

A lightweight library create to eliminate a lot of the boilerplate code involved with coding JDBC logic.  For more details, see the docs/JdbcStatementFactory.pptx Powerpoint presentation file. 

**NOTE:** The classes contained in this library mirror the ones contained in the resolute-utils subproject of the resolute-common-utils project. I decided to copy these classes into their own library so that they could be used without pulling in a bunch of superfluous transitive dependencies.

## JDBC Simple - Cookbook

### Create an instance of JdbcStatementFactory

The first step to using the ``jdbc-simple`` library is to create an instance of ``JdbcStatementFactory`` from an instance of ``javax.sql.DataSource`` as follows:

```java
JdbcStatementFactory statementFactory = JdbcStatementFactory.getInstance(dataSource);
```

### Execute a SQL query that returns a single object

```java
Foo foo = factory.newStatement()
    .withSql("SELECT * FROM foo WHERE bar_id = ?")
    .withErrorMessage("A problem occurred while attempting to retrieve a foo.")
    .prepareStatement(stmt -> {
        stmt.setInt(1, 42);
    })
    .executeQuery(QueryHandler.toObject(rs -> {
        Foo f = Foo.builder()
            .withId(rs.getInt("id"))
            .withName(rs.getString("name"))
            .build();
        return f;
    }));

```

### Execute a SQL query that processes a single row, but does not return anything

```java
factory.newStatement()
    .withSql("SELECT * FROM foo WHERE bar_id = ?")
    .withErrorMessage("A problem occurred while attempting to retrieve a foo.")
    .prepareStatement(stmt -> {
        stmt.setInt(1, 42);
    })
    .executeQuery(QueryHandler.processObject(rs -> {
        Foo f = Foo.builder()
            .withId(rs.getInt("id"))
            .withName(rs.getString("name"))
            .build();
        System.out.println(f);
    }));

```


### Execute a SQL query that returns a list of objects

```java
List<Foo> foos = factory.newStatement()
    .withSql("SELECT * FROM foo WHERE bar_id = ?")
    .withErrorMessage("A problem occurred while attempting to retrieve foos.")
    .prepareStatement(s -> {
        s.setInt(1, 5);
    })
    .executeQuery(QueryHandler.toList(rs -> {
        Foo f = Foo.builder()
            .withId(rs.getInt("id"))
            .withName(rs.getString("name"))
            .build();
        return f;
    }));
```
### Execute a SQL query that processes a list of rows, but does not return anything

```java
factory.newStatement()
    .withSql("SELECT * FROM foo WHERE bar_id = ?")
    .withErrorMessage("A problem occurred while attempting to retrieve foos.")
    .prepareStatement(s -> {
        s.setInt(1, 5);
    })
    .executeQuery(QueryHandler.processList(rs -> {
        Foo f = Foo.builder()
            .withId(rs.getInt("id"))
            .withName(rs.getString("name"))
            .build();
        System.out.println(f);
    }));
```

### Execute a SQL statement that is not a query

```java
int result = factory.newStatement()
    .withSql("INSERT INTO foo (id, name) VALUES (?, ?)")
    .withErrorMessage("A problem occurred while attempting to insert a foo.")
    .prepareStatement(s -> {
        s.setInt(1, 1);
        s.setString(2, "My First Foo");
    })
    .execute();
System.our.println("Update Count: " + result);    
```
### Execute multiple SQL statements using the same connection

```java
    factory.newStatement()
        .withErrorMessage("A problem occurred while attempting to insert a bar.")
        .executeMultipleStatements(conn -> {

          int fooId = factory.newStatement()
              .withSql("SELECT id FROM foo WHERE name = ?")
              .prepareStatement(stmt -> {
                stmt.setString(1, "My First Foo");
              })
              .executeQueryWithConnection(conn, QueryHandler.toObject(rs -> rs.getInt("id")));

          int updateCount = factory.newStatement()
              .withSql("INSERT INTO bar (foo_id, name) VALUES (?, ?)")
              .prepareStatement(stmt -> {
                stmt.setInt(1, fooId);
                stmt.setString(2, "My First Bar");
              })
              .executeWithConnection(conn); // Must pass in the connection!
        });

```

### Execute multiple SQL statements as a single transaction
This is very similar to executing multiple statements using the same connection; the main difference is that, in this case, the statements are executed in an all-or-nothing fashion.
```java
    factory.newStatement()
        .withErrorMessage("A problem occurred while attempting to insert a bar.")
        .executeTransaction(conn -> {

          int fooId = factory.newStatement()
              .withSql("INSERT INTO foo (name) VALUES (?) RETURNING id")
              .prepareStatement(stmt -> {
                stmt.setString(1, "My First Foo");
              })
              .executeQueryWithConnection(conn, QueryHandler.toObject(rs -> rs.getInt("id")));

          int updateCount = factory.newStatement()
              .withSql("INSERT INTO bar (foo_id, name) VALUES (?, ?)")
              .prepareStatement(stmt -> {
                stmt.setInt(1, fooId);
                stmt.setString(2, "My First Bar");
              })
              .executeWithConnection(conn); // Must pass in the connection!
        });

```

### Execute multiple SQL statements in batch

```java
    List<Foo> foos = new ArrayList<>();

    foos.add(Foo.builder()
      .withId(1)
      .withName("My First Foo")
      .build();
    );

    foos.add(Foo.builder()
      .withId(2)
      .withName("My Second Foo")
      .build();
    );

    int[] result = factory.newStatement()
        .withSql("INSERT INTO foo (id, name) VALUES (?, ?)")
        .withErrorMessage("A problem occurred while attempting to insert a foo.")
        .prepareStatement(stmt -> {
            for (Foo foo: foos) {
                stmt.setInt(1, foo.getId());
                stmt.setString(2, foo.getName());
                stmt.addBatch();
            }
        })
        .executeBatch(); 
    // result contains an array of ints where each int represents the update count 
    // for a single SQL statement executed as part of the batch
```

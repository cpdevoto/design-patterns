package com.resolute.dataset.cloner.engine.script;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.sql.DataSource;

import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.utils.Constants;
import com.resolute.jdbc.simple.DataAccessException;
import com.resolute.jdbc.simple.JdbcStatementFactory;

public class ScriptOperation {
  private static final int DEFAULT_EXECUTION_THRESHOLD = 10000;


  private final JdbcStatementFactory statementFactory;
  private final File scriptFile;
  private final int executionThreshold;
  private final boolean debug;

  public static void execute(Environment env) {
    requireNonNull(env, "env cannot be null");

    ScriptOperation op = new Builder()
        .withDataSource(env.getDataSource())
        .withScriptFile(env.getOutputFile().getFile())
        .withDebug(env.getDebug())
        .build();

    try {
      op.execute();
    } catch (IOException e) {
      throw new DataAccessException(e);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private ScriptOperation(Builder builder) {
    this.statementFactory = JdbcStatementFactory.getInstance(builder.dataSource);
    this.scriptFile = builder.scriptFile;
    this.executionThreshold = builder.executionThreshold;
    this.debug = builder.debug;
  }

  public void execute() throws IOException {
    ScriptBuffer executor = new ScriptBuffer(executionThreshold, this::execute);
    try (BufferedReader in = new BufferedReader(new FileReader(scriptFile))) {
      for (String line = in.readLine(); line != null; line = in.readLine()) {
        executor.processLine(line);
      }
    }
    executor.flush();
  }

  private void execute(String sql) {
    System.out.println("Executing SQL script code...");
    if (debug) {
      System.out.println(sql);
    }
    statementFactory.newStatement()
        .withErrorMessage(
            "A problem occurred while attempting to execute the SQL script contained in file "
                + scriptFile.getAbsolutePath())
        .executeMultipleStatements(conn -> {

          statementFactory.newStatement()
              .withSql(Constants.SET_SESSION_REPLICATION_ROLE_SQL)
              .executeWithConnection(conn);

          statementFactory.newStatement()
              .withSql(sql.trim())
              .executeWithConnection(conn);
        });

  }

  public static class Builder {

    private DataSource dataSource;
    private File scriptFile;
    private int executionThreshold = DEFAULT_EXECUTION_THRESHOLD;
    private boolean debug = false;

    private Builder() {}

    public Builder withDataSource(DataSource dataSource) {
      this.dataSource = requireNonNull(dataSource, "dataSource cannot be null");
      return this;
    }

    public Builder withScriptFile(File scriptFile) {
      this.scriptFile = requireNonNull(scriptFile, "scriptFile cannot be null");
      return this;
    }

    public Builder withExecutionThreshold(int executionThreshold) {
      checkArgument(executionThreshold > 0, "expected a positive integer execution threshold");
      this.executionThreshold = executionThreshold;
      return this;
    }

    public Builder withDebug(boolean debug) {
      this.debug = debug;
      return this;
    }

    public ScriptOperation build() {
      requireNonNull(dataSource, "dataSource cannot be null");
      requireNonNull(scriptFile, "scriptFile cannot be null");
      return new ScriptOperation(this);
    }

  }

}

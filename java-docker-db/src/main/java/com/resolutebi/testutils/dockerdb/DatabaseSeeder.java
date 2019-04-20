package com.resolutebi.testutils.dockerdb;

import static com.resolutebi.testutils.dockerdb.RuleUtils.DATABASE_HOST_PROP;
import static com.resolutebi.testutils.dockerdb.RuleUtils.DATABASE_NAME_PROP;
import static com.resolutebi.testutils.dockerdb.RuleUtils.DATABASE_PASSWORD_PROP;
import static com.resolutebi.testutils.dockerdb.RuleUtils.DATABASE_PORT_PROP;
import static com.resolutebi.testutils.dockerdb.RuleUtils.DATABASE_USER_PROP;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.postgresql.ds.PGSimpleDataSource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class DatabaseSeeder implements TestRule {
  private final List<ClassResource> seedScripts;
  private final List<ClassResource> tearDownScripts;
  private final DataSource dataSource;
  private final boolean runOnce;
  private final AtomicBoolean started = new AtomicBoolean();

  public static SeedDatabaseSeedScriptBuilder builder() {
    return new Builder();
  }

  private DatabaseSeeder(Builder builder) {
    this.runOnce = builder.runOnce;
    this.seedScripts = ImmutableList.copyOf(builder.seedScripts);
    this.tearDownScripts = ImmutableList.copyOf(builder.tearDownScripts);
    try {
      this.dataSource = createDataSource();
    } catch (IOException e) {
      throw new ApplicationConfigLoadException(e);
    }
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    if (runOnce && !started.compareAndSet(false, true)) {
      return base;
    }
    return new DatabaseSeederStatement(base, this);
  }

  List<ClassResource> getSeedScripts() {
    return seedScripts;
  }

  List<ClassResource> getTearDownScripts() {
    return tearDownScripts;
  }

  private DataSource createDataSource() throws IOException {
    ApplicationConfig conf = ApplicationConfig.load();

    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setServerName(conf.expectProperty(DATABASE_HOST_PROP));
    dataSource.setPortNumber(Integer.parseInt(conf.expectProperty(DATABASE_PORT_PROP)));
    dataSource.setDatabaseName(conf.expectProperty(DATABASE_NAME_PROP));
    dataSource.setUser(conf.expectProperty(DATABASE_USER_PROP));
    dataSource.setPassword(conf.expectProperty(DATABASE_PASSWORD_PROP));
    return dataSource;
  }

  public static class Builder
      implements SeedDatabaseBuilder {
    private boolean runOnce = true;
    private List<ClassResource> seedScripts = Lists.newArrayList();
    private List<ClassResource> tearDownScripts = Lists.newArrayList();

    private Builder() {}

    public Builder runAlways() {
      this.runOnce = false;
      return this;
    }

    @Override
    public Builder withSeedScript(String fileName) {
      requireNonNull(fileName, "fileName cannot be null");
      ClassResource resource = new ClassResource(fileName);
      this.seedScripts.add(resource);
      return this;
    }

    @Override
    public Builder withSeedScript(Class<?> fileLocatorClass, String fileName) {
      requireNonNull(fileLocatorClass, "fileLocatorClass cannot be null");
      requireNonNull(fileName, "fileName cannot be null");
      ClassResource resource = new ClassResource(fileLocatorClass, fileName);
      this.seedScripts.add(resource);
      return this;
    }

    @Override
    public Builder withTearDownScript(String fileName) {
      requireNonNull(fileName, "fileName cannot be null");
      ClassResource resource = new ClassResource(fileName);
      this.tearDownScripts.add(resource);
      return this;
    }

    @Override
    public Builder withTearDownScript(Class<?> fileLocatorClass, String fileName) {
      requireNonNull(fileLocatorClass, "fileLocatorClass cannot be null");
      requireNonNull(fileName, "fileName cannot be null");
      ClassResource resource = new ClassResource(fileLocatorClass, fileName);
      this.tearDownScripts.add(resource);
      return this;
    }

    @Override
    public DatabaseSeeder build() {
      return new DatabaseSeeder(this);
    }
  }

  public interface SeedDatabaseSeedScriptBuilder {
    SeedDatabaseTearDownScriptBuilder withSeedScript(String fileName);

    SeedDatabaseTearDownScriptBuilder withSeedScript(Class<?> fileLocatorClass, String fileName);
  }

  public interface SeedDatabaseTearDownScriptBuilder extends SeedDatabaseSeedScriptBuilder {
    SeedDatabaseBuilder withTearDownScript(String fileName);

    SeedDatabaseBuilder withTearDownScript(Class<?> fileLocatorClass, String fileName);
  }

  public interface SeedDatabaseBuilder extends SeedDatabaseTearDownScriptBuilder {
    DatabaseSeeder build();
  }

}

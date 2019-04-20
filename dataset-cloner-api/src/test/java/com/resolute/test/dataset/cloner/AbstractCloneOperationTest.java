package com.resolute.test.dataset.cloner;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.rules.RuleChain;

import com.resolute.jdbc.simple.JdbcStatementFactory;
import com.resolutebi.testutils.dockerdb.DatabaseSeeder;
import com.resolutebi.testutils.dockerdb.DockerDatabase;

public abstract class AbstractCloneOperationTest {
  private static final DockerDatabase dockerDatabase = new DockerDatabase();

  @ClassRule
  public static DatabaseSeeder seeder = DatabaseSeeder.builder()
      .withSeedScript(AbstractCloneOperationTest.class, "bootstrap-data.sql")
      .withSeedScript(AbstractCloneOperationTest.class, "ad-rule-data.sql")
      .withTearDownScript(AbstractCloneOperationTest.class, "ad-rule-teardown.sql")
      .withTearDownScript(AbstractCloneOperationTest.class, "bootstrap-teardown.sql")
      .build();

  @ClassRule
  public static final RuleChain CHAIN = RuleChain
      .outerRule(dockerDatabase)
      .around(seeder);

  protected DataSource dataSource;
  protected JdbcStatementFactory statementFactory;

  @Before
  public void setUp() throws Exception {
    dataSource = seeder.getDataSource();
    statementFactory = JdbcStatementFactory.getInstance(dataSource);
  }

  @After
  public void tearDown() throws Exception {

  }


}

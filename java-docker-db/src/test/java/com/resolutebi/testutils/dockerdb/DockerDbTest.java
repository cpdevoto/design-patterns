package com.resolutebi.testutils.dockerdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.equalTo;

public class DockerDbTest {
  private static final DockerDatabase dockerDatabase = new DockerDatabase();

  private static final DatabaseSeeder seeder = DatabaseSeeder.builder()
      .withSeedScript(DockerDbTest.class, "base-data.sql")
      .withTearDownScript(DockerDbTest.class, "base-teardown.sql")
      .build();

  @ClassRule
  public static final RuleChain CHAIN = RuleChain
      .outerRule(dockerDatabase)
      .around(seeder);

  @Test
  public void test_dockerdb() throws SQLException {

    String sql = "SELECT name FROM customers WHERE id = 1";
    try (Connection conn = seeder.getDataSource().getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      assertThat(rs.next(), equalTo(true));
      assertThat(rs.getString("name"), equalTo("McLaren"));
    }
  }

}

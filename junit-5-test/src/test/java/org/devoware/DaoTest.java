package org.devoware;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.devoware.dao.Dao;
import org.devoware.dao.DataSourceBuilder;
import org.devoware.model.Distributor;
import org.devoware.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


@Testcontainers
public class DaoTest {

  private Dao dao;

  @Container
  private static GenericContainer<?> db = new GenericContainer<>(
      DockerImageName.parse("maddogtechnology-docker-develop.jfrog.io/postgres-schema:latest"))
          .withExposedPorts(5432);

  @BeforeEach
  public void setup() {
    DataSource dataSource = DataSourceBuilder.newInstance()
        .withHost(db.getHost())
        .withPort(db.getFirstMappedPort())
        .withDatabase("resolute_cloud_dev")
        .withUsername("postgres")
        .withPassword("")
        .build();
    dao = Dao.create(dataSource);
  }

  @Test
  public void test_retrieve_tags() throws SQLException {
    List<Tag> actualTags = dao.retrieveTags();

    Tag expectedTag = new Tag(43, "vav");
    assertThat(actualTags.size()).isGreaterThan(0);
    assertThat(actualTags).contains(expectedTag);

  }

  @Test
  public void test_retrieve_distributors() throws SQLException {
    List<Distributor> actualDistributors = dao.retrieveDistributors();

    Distributor expectedDistributor = new Distributor(1, "Resolute");
    assertThat(actualDistributors.size()).isGreaterThan(0);
    assertThat(actualDistributors).contains(expectedDistributor);

  }
}

package org.devoware.dao;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.devoware.model.Distributor;
import org.devoware.model.Tag;

import com.google.common.collect.Lists;

public class Dao {
  private final DataSource dataSource;

  public static Dao create(DataSource dataSource) {
    return new Dao(dataSource);
  }

  private Dao(DataSource dataSource) {
    this.dataSource = requireNonNull(dataSource, "dataSource cannot be null");
  }

  public List<Tag> retrieveTags() {
    return executeQuery("SELECT id, name FROM tag_tbl",
        rs -> new Tag(rs.getInt("id"), rs.getString("name")));
  }

  public List<Distributor> retrieveDistributors() {
    return executeQuery("SELECT id, name FROM distributor_tbl",
        rs -> new Distributor(rs.getInt("id"), rs.getString("name")));
  }

  private <T> List<T> executeQuery(String sql, RowMapper<T> rowMapper) {
    List<T> tags = Lists.newArrayList();
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        try (ResultSet rs = stmt.executeQuery()) {
          while (rs.next()) {
            T tag = rowMapper.map(rs);
            tags.add(tag);
          }
        }
      }
    } catch (SQLException e) {

    }
    return tags;

  }

  @FunctionalInterface
  private interface RowMapper<T> {
    public T map(ResultSet rs) throws SQLException;
  }

}

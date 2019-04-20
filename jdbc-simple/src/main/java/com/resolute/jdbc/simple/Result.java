package com.resolute.jdbc.simple;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Result {

  private final ResultSet rs;

  Result(ResultSet resultSet) {
    this.rs = resultSet;
  }

  public ResultSet getResultSet() {
    return rs;
  }

  public void processObject(NoReturnRowMapper mapper)
      throws SQLException {
    if (rs.next()) {
      mapper.mapRow(1, rs);
    }
  }

  public <T> T toObject(RowMapper<T> mapper) throws SQLException {
    T entity = null;
    if (rs.next()) {
      entity = mapper.mapRow(1, rs);
    }
    return entity;
  }

  public void processList(NoReturnRowMapper mapper)
      throws SQLException {
    int i = 1;
    while (rs.next()) {
      mapper.mapRow(i++, rs);
    }
  }


  public <T> List<T> toList(RowMapper<T> mapper) throws SQLException {
    List<T> entities = new ArrayList<>();
    int i = 1;
    while (rs.next()) {
      T entity = mapper.mapRow(i++, rs);
      entities.add(entity);
    }
    return entities;
  }

}

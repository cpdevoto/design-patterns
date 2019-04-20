package org.devoware.homonculus.database.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoUtils {

  public static <T> T processObject(ResultSet rs, RowMapper<T> mapper) throws SQLException {
    T entity = null;
    if (rs.next()) {
      entity = mapper.mapRow(1, rs);
    }
    return entity;
  }

  public static <T> List<T> processList(ResultSet rs, RowMapper<T> mapper) throws SQLException {
    List<T> entities = new ArrayList<>();
    int i = 1;
    while (rs.next()) {
      T entity = mapper.mapRow(i++, rs);
      entities.add(entity);
    }
    return entities;
  }

  public static <T> List<T> processList(ResultSet rs, RowMapper<T> mapper, RowFilter<T> filter)
      throws SQLException {
    List<T> entities = new ArrayList<>();
    int i = 1;
    while (rs.next()) {
      T entity = mapper.mapRow(i++, rs);
      if (filter.accept(entity)) {
        entities.add(entity);
      }
    }
    return entities;
  }

  private DaoUtils() {}

}

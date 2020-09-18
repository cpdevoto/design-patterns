package com.resolute.jdbc.simple;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SimpleRowMapper<T> {

  public T mapRow(ResultSet rs) throws SQLException;

}

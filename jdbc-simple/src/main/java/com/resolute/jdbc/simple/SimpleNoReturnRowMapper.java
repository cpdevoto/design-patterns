package com.resolute.jdbc.simple;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SimpleNoReturnRowMapper {

  public void mapRow(ResultSet rs) throws SQLException;

}

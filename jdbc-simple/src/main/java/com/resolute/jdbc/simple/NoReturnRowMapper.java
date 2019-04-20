package com.resolute.jdbc.simple;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface NoReturnRowMapper {

  public void mapRow(int rowNumber, ResultSet rs) throws SQLException;

}

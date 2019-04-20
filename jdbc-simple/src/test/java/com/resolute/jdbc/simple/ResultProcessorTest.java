package com.resolute.jdbc.simple;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.equalTo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ResultProcessorTest {
  private RowMapper<DataObject> mapper;

  @Before
  public void setup() {
    mapper = new RowMapper<DataObject>() {

      @Override
      public DataObject mapRow(int rowNumber, ResultSet rs) throws SQLException {
        return new DataObject(rowNumber, rs.getString(1));
      }

    };
  }

  @Test
  public void testProcessObject() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.next()).thenReturn(true).thenReturn(false);
    when(rs.getString(1)).thenReturn("value1");

    Result processor = new Result(rs);

    DataObject obj = processor.toObject(mapper);
    assertThat(obj, equalTo(new DataObject(1, "value1")));

    rs = mock(ResultSet.class);
    when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    when(rs.getString(1)).thenReturn("value1").thenReturn("value2");

    processor = new Result(rs);

    obj = processor.toObject(mapper);
    assertThat(obj, equalTo(new DataObject(1, "value1")));
  }

  @Test
  public void testProcessList() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    when(rs.getString(1)).thenReturn("value1").thenReturn("value2");

    Result processor = new Result(rs);

    List<DataObject> obj = processor.toList(mapper);
    assertThat(obj.size(), equalTo(2));
    assertThat(obj.get(0), equalTo(new DataObject(1, "value1")));
    assertThat(obj.get(1), equalTo(new DataObject(2, "value2")));
  }

  private static class DataObject {
    private int rowNumber;
    private String value;

    private DataObject(int rowNumber, String value) {
      super();
      this.rowNumber = rowNumber;
      this.value = value;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + rowNumber;
      result = prime * result + ((value == null) ? 0 : value.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      DataObject other = (DataObject) obj;
      if (rowNumber != other.rowNumber)
        return false;
      if (value == null) {
        if (other.value != null)
          return false;
      } else if (!value.equals(other.value))
        return false;
      return true;
    }


  }

}

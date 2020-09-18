package com.resolute.jdbc.simple;

import java.util.List;

public class QueryHandler {

  public static <T> SqlFunction<Result, List<T>> toList(RowMapper<T> mapper) {
    return result -> result.toList(mapper);
  }

  public static <T> SqlFunction<Result, T> toObject(RowMapper<T> mapper) {

    return result -> result.toObject(mapper);
  }

  public static <T> SqlConsumer<Result> processList(NoReturnRowMapper mapper) {
    return result -> result.processList(mapper);
  }

  public static <T> SqlConsumer<Result> processObject(NoReturnRowMapper mapper) {
    return result -> result.processObject(mapper);
  }
}

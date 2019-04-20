package com.resolute.dataset.cloner;

import java.util.List;

public interface Filter {

  public List<ForeignKeyFilter> getForeignKeyFilters();

  public String toSql();

}

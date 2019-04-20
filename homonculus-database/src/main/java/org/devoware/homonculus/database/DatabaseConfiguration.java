package org.devoware.homonculus.database;

import org.devoware.homonculus.core.Configuration;

public interface DatabaseConfiguration<T extends Configuration> {
  PooledDataSourceFactory getDataSourceFactory(T configuration);
}

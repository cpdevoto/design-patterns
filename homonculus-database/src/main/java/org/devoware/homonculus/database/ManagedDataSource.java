package org.devoware.homonculus.database;

import javax.sql.DataSource;

import org.devoware.homonculus.core.lifecycle.Managed;

public interface ManagedDataSource extends DataSource, Managed {

}

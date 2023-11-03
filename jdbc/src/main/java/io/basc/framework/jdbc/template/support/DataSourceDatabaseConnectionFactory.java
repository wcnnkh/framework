package io.basc.framework.jdbc.template.support;

import javax.sql.DataSource;

import io.basc.framework.jdbc.support.DataSourceConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;

public class DataSourceDatabaseConnectionFactory<D extends DataSource>
		extends DefaultDatabaseConnectionFactory<DataSourceConnectionFactory<D>> {

	public DataSourceDatabaseConnectionFactory(DataSourceConnectionFactory<D> connectionFactory,
			DatabaseDialect databaseDialect) {
		super(connectionFactory, databaseDialect);
	}

	/**
	 * 获取数据源
	 * 
	 * @see #getRawConnectionFactory()
	 * @return
	 */
	public final D getDataSource() {
		return getRawConnectionFactory().getDataSource();
	}
}

package io.basc.framework.jdbc.template.support;

import javax.sql.DataSource;

import io.basc.framework.jdbc.support.DataSourceConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DataSourceDatabaseConnectionFactory<D extends DataSource>
		extends DefaultDatabaseConnectionFactory<DataSourceConnectionFactory<D>> implements DatabaseConnectionFactory {

	public DataSourceDatabaseConnectionFactory(D dataSource, DatabaseDialect databaseDialect) {
		this(new DataSourceConnectionFactory<>(dataSource), databaseDialect);
	}

	public DataSourceDatabaseConnectionFactory(DataSourceConnectionFactory<D> dataSource,
			DatabaseDialect databaseDialect) {
		super(dataSource, databaseDialect);
	}
}

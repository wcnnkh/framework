package io.basc.framework.jdbc.template.support;

import javax.sql.DataSource;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.support.DataSourceConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DataSourceDatabaseConnectionFactory<D extends DataSource> extends DataSourceConnectionFactory<D>
		implements DatabaseConnectionFactory {
	private ConnectionFactory serverConnectionFactory;
	private DatabaseDialect databaseDialect;
	private String name;

	public DataSourceDatabaseConnectionFactory() {
		super();
	}

	public DataSourceDatabaseConnectionFactory(D dataSource) {
		super(dataSource);
	}

	@Override
	public DataSourceDatabaseConnectionFactory<D> newDatabase(String name) throws UnsupportedException {
		DataSourceDatabaseConnectionFactory<D> factory = new DataSourceDatabaseConnectionFactory<>(getDataSource());
		factory.setName(name);
		factory.setServerConnectionFactory(this.serverConnectionFactory);
		factory.setDatabaseDialect(this.databaseDialect);
		return factory;
	}

	@Override
	public String getDatabaseName() {
		return databaseDialect.getSelectedDatabaseName(operations());
	}

	@Override
	public Elements<String> getDatabaseNames() {
		return databaseDialect.getDatabaseNames(operations());
	}
}

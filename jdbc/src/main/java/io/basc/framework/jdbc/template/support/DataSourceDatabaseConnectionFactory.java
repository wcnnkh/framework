package io.basc.framework.jdbc.template.support;

import javax.sql.DataSource;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.jdbc.support.DataSourceConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.lang.UnsupportedException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DataSourceDatabaseConnectionFactory<D extends DataSource> extends DataSourceConnectionFactory<D>
		implements DatabaseConnectionFactory {
	private ConnectionFactory serverConnectionFactory;
	private String name;
	@NonNull
	private Sql queryDatabaseNameSql = QUERY_DATABASE_NAME_SQL;

	public DataSourceDatabaseConnectionFactory() {
		super();
	}

	public DataSourceDatabaseConnectionFactory(D dataSource) {
		super(dataSource);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = query(queryDatabaseNameSql, (e) -> e.getString(1)).getElements().first();
		}
		return this.name;
	}

	@Override
	public DataSourceDatabaseConnectionFactory<D> newDatabase(String name) throws UnsupportedException {
		DataSourceDatabaseConnectionFactory<D> factory = new DataSourceDatabaseConnectionFactory<>(getDataSource());
		factory.setName(name);
		factory.setServerConnectionFactory(this.serverConnectionFactory);
		factory.setQueryDatabaseNameSql(this.queryDatabaseNameSql);
		return factory;
	}
}

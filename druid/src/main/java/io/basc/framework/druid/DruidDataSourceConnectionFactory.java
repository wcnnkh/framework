package io.basc.framework.druid;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.jdbc.template.support.DataSourceDatabaseConnectionFactory;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DruidDataSourceConnectionFactory extends DataSourceDatabaseConnectionFactory<DataSource> {
	private final DruidDataSource druidDataSource;
	private ConnectionFactory serverConnectionFactory;
	private String name;
	@NonNull
	private Sql queryDatabaseNameSql = QUERY_DATABASE_NAME_SQL;

	@Override
	public Connection getConnection() throws SQLException {
		return druidDataSource.getConnection();
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = query(queryDatabaseNameSql, (e) -> e.getString(1)).getElements().first();
		}
		return this.name;
	}

	@Override
	public DatabaseConnectionFactory newDatabase(String name) throws UnsupportedException {
		Assert.requiredArgument(StringUtils.isEmpty(name), "name");
		if (serverConnectionFactory == null) {
			throw new UnsupportedException(name);
		}

		DruidDataSourceConnectionFactory factory = new DruidDataSourceConnectionFactory();
		factory.setName(name);
		factory.setServerConnectionFactory(factory);
		return factory;
	}

	@Override
	public ConnectionFactory getServerConnectionFactory() {
		// TODO Auto-generated method stub
		return null;
	}

}

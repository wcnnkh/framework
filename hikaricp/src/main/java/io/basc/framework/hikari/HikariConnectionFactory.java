package io.basc.framework.hikari;

import com.zaxxer.hikari.HikariDataSource;

import io.basc.framework.jdbc.support.DataSourceConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.jdbc.template.support.DataSourceDatabaseConnectionFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public class HikariConnectionFactory extends DataSourceDatabaseConnectionFactory<HikariDataSource> {

	public HikariConnectionFactory(HikariDataSource dataSource, DatabaseDialect databaseDialect) {
		this(new DataSourceConnectionFactory<>(dataSource), databaseDialect);
	}

	private HikariConnectionFactory(DataSourceConnectionFactory<HikariDataSource> dataSourceConnectionFactory,
			DatabaseDialect databaseDialect) {
		super(dataSourceConnectionFactory, databaseDialect);
		if (databaseDialect != null) {
			Assert.requiredArgument(StringUtils.isNotEmpty(dataSourceConnectionFactory.getDataSource().getJdbcUrl()),
					"jdbcUrl");
			setDatabaseURL(databaseDialect.resolveUrl(dataSourceConnectionFactory.getDataSource().getJdbcUrl()));
		}
	}
}

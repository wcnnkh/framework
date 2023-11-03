package io.basc.framework.druid;

import com.alibaba.druid.pool.DruidDataSource;

import io.basc.framework.jdbc.support.DataSourceConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.jdbc.template.DatabaseURL;
import io.basc.framework.jdbc.template.support.DataSourceDatabaseConnectionFactory;
import io.basc.framework.mapper.support.Copy;

public class DruidConnectionFactory extends DataSourceDatabaseConnectionFactory<DruidDataSource> {

	public DruidConnectionFactory(DruidDataSource dataSource, DatabaseDialect databaseDialect) {
		this(new DataSourceConnectionFactory<>(dataSource), databaseDialect);
	}

	private DruidConnectionFactory(DataSourceConnectionFactory<DruidDataSource> dataSourceConnectionFactory,
			DatabaseDialect databaseDialect) {
		super(dataSourceConnectionFactory, databaseDialect);
		if (databaseDialect != null) {
			setDatabaseURL(databaseDialect.resolveUrl(dataSourceConnectionFactory.getDataSource().getUrl()));
		}
	}

	@Override
	public DatabaseConnectionFactory getDatabaseConnectionFactory(String databaseName) {
		return getDatabaseConnectionFactory(databaseName, () -> {
			DatabaseURL databaseURL = getDatabaseURL();
			if (databaseURL == null) {
				return null;
			}

			databaseURL = databaseURL.clone();
			databaseURL.setDatabaseName(databaseName);

			DruidDataSource dataSource = new DruidDataSource();
			Copy.copy(getDataSource(), dataSource);
			dataSource.setUrl(databaseURL.getRawURL());
			return new DataSourceConnectionFactory<>(dataSource);
		}, (e) -> new DruidConnectionFactory(e, getDatabaseDialect()));
	}
}

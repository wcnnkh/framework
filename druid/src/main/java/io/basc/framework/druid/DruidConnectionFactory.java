package io.basc.framework.druid;

import com.alibaba.druid.pool.DruidDataSource;

import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.jdbc.template.DatabaseURL;
import io.basc.framework.jdbc.template.support.DataSourceDatabaseConnectionFactory;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.support.Copy;
import io.basc.framework.util.StringUtils;

public class DruidConnectionFactory extends DataSourceDatabaseConnectionFactory<DruidDataSource> {
	private volatile DatabaseURL databaseURL;

	public DruidConnectionFactory(DruidDataSource dataSource, DatabaseDialect databaseDialect) {
		super(dataSource, databaseDialect);
	}

	public DatabaseURL getDatabaseURL() {
		if (databaseURL == null) {
			synchronized (this) {
				if (databaseURL == null) {
					if (getDatabaseDialect() == null) {
						return null;
					}

					this.databaseURL = getDatabaseDialect().resolveUrl(getDataSource().getUrl());
				}
			}
		}
		return databaseURL;
	}

	public void setDatabaseURL(DatabaseURL databaseURL) {
		this.databaseURL = databaseURL;
		getDataSource().setUrl(databaseURL.getRawURL());
	}

	@Override
	public String getDatabaseName() {
		String databaseName = null;
		DatabaseURL databaseURL = getDatabaseURL();
		if (databaseURL != null) {
			databaseName = databaseURL.getDatabaseNmae();
		}

		if (StringUtils.isEmpty(databaseName)) {
			databaseName = super.getDatabaseName();
		}
		return databaseName;
	}

	@Override
	public DatabaseConnectionFactory newDatabaseConnectionFactory(String databaseName) throws UnsupportedException {
		DatabaseConnectionFactory connectionFactory = getDataSourceDatabaseConnectionFactory(databaseName);
		if (connectionFactory == null) {
			DatabaseURL databaseURL = getDatabaseURL();
			if (databaseURL != null) {
				databaseURL = databaseURL.clone();
				databaseURL.setDatabaseName(databaseName);
				DruidDataSource dataSource = new DruidDataSource();
				Copy.copy(getDataSource(), dataSource);
				dataSource.setUrl(databaseURL.getRawURL());
				connectionFactory = new DruidConnectionFactory(dataSource, getDatabaseDialect());
				// TODO 是否应该这样，其他实现是否也应该如此，线程不安全的行为
				registerDataSource(databaseName, dataSource);
			}
		}

		if (connectionFactory == null) {
			throw new UnsupportedException(databaseName);
		}
		return connectionFactory;
	}
}

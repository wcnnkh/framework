package io.basc.framework.druid;

import io.basc.framework.db.Configurable;
import io.basc.framework.db.DataBase;
import io.basc.framework.db.DataBaseResolver;

import com.alibaba.druid.pool.DruidDataSource;

public final class DruidUtils {
	private DruidUtils() {
	};

	public static void config(DruidDataSource dataSource, Configurable config) {
		dataSource.setDriverClassName(config.getDriverClassName());
		dataSource.setUrl(config.getUrl());
		dataSource.setUsername(config.getUsername());
		dataSource.setPassword(config.getPassword());
		if (config.getMinSize() != null) {
			dataSource.setInitialSize(config.getMinSize());
			dataSource.setMinIdle(config.getMinSize());
		}

		if (config.getMaxSize() != null) {
			dataSource.setMaxActive(config.getMaxSize());
		}
	}

	public static DataBase resolve(DruidDataSource druidDataSource, DataBaseResolver dataBaseResolver) {
		return dataBaseResolver.resolve(druidDataSource.getDriverClassName(), druidDataSource.getUrl(),
				druidDataSource.getUsername(), druidDataSource.getPassword());
	}
}

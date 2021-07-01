package scw.druid;

import com.alibaba.druid.pool.DruidDataSource;

import scw.db.Configurable;

public final class DruidUtils {
	private DruidUtils() {
	};

	public static void config(DruidDataSource dataSource, Configurable config) {
		dataSource.setDriverClassName(config.getDriverClassName());
		dataSource.setUrl(config.getUrl());
		dataSource.setUsername(config.getUsername());
		dataSource.setPassword(config.getPassword());
		dataSource.setInitialSize(config.getMinSize());
		dataSource.setMinIdle(config.getMinSize());
		dataSource.setMaxActive(config.getMaxSize());
	}
}

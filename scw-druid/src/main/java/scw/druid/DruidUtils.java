package scw.druid;

import scw.db.Configurable;
import scw.db.DataBase;
import scw.db.DataBaseResolver;

import com.alibaba.druid.pool.DruidDataSource;

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
	
	public static DataBase resolve(DruidDataSource druidDataSource, DataBaseResolver dataBaseResolver){
		return dataBaseResolver.resolve(druidDataSource.getDriverClassName(), druidDataSource.getUrl(),
				druidDataSource.getUsername(), druidDataSource.getPassword());
	}
}

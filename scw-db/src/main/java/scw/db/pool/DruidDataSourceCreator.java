package scw.db.pool;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceCreator implements DataSourceCreator {

	public DruidDataSource create(Config config) {
		DruidDataSource datasource = new DruidDataSource();
		datasource.setUrl(config.getUrl());
		datasource.setDriverClassName(config.getDirverClassName());
		datasource.setUsername(config.getUser());
		datasource.setPassword(config.getPassword());
		datasource.setTestWhileIdle(false);

		if (config.getInitialSize() > 0) {
			datasource.setInitialSize(config.getInitialSize());
		}

		if (config.getMaximumSize() > 0) {
			datasource.setMaxActive(config.getMaximumSize());
		}

		if (config.getMinimumSize() > 0) {
			datasource.setMinIdle(config.getMinimumSize());
		}
		return datasource;
	}

}

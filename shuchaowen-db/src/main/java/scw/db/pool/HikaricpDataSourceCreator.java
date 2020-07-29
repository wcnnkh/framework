package scw.db.pool;

import com.zaxxer.hikari.HikariDataSource;

public class HikaricpDataSourceCreator implements DataSourceCreator {

	public HikariDataSource create(Config config) {
		HikariDataSource hds = new HikariDataSource();
		hds.setJdbcUrl(config.getUrl());
		hds.setDriverClassName(config.getDirverClassName());
		hds.setUsername(config.getUser());
		hds.setPassword(config.getPassword());

		if (config.getMaximumSize() > 0) {
			hds.setMaximumPoolSize(config.getMaximumSize());
		}

		if (config.getMinimumSize() > 0) {
			hds.setMinimumIdle(config.getMinimumSize());
		}
		return hds;
	}

}

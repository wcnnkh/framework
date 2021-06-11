package scw.druid;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;

import scw.context.Destroy;
import scw.db.ConnectionPoolConfig;
import scw.sql.ConnectionFactory;

public class DruidConnectionFactory implements ConnectionFactory, Destroy {
	private final DruidDataSource druidDataSource;
	private final boolean canClose;

	public DruidConnectionFactory(DruidDataSource druidDataSource) {
		this.druidDataSource = druidDataSource;
		this.canClose = false;
	}

	public DruidConnectionFactory(ConnectionPoolConfig config) {
		this.canClose = true;
		this.druidDataSource = new DruidDataSource();
		druidDataSource.setDriverClassName(config.getDriverClassName());
		druidDataSource.setUrl(config.getUrl());
		druidDataSource.setUsername(config.getUsername());
		druidDataSource.setPassword(config.getPassword());
		druidDataSource.setInitialSize(config.getMinSize());
		druidDataSource.setMinIdle(config.getMinSize());
		druidDataSource.setMaxActive(config.getMaxSize());
	}

	@Override
	public Connection getConnection() throws SQLException {
		return druidDataSource.getConnection();
	}

	public DruidDataSource getDruidDataSource() {
		return druidDataSource;
	}

	@Override
	public void destroy() {
		if (canClose) {
			druidDataSource.close();
		}
	}

}

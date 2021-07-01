package scw.druid;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;

import scw.sql.ConnectionFactory;

public class DruidConnectionFactory implements ConnectionFactory {
	private final DruidDataSource druidDataSource;

	public DruidConnectionFactory(DruidDataSource druidDataSource) {
		this.druidDataSource = druidDataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return druidDataSource.getConnection();
	}

	public DruidDataSource getDruidDataSource() {
		return druidDataSource;
	}
}

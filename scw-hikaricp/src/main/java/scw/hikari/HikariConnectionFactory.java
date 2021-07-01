package scw.hikari;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import scw.context.Destroy;
import scw.db.Configurable;
import scw.sql.ConnectionFactory;

public class HikariConnectionFactory implements ConnectionFactory, Destroy {
	private final HikariDataSource dataSource;
	private final boolean canClose;

	public HikariConnectionFactory(HikariDataSource dataSource) {
		this.dataSource = dataSource;
		this.canClose = false;
	}

	public HikariConnectionFactory(HikariConfig config) {
		this.canClose = true;
		this.dataSource = new HikariDataSource(config);
	}

	public HikariConnectionFactory(Configurable config) {
		this.canClose = true;
		this.dataSource = new HikariDataSource();
		HikariUtils.config(dataSource, config);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void destroy() throws Throwable {
		if (canClose) {
			dataSource.close();
		}
	}
}

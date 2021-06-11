package scw.hikari;

import com.zaxxer.hikari.HikariConfig;

import scw.db.ConnectionPoolConfig;

public class HikariUtils {
	public static void config(HikariConfig hikariConfig, ConnectionPoolConfig connectionPoolConfig) {
		hikariConfig.setJdbcUrl(connectionPoolConfig.getUrl());
		hikariConfig.setDriverClassName(connectionPoolConfig.getDriverClassName());
		hikariConfig.setUsername(connectionPoolConfig.getUsername());
		hikariConfig.setPassword(connectionPoolConfig.getPassword());
		hikariConfig.setMinimumIdle(connectionPoolConfig.getMinSize());
		hikariConfig.setMaximumPoolSize(connectionPoolConfig.getMaxSize());
	}
}

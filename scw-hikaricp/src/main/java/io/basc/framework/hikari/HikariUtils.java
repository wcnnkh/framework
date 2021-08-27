package io.basc.framework.hikari;

import io.basc.framework.db.Configurable;
import io.basc.framework.db.DataBase;
import io.basc.framework.db.DataBaseResolver;

import com.zaxxer.hikari.HikariConfig;

public class HikariUtils {
	public static void config(HikariConfig hikariConfig, Configurable configurable) {
		hikariConfig.setJdbcUrl(configurable.getUrl());
		hikariConfig.setDriverClassName(configurable.getDriverClassName());
		hikariConfig.setUsername(configurable.getUsername());
		hikariConfig.setPassword(configurable.getPassword());
		hikariConfig.setMinimumIdle(configurable.getMinSize());
		hikariConfig.setMaximumPoolSize(configurable.getMaxSize());
	}

	public static DataBase resolveDataBase(HikariConfig config, DataBaseResolver resolver) {
		return resolver.resolve(config.getDriverClassName(), config.getJdbcUrl(), config.getUsername(),
				config.getPassword());
	}
}

package io.basc.framework.hikari;

import io.basc.framework.db.Configurable;
import io.basc.framework.db.Database;
import io.basc.framework.db.DataBaseResolver;

import com.zaxxer.hikari.HikariConfig;

public class HikariUtils {
	public static void config(HikariConfig hikariConfig, Configurable configurable) {
		hikariConfig.setJdbcUrl(configurable.getUrl());
		hikariConfig.setDriverClassName(configurable.getDriverClassName());
		hikariConfig.setUsername(configurable.getUsername());
		hikariConfig.setPassword(configurable.getPassword());
		if(configurable.getMinSize() != null) {
			hikariConfig.setMinimumIdle(configurable.getMinSize());
		}
		
		if(configurable.getMaxSize() != null) {
			hikariConfig.setMaximumPoolSize(configurable.getMaxSize());
		}
	}

	public static Database resolveDataBase(HikariConfig config, DataBaseResolver resolver) {
		return resolver.resolve(config.getDriverClassName(), config.getJdbcUrl(), config.getUsername(),
				config.getPassword());
	}
}

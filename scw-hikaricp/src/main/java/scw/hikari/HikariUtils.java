package scw.hikari;

import com.zaxxer.hikari.HikariConfig;

import scw.db.Configurable;
import scw.db.DBUtils;
import scw.db.database.DataBase;

public class HikariUtils {
	public static void config(HikariConfig hikariConfig, Configurable configurable) {
		hikariConfig.setJdbcUrl(configurable.getUrl());
		hikariConfig.setDriverClassName(configurable.getDriverClassName());
		hikariConfig.setUsername(configurable.getUsername());
		hikariConfig.setPassword(configurable.getPassword());
		hikariConfig.setMinimumIdle(configurable.getMinSize());
		hikariConfig.setMaximumPoolSize(configurable.getMaxSize());
	}

	public static DataBase resolveDataBase(HikariConfig config) {
		return DBUtils.automaticRecognition(config.getDriverClassName(), config.getJdbcUrl(), config.getUsername(),
				config.getPassword());
	}
}

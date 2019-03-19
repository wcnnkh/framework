package scw.db;

import java.util.Arrays;

import scw.common.utils.PropertiesUtils;

public final class DBUtils {
	private DBUtils() {
	};

	public static void loadProperties(Object instance, String propertiesFile) {
		PropertiesUtils.loadProperties(instance, propertiesFile,
				Arrays.asList("jdbcUrl,url,host", "username,user", "password", "minSize,initialSize,minimumIdle",
						"maxSize,maxActive,maximumPoolSize", "driver,driverClass,driverClassName"),
				true);
	}
}

package io.basc.framework.jdbc.template.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import io.basc.framework.jdbc.support.DriverManagerConnectionFactory;
import io.basc.framework.util.StringUtils;

public class DriverManagerDatabaseConnectionFactory extends DriverManagerConnectionFactory {
	private final String url;
	private final Properties info;

	public DriverManagerDatabaseConnectionFactory(String url) {
		this(url, null, null);
	}

	public DriverManagerDatabaseConnectionFactory(String url, String user, String password) {
		if (StringUtils.isEmpty(url)) {
			throw new IllegalArgumentException("The url cannot be null");
		}

		java.util.Properties info = new java.util.Properties();
		if (StringUtils.isNotEmpty(user)) {
			info.put("user", user);
		}
		if (StringUtils.isNotEmpty(password)) {
			info.put("password", password);
		}

		this.url = url;
		this.info = info;
	}

	public DriverManagerDatabaseConnectionFactory(String url, Properties info) {
		if (StringUtils.isEmpty(url)) {
			throw new IllegalArgumentException("The url cannot be null");
		}

		this.url = url;
		this.info = (Properties) (info == null ? new Properties() : info);
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, info);
	}
}

package io.basc.framework.sql;

import io.basc.framework.core.utils.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DriverManagerConnectionFactory implements ConnectionFactory {
	private final String url;
	private final Properties info;

	public DriverManagerConnectionFactory(String url) {
		this(url, null, null);
	}

	public DriverManagerConnectionFactory(String url, String user, String password) {
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

	public DriverManagerConnectionFactory(String url, Properties info) {
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

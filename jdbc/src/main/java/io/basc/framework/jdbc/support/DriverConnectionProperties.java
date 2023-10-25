package io.basc.framework.jdbc.support;

import java.util.Properties;

import io.basc.framework.util.StringUtils;

/**
 * 驱动连接属性
 */
public class DriverConnectionProperties extends Properties {
	private static final long serialVersionUID = 1L;
	public static final String USER_KEY = "user";
	public static final String PASSWORD_KEY = "password";

	public DriverConnectionProperties() {
		super();
	}

	public DriverConnectionProperties(String user, String password) {
		super();
		setUser(user);
		setPassword(password);
	}

	public DriverConnectionProperties(Properties properties) {
		super(properties);
	}

	public String getUser() {
		return getProperty(USER_KEY);
	}

	public void setUser(String user) {
		if (StringUtils.isEmpty(user)) {
			remove(USER_KEY);
		} else {
			setProperty(USER_KEY, user);
		}
	}

	public String getPassword() {
		return getProperty(PASSWORD_KEY);
	}

	public void setPassword(String password) {
		if (StringUtils.isEmpty(password)) {
			remove(PASSWORD_KEY);
		} else {
			setProperty(PASSWORD_KEY, password);
		}
	}
}

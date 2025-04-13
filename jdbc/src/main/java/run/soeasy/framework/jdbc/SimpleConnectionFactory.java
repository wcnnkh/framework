package run.soeasy.framework.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.strings.StringUtils;

@Data
public class SimpleConnectionFactory implements ConnectionFactory {
	public static final String USER_KEY = "user";
	public static final String PASSWORD_KEY = "password";

	@NonNull
	private String url;
	private Properties info;

	public SimpleConnectionFactory(@NonNull String url, String user, String password) {
		this.url = url;
		if (StringUtils.isNotEmpty(user)) {
			if (info == null) {
				info = new Properties();
			}
			info.put(USER_KEY, user);
		}

		if (StringUtils.isNotEmpty(password)) {
			if (info == null) {
				info = new Properties();
			}
			info.put(PASSWORD_KEY, password);
		}
	}

	public SimpleConnectionFactory(@NonNull String url, Properties info) {
		this.url = url;
		this.info = info;
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, info);
	}
}

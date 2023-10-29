package io.basc.framework.jdbc.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.basc.framework.jdbc.ConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 使用原始驱动创建连接
 */
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class DriverManagerConnectionFactory implements ConnectionFactory {
	@NonNull
	private String url;
	private DriverConnectionInfo info;

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, info);
	}
}

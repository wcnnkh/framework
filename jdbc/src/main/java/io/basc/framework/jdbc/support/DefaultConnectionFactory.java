package io.basc.framework.jdbc.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.basc.framework.jdbc.ConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class DefaultConnectionFactory implements ConnectionFactory {
	@NonNull
	private String url;
	private ConnectionInfo info;

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, info);
	}
}

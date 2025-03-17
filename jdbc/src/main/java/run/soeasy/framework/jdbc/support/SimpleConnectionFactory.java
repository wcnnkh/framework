package run.soeasy.framework.jdbc.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.jdbc.ConnectionFactory;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class SimpleConnectionFactory implements ConnectionFactory {
	@NonNull
	private String url;
	private ConnectionInfo info;

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, info);
	}
}

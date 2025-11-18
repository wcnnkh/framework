package run.soeasy.framework.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SqlitConnectionFactory implements ConnectionFactory {
	@NonNull
	private final String url;

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url);
	}

}

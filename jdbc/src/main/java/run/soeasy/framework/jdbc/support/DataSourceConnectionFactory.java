package run.soeasy.framework.jdbc.support;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.jdbc.ConnectionFactory;

@Data
@AllArgsConstructor
public class DataSourceConnectionFactory<D extends DataSource> implements ConnectionFactory {
	@NonNull
	private final D dataSource;

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}

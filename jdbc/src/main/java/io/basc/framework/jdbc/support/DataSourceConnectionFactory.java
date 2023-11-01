package io.basc.framework.jdbc.support;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import io.basc.framework.jdbc.ConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class DataSourceConnectionFactory<D extends DataSource> implements ConnectionFactory {
	@NonNull
	private final D dataSource;

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}

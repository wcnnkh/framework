package io.basc.framework.jdbc.support;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.util.Assert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceConnectionFactory<D extends DataSource> implements ConnectionFactory {
	@NonNull
	private D dataSource;

	public Connection getConnection() throws SQLException {
		Assert.requiredArgument(dataSource != null, "dataSource");
		return dataSource.getConnection();
	}
}

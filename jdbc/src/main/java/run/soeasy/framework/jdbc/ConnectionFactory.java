package run.soeasy.framework.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import run.soeasy.framework.core.function.Pipeline;

public interface ConnectionFactory {
	Connection getConnection() throws SQLException;

	default ConnectionWrapper createConnection() {
		return new ConnectionWrapper(Pipeline.forSupplier(this::getConnection).onClose((e) -> e.close()));
	}
}

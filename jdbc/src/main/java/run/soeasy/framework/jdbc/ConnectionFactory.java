package run.soeasy.framework.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import run.soeasy.framework.util.function.Pipeline;

public interface ConnectionFactory {
	Connection getConnection() throws SQLException;

	default ConnectionWrapper createConnection() {
		return new ConnectionWrapper(Pipeline.of(this::getConnection).onClose((e) -> e.close()).newPipeline());
	}
}

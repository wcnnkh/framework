package run.soeasy.framework.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import run.soeasy.framework.core.function.stream.Source;

public interface ConnectionFactory {
	Connection getConnection() throws SQLException;

	default ConnectionWrapper createConnection() {
		return new ConnectionWrapper(Source.of(this::getConnection).onClose((e) -> e.close()).newPipeline());
	}
}

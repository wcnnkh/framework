package io.basc.framework.sql;

import java.sql.Connection;
import java.sql.SQLException;

import io.basc.framework.util.stream.ConsumerProcessor;
import io.basc.framework.util.stream.Processor;

@FunctionalInterface
public interface ConnectionFactory {
	Connection getConnection() throws SQLException;

	default <T, E extends Throwable> T process(Processor<Connection, ? extends T, ? extends E> process)
			throws SQLException, E {
		Connection connection = null;
		try {
			connection = getConnection();
			return process.process(connection);
		} finally {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	/**
	 * @see #process(SqlProcessor)
	 * @param processor
	 * @throws SQLException
	 */
	default <E extends Throwable> void process(ConsumerProcessor<Connection, ? extends E> processor) throws SQLException, E {
		process(processor.toProcessor());
	}
}

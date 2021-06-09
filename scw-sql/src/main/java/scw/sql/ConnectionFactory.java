package scw.sql;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionFactory {
	Connection getConnection() throws SQLException;

	default <T> T process(ConnectionProcessor<T> process) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();
			return process.processConnection(connection);
		} finally {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	/**
	 * @see #process(ConnectionProcessor)
	 * @param callback
	 * @throws SQLException
	 */
	default void process(ConnectionCallback callback) throws SQLException {
		process(new ConnectionProcessor<Void>() {

			@Override
			public Void processConnection(Connection connection) throws SQLException {
				callback.doInConnection(connection);
				return null;
			}
		});
	}
}

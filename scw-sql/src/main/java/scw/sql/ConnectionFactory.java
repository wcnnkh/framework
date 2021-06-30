package scw.sql;

import java.sql.Connection;
import java.sql.SQLException;

import scw.util.stream.Callback;
import scw.util.stream.Processor;

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
	 * @param callback
	 * @throws SQLException
	 */
	default <E extends Throwable> void process(Callback<Connection, ? extends E> callback) throws SQLException, E {
		process(new Processor<Connection, Void, E>() {

			@Override
			public Void process(Connection connection) throws E {
				callback.call(connection);
				return null;
			}
		});
	}
}

package scw.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import scw.util.stream.AutoCloseStream;

@FunctionalInterface
public interface ConnectionFactory {
	Connection getConnection() throws SQLException;

	default <T> T process(SqlProcessor<Connection, T> process) throws SQLException {
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
	default void process(SqlCallback<Connection> callback) throws SQLException {
		process(new SqlProcessor<Connection, Void>() {

			@Override
			public Void process(Connection connection) throws SQLException {
				callback.call(connection);
				return null;
			}
		});
	}

	default <T> AutoCloseStream<T> streamProcess(SqlProcessor<Connection, AutoCloseStream<T>> processor,
			Supplier<String> descSupplier) throws SQLException {
		Connection connection = getConnection();
		return processor.process(connection).onClose(new Runnable() {

			@Override
			public void run() {
				try {
					if (!connection.isClosed()) {
						connection.close();
					}
				} catch (SQLException e) {
					if (descSupplier == null) {
						throw new SqlException(e);
					} else {
						throw new SqlException(descSupplier.get(), e);
					}
				}
			}
		});
	}
}

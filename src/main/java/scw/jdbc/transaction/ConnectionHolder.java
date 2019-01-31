package scw.jdbc.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import scw.jdbc.ConnectionProxy;

public class ConnectionHolder {
	/**
	 * Prefix for savepoint names.
	 */
	public static final String SAVEPOINT_NAME_PREFIX = "SAVEPOINT_";

	private Connection connection;
	private int savepointCounter;

	public ConnectionHolder(Connection connection) {
		this.connection = connection;
	}

	public Savepoint createSavePoint() throws SQLException {
		savepointCounter++;
		return connection.setSavepoint(SAVEPOINT_NAME_PREFIX + savepointCounter);
	}

	public Connection getConnection() {
		return connection;
	}

	public void close() throws SQLException {
		if (connection instanceof ConnectionProxy) {
			((ConnectionProxy) connection).getTargetConnection().close();
		} else {
			connection.close();
		}
	}
}

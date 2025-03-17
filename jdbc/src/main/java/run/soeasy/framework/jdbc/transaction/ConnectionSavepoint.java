package run.soeasy.framework.jdbc.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import run.soeasy.framework.transaction.TransactionException;

public class ConnectionSavepoint implements io.basc.framework.transaction.Savepoint {
	private Connection connection;
	private Savepoint savepoint;

	public ConnectionSavepoint(Connection connection, String name) throws TransactionException {
		this.connection = connection;
		try {
			this.savepoint = connection.setSavepoint(name);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

	public void rollback() throws TransactionException {
		try {
			connection.rollback(savepoint);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

	public void release() throws TransactionException {
		try {
			connection.releaseSavepoint(savepoint);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

}

package io.basc.framework.sql.transaction;

import io.basc.framework.transaction.Savepoint;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.transaction.TransactionException;
import io.basc.framework.transaction.TransactionResource;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractConnectionTransactionResource implements TransactionResource {
	private static final String SAVEPOINT_NAME_PREFIX = "SAVEPOINT_";
	private final TransactionDefinition transactionDefinition;
	private final boolean active;
	private int savepointCounter;

	public AbstractConnectionTransactionResource(TransactionDefinition transactionDefinition, boolean active) {
		this.transactionDefinition = transactionDefinition;
		this.active = active;
	}

	public abstract boolean hasConnection();

	public abstract Connection getConnection() throws SQLException;

	public TransactionDefinition getTransactionDefinition() {
		return transactionDefinition;
	}

	public boolean isActive() {
		return active;
	}

	public Savepoint createSavepoint() throws TransactionException {
		savepointCounter++;
		try {
			return new ConnectionSavepoint(getConnection(), SAVEPOINT_NAME_PREFIX + savepointCounter);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

	public void commit() throws Throwable {
		if (hasConnection()) {
			Connection connection = getConnection();
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
		}
	}
}

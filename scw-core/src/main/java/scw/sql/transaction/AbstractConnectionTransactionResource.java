package scw.sql.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import scw.sql.Sql;
import scw.sql.SqlUtils;
import scw.transaction.Savepoint;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.TransactionResource;

public abstract class AbstractConnectionTransactionResource implements TransactionResource {
	private static final String SAVEPOINT_NAME_PREFIX = "SAVEPOINT_";
	private final TransactionDefinition transactionDefinition;
	private final boolean active;
	private int savepointCounter;
	private LinkedHashMap<String, Sql> sqlMap;

	public AbstractConnectionTransactionResource(TransactionDefinition transactionDefinition, boolean active) {
		this.transactionDefinition = transactionDefinition;
		this.active = active;
	}

	public abstract boolean hasConnection();

	public abstract Connection getConnection() throws SQLException;

	public TransactionDefinition getTransactionDefinition() {
		return transactionDefinition;
	}

	public void addSql(Sql sql) {
		if (sql == null) {
			return;
		}

		if (sqlMap == null) {
			sqlMap = new LinkedHashMap<String, Sql>(8);
		}

		sqlMap.put(SqlUtils.getSqlId(sql), sql);
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
		if (sqlMap != null && !sqlMap.isEmpty()) {
			for (Entry<String, Sql> entry : sqlMap.entrySet()) {
				PreparedStatement preparedStatement = SqlUtils.createPreparedStatement(getConnection(),
						entry.getValue());
				try {
					preparedStatement.execute();
				} catch (SQLException e) {
					throw new TransactionException(SqlUtils.getSqlId(entry.getValue()), e);
				} finally {
					preparedStatement.close();
				}
			}
		}

		if (hasConnection()) {
			Connection connection = getConnection();
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
		}
	}
}
